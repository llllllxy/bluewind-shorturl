package com.bluewind.shorturl.module.mobile.util;

import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.consts.WxUrlConst;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.module.mobile.entity.WxCpConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxingyu01
 * @date 2022-09-05 19:57
 * @description 企业微信工具类
 **/
@Component
public class WxCpUtils {
    private final static Logger log = LoggerFactory.getLogger(WxCpUtils.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public WxCpConfig getWxConfig(String corpId, String agentId) {
        String redisKey = SystemConst.WX_CONFIG_KEY + ":" + corpId + ":" + agentId;

        WxCpConfig config = new WxCpConfig();

        // 再判断token是否存在，先从缓存中获取，缓存中取不到，再去数据库里拿
        String configInfoStr = redisTemplate.opsForValue().get(redisKey);
        config = JsonUtils.readValue(configInfoStr, WxCpConfig.class);
        if (log.isInfoEnabled()) {
            log.info("WxUtils -- getWxConfig -- redisCache -- config: {}", config);
        }
        // redisCache取不到的话，则从数据库中查一遍试试
        if (Objects.isNull(config) || Objects.isNull(config.getConfigId())) {
            String sql = "select * from s_wx_cp_config where corp_id = ? and agent_id = ? and status = '01'";
            List<WxCpConfig> list = jdbcTemplate.query(sql, new Object[]{corpId, agentId}, new BeanPropertyRowMapper<WxCpConfig>(WxCpConfig.class));
            if (CollectionUtils.isNotEmpty(list)) {
                config = list.get(0);
            }
            if (log.isInfoEnabled()) {
                log.info("WxUtils -- getWxConfig -- database -- config = {}", config);
            }
        }
        // 再判一次空，还为空说明corpId或者agentId不合法
        if (Objects.isNull(config) || Objects.isNull(config.getConfigId())) {
            return null;
        }

        // 校验AccessToken和JsapiTicket是否失效，失效就重新设置AccessToken和JsapiTicket
        config = setAccessToken(config);
        config = setJsapiTicket(config);
        // 放入缓存
        redisTemplate.opsForValue().set(redisKey, JsonUtils.writeValueAsString(config), 30, TimeUnit.DAYS);

        if (log.isInfoEnabled()) {
            log.info("WxUtils -- getWxConfig -- end -- config = {}", config);
        }
        return config;
    }


    /**
     * 获取accessToken并且翻到WxConfig中去
     * 参考文档：https://developer.work.weixin.qq.com/document/path/91039
     *
     * @param config WxConfig
     * @return WxConfig
     */
    public WxCpConfig setAccessToken(WxCpConfig config) {
        String currentTime = DateTool.getCurrentTime("yyyyMMddHHmmss");

        // 如果accessToken已超时或者根本就不存在，则重新获取accessToken
        if (Objects.isNull(config.getAccessTokenExpires())
                || currentTime.compareTo(config.getAccessTokenExpires()) > 0
                || Objects.isNull(config.getAccessToken())) {
            String url = WxUrlConst.QY_TOKEN_URL + "?corpid=" + config.getCorpId() + "&corpsecret=" + config.getSecret();
            String responseStr = restTemplate.getForObject(url, String.class);

            Map<String, Object> response = (Map<String, Object>) JsonUtils.readValue(responseStr, Map.class);

            String errcode = response.get("errcode") == null ? "" : response.get("errcode").toString();
            String errmsg = response.get("errmsg") == null ? "" : response.get("errmsg").toString();
            // errcode为0表示成功
            if ("0".equals(errcode)) {
                String accessToken = response.get("access_token") == null ? "" : response.get("access_token").toString();
                String expiresIn = response.get("expires_in") == null ? "" : response.get("expires_in").toString();
                if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(expiresIn)) {
                    config.setAccessToken(accessToken);

                    int later = Integer.parseInt(expiresIn) - 100;
                    String accessTokenExpires = getLaterTime(later);
                    config.setAccessTokenExpires(accessTokenExpires);
                }
            } else {
                // 获取access_token失败，打印出失败的日志
                if (log.isErrorEnabled()) {
                    log.error("WxUtils -- setAccessToken -- errmsg = {}", errmsg);
                }
            }
        }

        return config;
    }


    /**
     * 获取JsapiTicket并且翻到WxConfig中去
     *
     * @param config WxConfig
     * @return WxConfig
     */
    public WxCpConfig setJsapiTicket(WxCpConfig config) {
        String currentTime = DateTool.getCurrentTime("yyyyMMddHHmmss");
        if (StringUtils.isBlank(config.getAccessToken())) {
            if (log.isInfoEnabled()) {
                log.info("WxUtils -- setJsapiTicket -- accessToken不能为空");
            }
            return config;
        }

        // 如果accessToken已超时或者根本就不存在，则重新获取accessToken
        if (Objects.isNull(config.getJsapiTicketExpires())
                || currentTime.compareTo(config.getJsapiTicketExpires()) > 0
                || Objects.isNull(config.getJsapiTicket())) {
            String url = WxUrlConst.QY_TICKET_URL + "?access_token=" + config.getAccessToken();
            if (log.isInfoEnabled()) {
                log.info("WxUtils -- setJsapiTicket -- url = {}", url);
            }
            String responseStr = restTemplate.getForObject(url, String.class);

            Map<String, Object> response = (Map<String, Object>) JsonUtils.readValue(responseStr, Map.class);

            String errcode = response.get("errcode") == null ? "" : response.get("errcode").toString();
            String errmsg = response.get("errmsg") == null ? "" : response.get("errmsg").toString();

            // errcode为0表示成功
            if ("0".equals(errcode)) {
                String ticket = response.get("ticket") == null ? "" : response.get("ticket").toString();
                String expiresIn = response.get("expires_in") == null ? "" : response.get("expires_in").toString();

                if (StringUtils.isNotBlank(ticket) && StringUtils.isNotBlank(expiresIn)) {
                    config.setJsapiTicket(ticket);

                    int later = Integer.parseInt(expiresIn) - 100;
                    String jsapiTicketExpires = getLaterTime(later);
                    config.setJsapiTicketExpires(jsapiTicketExpires);
                }
            }
        }

        return config;
    }


    /**
     * 获取微信用户信息
     * <p>
     * 返回结果格式如下：
     * <pre>
     *        {
     *           "wxUserId": "",
     *           "wxDeviceId": "",
     *           "wxUserName": "",
     *           "wxMobile": "手机号",
     *        }
     *      </pre>
     *
     * @param config WxConfig
     * @param code   身份码
     * @return
     */
    public Map<String, Object> getWxUserInfo(WxCpConfig config, String code) {
        Map<String, Object> userInfo = new HashMap<>();

        // 企业微信获取用户信息
        String url = WxUrlConst.QY_USER_INFO_URL + "?access_token=" + config.getAccessToken() + "&code=" + code;
        String responseStr = restTemplate.getForObject(url, String.class);
        if (log.isInfoEnabled()) {
            log.info("WxUtils -- getWxUserInfo -- responseStr = {}", responseStr);
        }

        Map<String, Object> response = (Map<String, Object>) JsonUtils.readValue(responseStr, Map.class);
        if (MapUtils.isNotEmpty(response)) {
            String errcode = response.get("errcode") == null ? "" : response.get("errcode").toString();
            String errmsg = response.get("errmsg") == null ? "" : response.get("errmsg").toString();

            // errcode为0表示成功
            if ("0".equals(errcode)) {
                String userId = response.get("UserId") == null ? "" : response.get("UserId").toString();
                String deviceId = response.get("DeviceId") == null ? "" : response.get("DeviceId").toString();

                if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(deviceId)) {
                    userInfo.put("wxUserId", userId);
                    userInfo.put("wxDeviceId", deviceId);

                    // 再根据userId，获取userName
                    url = WxUrlConst.QY_USER_DETAIL_URL + "?access_token=" + config.getAccessToken() + "&userid=" + userId;
                    responseStr = restTemplate.getForObject(url, String.class);
                    if (log.isInfoEnabled()) {
                        log.info("WxUtils -- getWxUserInfo -- responseStr2 = {}", responseStr);
                    }
                    response = (Map<String, Object>) JsonUtils.readValue(responseStr, Map.class);
                    if (MapUtils.isNotEmpty(response)) {
                        errcode = response.get("errcode") == null ? "" : response.get("errcode").toString();
                        if ("0".equals(errcode)) {
                            userInfo.put("wxUserName", Optional.ofNullable(response.get("name")).orElse("").toString());
                            userInfo.put("wxMobile", Optional.ofNullable(response.get("telephone")).orElse("").toString());
                        }
                    }
                }
            }
        }

        return userInfo;
    }


    /**
     * 发送企业微信消息（注意：本公共方法只负责推送，消息体需要自己进行组织）
     *
     * @param config     WxConfig
     * @param jsonString 消息体，具体参考https://developer.work.weixin.qq.com/document/path/90236
     * @return boolean
     */
    public boolean sendMsg(WxCpConfig config, String jsonString) {
        String url = WxUrlConst.QY_SEND_MSG_URL + "?access_token=" + config.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        String responseStr = responseEntity.getBody();

        Map<String, Object> response = (Map<String, Object>) JsonUtils.readValue(responseStr, Map.class);

        if (MapUtils.isNotEmpty(response)) {
            String errcode = response.get("errcode") == null ? "" : response.get("errcode").toString();
            String errmsg = response.get("errmsg") == null ? "" : response.get("errmsg").toString();
            if ("0".equals(errcode)) {
                return true;
            } else {
                if (log.isErrorEnabled()) {
                    log.error("WxUtils -- sendMsg -- errmsg = {}", errmsg);
                }
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 获取若干秒后的时间字符串
     *
     * @param secondsCount 数字，单位秒
     * @return 固定格式 yyyyMMddHHmmss
     */
    public String getLaterTime(int secondsCount) {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime newTime = time.plusSeconds(secondsCount); // 增加几秒
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return dateTimeFormatter.format(newTime);
    }


    /**
     * 字符串加密
     *
     * @param decript
     * @return
     */
    public String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

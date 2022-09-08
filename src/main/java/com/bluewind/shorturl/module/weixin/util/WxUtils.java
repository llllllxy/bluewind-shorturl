package com.bluewind.shorturl.module.weixin.util;

import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.consts.WxUrlConst;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.module.weixin.entity.WxConfig;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
public class WxUtils {
    private final static Logger log = LoggerFactory.getLogger(WxUtils.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public WxConfig getWxConfig(String corpId, String agentId) {
        String redisKey = SystemConst.WX_CONFIG_KEY + ":" + corpId + ":" + agentId;

        WxConfig config = new WxConfig();

        // 再判断token是否存在，先从缓存中获取，缓存中取不到，再去数据库里拿
        String configInfoStr = redisTemplate.opsForValue().get(redisKey);
        config = JsonUtils.readValue(configInfoStr, WxConfig.class);

        if (log.isInfoEnabled()) {
            log.info("WxUtils -- getWxConfig -- config = {}", config);
        }

        if (Objects.isNull(config) || Objects.isNull(config.getConfigId())) {
            String sql = "select * from s_wx_config where corp_id = ? and agent_id = ? and status = '01'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, corpId, agentId);

            if (log.isInfoEnabled()) {
                log.info("WxUtils -- getWxConfig -- list = {}", list);
            }

            if (CollectionUtils.isNotEmpty(list)) {
                Map<String, Object> result = list.get(0);
                config = new WxConfig();
                config.setConfigId(result.get("config_id").toString());
                config.setCropId(result.get("corp_id").toString());
                config.setAgentId(result.get("agent_id").toString());
                config.setName(result.get("name").toString());
                config.setSecret(result.get("secret").toString());
                config.setStatus(result.get("status").toString());
            }
        }

        // 校验AccessToken和JsapiTicket是否失效，失效就重新设置AccessToken和JsapiTicket
        config = setAccessToken(config);
        config = setJsapiTicket(config);
        // 放入缓存
        redisTemplate.opsForValue().set(redisKey, JsonUtils.writeValueAsString(config), 30 , TimeUnit.DAYS);

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
    public WxConfig setAccessToken(WxConfig config) {
        String currentTime = DateTool.getCurrentTime("yyyyMMddHHmmss");

        // 如果accessToken已超时或者根本就不存在，则重新获取accessToken
        if (Objects.isNull(config.getAccessTokenExpires())
                || currentTime.compareTo(config.getAccessTokenExpires()) > 0
                || Objects.isNull(config.getAccessToken())) {
            String url = WxUrlConst.QY_TOKEN_URL +  "?corpid=" + config.getCropId() + "&corpsecret=" + config.getSecret();
            String responseStr = restTemplate.getForObject(url, String.class);

            Map<String, Object> response = (Map<String,Object>) JsonUtils.readValue(responseStr, Map.class);

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



    public WxConfig setJsapiTicket(WxConfig config) {
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
            String url = WxUrlConst.QY_TICKET_URL +  "?access_token=" + config.getAccessToken();
            if (log.isInfoEnabled()) {
                log.info("WxUtils -- setJsapiTicket -- url = {}", url);
            }
            String responseStr = restTemplate.getForObject(url, String.class);

            Map<String, Object> response = (Map<String,Object>) JsonUtils.readValue(responseStr, Map.class);

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
     *
     * 返回结果格式如下：
     *      <pre>
     *        {
     *           "wxUserId": "",
     *           "wxDeviceId": "",
     *           "wxUserName": "",
     *           "wxMobile": "手机号",
     *        }
     *      </pre>
     *
     * @param config WxConfig
     * @param code 身份码
     * @return
     */
    public Map<String, Object> getWxUserInfo(WxConfig config, String code) {
        Map<String, Object> userInfo = new HashMap<>();

        // 企业微信获取用户信息
        String url = WxUrlConst.QY_USER_INFO_URL + "?access_token=" + config.getAccessToken() + "&code=" + code;
        String responseStr = restTemplate.getForObject(url, String.class);
        if (log.isInfoEnabled()) {
            log.info("WxUtils -- getWxUserInfo -- responseStr = {}", responseStr);
        }

        Map<String, Object> response = (Map<String,Object>) JsonUtils.readValue(responseStr, Map.class);
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
                    response = (Map<String,Object>) JsonUtils.readValue(responseStr, Map.class);
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
     * 发送企业微信消息
     * @param config
     * @param jsonString
     * @return
     */
    public boolean sendMsg(WxConfig config, String jsonString) {
        String url = WxUrlConst.QY_SEND_MSG_URL + "?access_token=" + config.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        String responseStr = responseEntity.getBody();

        Map<String, Object> response = (Map<String,Object>) JsonUtils.readValue(responseStr, Map.class);

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
    public static String getLaterTime(int secondsCount) {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime newTime = time.plusSeconds(secondsCount); // 增加几秒
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return dateTimeFormatter.format(newTime);
    }
}

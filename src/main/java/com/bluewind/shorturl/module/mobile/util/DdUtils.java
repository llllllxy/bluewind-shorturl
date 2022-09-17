package com.bluewind.shorturl.module.mobile.util;

import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.module.mobile.entity.DdConfig;
import com.taobao.api.ApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author liuxingyu01
 * @date 2022-09-17 13:53
 * @description 钉钉工具类
 **/
@Component
public class DdUtils {
    private final static Logger log = LoggerFactory.getLogger(DdUtils.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public DdConfig getDdConfig(String appKey) {
        String redisKey = SystemConst.DD_CONFIG_KEY + ":" + appKey;

        DdConfig config = new DdConfig();
        // 再判断token是否存在，先从缓存中获取，缓存中取不到，再去数据库里拿
        String configInfoStr = redisTemplate.opsForValue().get(redisKey);
        config = JsonUtils.readValue(configInfoStr, DdConfig.class);
        if (log.isInfoEnabled()) {
            log.info("DdUtils -- getDdConfig -- redisCache -- config: {}", config);
        }
        // redisCache取不到的话，则从数据库中查一遍试试
        if (Objects.isNull(config) || Objects.isNull(config.getConfigId())) {
            String sql = "select * from s_dd_config where app_key = ? and status = '01'";
            List<DdConfig> list = jdbcTemplate.query(sql, new Object[]{appKey}, new BeanPropertyRowMapper<DdConfig>(DdConfig.class));
            if (CollectionUtils.isNotEmpty(list)) {
                config = list.get(0);
            }
            if (log.isInfoEnabled()) {
                log.info("DdUtils -- getDdConfig -- database -- config = {}", config);
            }
        }
        // 再判一次空，还为空说明appKey不合法
        if (Objects.isNull(config) || Objects.isNull(config.getConfigId())) {
            return null;
        }

        // 校验AccessToken和JsapiTicket是否失效，失效就重新设置AccessToken和JsapiTicket
        config = setAccessToken(config);
        config = setJsapiTicket(config);
        // 放入缓存
        redisTemplate.opsForValue().set(redisKey, JsonUtils.writeValueAsString(config), 30, TimeUnit.DAYS);

        if (log.isInfoEnabled()) {
            log.info("DdUtils -- getDdConfig -- end -- config = {}", config);
        }
        return config;
    }


    /**
     * 获取accessToken并且放到DdConfig中去
     *
     * @param config DdConfig
     * @return DdConfig
     */
    public DdConfig setAccessToken(DdConfig config) {
        String currentTime = DateTool.getCurrentTime("yyyyMMddHHmmss");

        // 如果accessToken已超时或者根本就不存在，则重新获取accessToken
        if (Objects.isNull(config.getAccessTokenExpires())
                || currentTime.compareTo(config.getAccessTokenExpires()) > 0
                || Objects.isNull(config.getAccessToken())) {
            try {
                DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
                OapiGettokenRequest request = new OapiGettokenRequest();
                request.setAppkey(config.getAppKey());
                request.setAppsecret(config.getAppSecret());
                request.setHttpMethod("GET");
                OapiGettokenResponse response = client.execute(request);
                if (log.isInfoEnabled()) {
                    log.info("DdUtils -- setAccessToken -- response: {}", response);
                }

                String accessToken = "";
                long expiresIn = 0;
                if (response.isSuccess()) {
                    accessToken = response.getAccessToken(); // accesstoken
                    expiresIn = response.getExpiresIn(); // token失效时间
                }
                if (StringUtils.isNotEmpty(accessToken) && expiresIn != 0) {
                    config.setAccessToken(accessToken);
                    String laterTime = getLaterTime(expiresIn - 100);
                    config.setAccessTokenExpires(laterTime);
                }
            } catch (ApiException e) {
                log.error("DdUtils -- setAccessToken -- ApiException: {e}", e);
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
    public DdConfig setJsapiTicket(DdConfig config) {
        String currentTime = DateTool.getCurrentTime("yyyyMMddHHmmss");
        if (StringUtils.isBlank(config.getAccessToken())) {
            if (log.isInfoEnabled()) {
                log.info("DdUtils -- setJsapiTicket -- accessToken不能为空");
            }
            return config;
        }

        // 如果accessToken已超时或者根本就不存在，则重新获取accessToken
        if (Objects.isNull(config.getJsapiTicketExpires())
                || currentTime.compareTo(config.getJsapiTicketExpires()) > 0
                || Objects.isNull(config.getJsapiTicket())) {
            try {
                if (log.isInfoEnabled()) {
                    log.info("DdUtils -- setJsapiTicket -- accessToken的值为：" + config.getAccessToken());
                }
                DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/get_jsapi_ticket");
                OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
                req.setTopHttpMethod("GET");
                OapiGetJsapiTicketResponse execute = client.execute(req, config.getAccessToken());
                String jsApiTicketStr = "";
                long expiresIn = 0;
                if (execute.isSuccess()) {// 返回成功了
                    jsApiTicketStr = execute.getTicket();// 获取到Ticket
                    expiresIn = execute.getExpiresIn();// 获取失效时间
                }
                if (StringUtils.isNotEmpty(jsApiTicketStr) && expiresIn != 0) {
                    config.setJsapiTicket(jsApiTicketStr);// 赋值ticket
                    String laterTime = getLaterTime(expiresIn - 100);
                    config.setJsapiTicketExpires(laterTime);
                }
            } catch (ApiException e) {
                log.error("DdUtils -- setJsapiTicket -- ApiException: {e}", e);
            }
        }

        return config;
    }


    /**
     * 获取钉钉用户信息
     *  <p>
     *  返回结果格式如下：
     *  <pre>
     *       {
     *          "ddUserId": "",
     *          "deviceId": "",
     *       }
     *  </pre>
     *
     * @param config DdConfig
     * @param code 免登码
     * @return Map
     */
    public Map<String, String> getDdUserInfo(DdConfig config, String code) {
        Map<String, String> userInfo = new HashMap<>();
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
        OapiUserGetuserinfoRequest request1 = new OapiUserGetuserinfoRequest();
        request1.setCode(code);
        request1.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("DdUtils -- getDdUserInfo -- accessToken的值为：" + config.getAccessToken());
            }
            response = client.execute(request1, config.getAccessToken());
            if (response.isSuccess()) {
                String ddUserId = response.getUserid();
                String deviceId = response.getDeviceId();
                userInfo.put("ddUserId", ddUserId);
                userInfo.put("deviceId", deviceId);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("DdUtil -- getDdUserInfo -- error -- response: {}", response);
                }
            }
        } catch (ApiException e) {
            log.error("DdUtils -- getDdUserInfo -- ApiException: {e}", e);
        }

        return userInfo;
    }


    /**
     * 获取若干秒后的时间字符串
     *
     * @param secondsCount 数字，单位秒
     * @return 固定格式 yyyyMMddHHmmss
     */
    public String getLaterTime(long secondsCount) {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime newTime = time.plusSeconds(secondsCount); // 增加几秒
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return dateTimeFormatter.format(newTime);
    }


    /**
     * 计算dd.config的签名参数
     *
     * @param jsticket  通过微应用appKey获取的jsticket
     * @param nonceStr  自定义固定字符串
     * @param timeStamp 当前时间戳
     * @param url       调用dd.config的当前页面URL
     * @return
     * @throws Exception
     */
    public String SHA256(String jsticket, String nonceStr, long timeStamp, String url) throws Exception {
        String plain = "jsapi_ticket=" + jsticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
                + "&url=" + decodeUrl(url);
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.reset();
            sha256.update(plain.getBytes(StandardCharsets.UTF_8));
            return byteToHex(sha256.digest());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    /**
     * 字节数组转化成十六进制字符串
     *
     * @param hash 字节数组
     * @return 十六进制字符串
     */
    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * 因为ios端上传递的url是encode过的，android是原始的url。开发者使用的也是原始url,
     * 所以需要把参数进行一般urlDecode
     *
     * @param url url链接
     * @return decode后的url链接
     * @throws Exception
     */
    private String decodeUrl(String url) throws Exception {
        URL urler = new URL(url);
        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append(urler.getProtocol());
        urlBuffer.append(":");
        if (urler.getAuthority() != null && urler.getAuthority().length() > 0) {
            urlBuffer.append("//");
            urlBuffer.append(urler.getAuthority());
        }
        if (urler.getPath() != null) {
            urlBuffer.append(urler.getPath());
        }
        if (urler.getQuery() != null) {
            urlBuffer.append('?');
            urlBuffer.append(URLDecoder.decode(urler.getQuery(), "utf-8"));
        }
        return urlBuffer.toString();
    }

}

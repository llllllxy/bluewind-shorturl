package com.bluewind.shorturl.module.weixin.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.common.util.SHA256Utils;
import com.bluewind.shorturl.common.util.web.CookieUtils;
import com.bluewind.shorturl.module.weixin.entity.WxConfig;
import com.bluewind.shorturl.module.weixin.service.WxServiceImpl;
import com.bluewind.shorturl.module.weixin.util.WxUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author liuxingyu01
 * @date 2022-09-06 19:15
 * @description
 **/
@RestController
@RequestMapping("/tenant/weixin")
public class WxController {
    private final static Logger log = LoggerFactory.getLogger(WxController.class);

    /**
     * 密码加密盐值
     */
    @Value("${hash.salt}")
    private String salt;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WxServiceImpl wxService;

    @Autowired
    private WxUtils wxUtils;


    @LogAround("微信用户绑定")
    @PostMapping("/bind")
    @ResponseBody
    public Result bind(@RequestParam(value = "appType") String appType,
                       @RequestParam(value = "appId") String appId,
                       @RequestParam(value = "openId") String openId,
                       @RequestParam(value = "wxUserId") String wxUserId,
                       @RequestParam(value = "agentId") String agentId,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "password") String password,
                       @RequestParam(required = false, defaultValue = "", value = "deviceId") String deviceId) {


        return Result.ok("");
    }


    @LogAround("微信用户解绑")
    @PostMapping("/unbind")
    @ResponseBody
    public Result unbind(@RequestParam(value = "appType") String appType,
                         @RequestParam(value = "appId") String appId,
                         @RequestParam(value = "openId") String openId,
                         @RequestParam(value = "wxUserId") String wxUserId,
                         @RequestParam(value = "agentId") String agentId,
                         @RequestParam(value = "deviceId") String deviceId) {


        return Result.ok("");
    }


    @LogAround("根据免登陆code获取微信用户编码")
    @PostMapping("/getWxUser")
    @ResponseBody
    public Result getWxUser(@RequestParam(value = "appType") String appType,
                            @RequestParam(value = "appId") String appId,
                            @RequestParam(value = "agentId") String agentId,
                            @RequestParam(value = "code") String code) {
        if (log.isInfoEnabled()) {
            log.info("WxUtilController getWxUser appId=" + appId + "&code=" + code + "&agentId=" + agentId + "&appType=" + appType);
        }
        WxConfig wxConfig = wxUtils.getWxConfig(appId, agentId);
        Map<String, Object> wxUserInfo = wxUtils.getWxUserInfo(wxConfig, code);
        if (log.isInfoEnabled()) {
            log.info("WxUtilController getWxUser wxUserInfo= {}", wxUserInfo);
        }

        if (MapUtils.isEmpty(wxUserInfo)) {
            return Result.error("获取微信用户失败");
        }
        return Result.ok("获取微信用户成功", wxUserInfo);
    }


    @LogAround("获取系统用户")
    @PostMapping("/getUser")
    @ResponseBody
    public Result getUser(@RequestParam(value = "appType") String appType,
                          @RequestParam(value = "appId") String appId,
                          @RequestParam(value = "agentId") String agentId,
                          @RequestParam(value = "wxUserId") String wxUserId,
                          @RequestParam(value = "deviceId") String deviceId,
                          HttpServletResponse response) {
        List<Map<String, Object>> wxUserInfoList = wxService.getWxUserMapInfo(appId, agentId, wxUserId, deviceId);

        if (CollectionUtils.isNotEmpty(wxUserInfoList)) {
            Map<String, Object> wxUserInfo = wxUserInfoList.get(0);
            String tenantAccount = wxUserInfo.get("tenant_account").toString();

            // 根据用户名查找到租户信息
            Map<String, Object> tenantInfo = wxService.getTenantInfo(tenantAccount);
            if (MapUtils.isNotEmpty(tenantInfo)) {
                log.info("IndexManageController - getUser - 获取绑定用户成功：{}！", tenantInfo);

                tenantInfo.put("tenant_password", "");
                // 数据写入redis，登陆成功
                String token = UUID.randomUUID().toString().replaceAll("-", "");
                redisTemplate.opsForValue().set(SystemConst.SYSTEM_TENANT_KEY + ":" + token, JsonUtils.writeValueAsString(tenantInfo), 1800, TimeUnit.SECONDS);

                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put(SystemConst.SYSTEM_TENANT_TOKEN, token);
                resultMap.put("userId", tenantAccount);
                resultMap.put("userName", Optional.ofNullable(tenantInfo.get("tenant_name")).orElse("").toString());

                // 将token放在cookie中
                CookieUtils.setCookie(response, SystemConst.SYSTEM_TENANT_TOKEN, token);

                return Result.ok("登录成功，欢迎回来！", resultMap);
            }
            return Result.error("获取系统绑定用户失败");
        }
        return Result.error("获取系统绑定用户失败");
    }


    @LogAround("浏览器开发登录")
    @PostMapping("/develop/login")
    @ResponseBody
    public Result login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response) {
        log.info("LoginController doLogin username = {}, password = {}", username, password);

        // 根据用户名查找到租户信息
        Map<String, Object> tenantInfo = wxService.getTenantInfo(username);

        // 没找到帐号(租户不存在)
        if (tenantInfo == null || tenantInfo.isEmpty()) {
            return Result.error("租户不存在！");
        }

        // 校验租户状态(用户已失效)
        if ("1".equals(tenantInfo.get("status").toString())) {
            return Result.error("该租户已被冻结！");
        }

        String localPassword = tenantInfo.get("tenant_password").toString();
        password = SHA256Utils.SHA256Encode(salt + password);

        if (localPassword.equals(password)) {
            log.info("IndexManageController - doLogin - {}登陆成功！", username);
            tenantInfo.put("tenant_password", "");
            // 数据写入redis，登陆成功
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            redisTemplate.opsForValue().set(SystemConst.SYSTEM_TENANT_KEY + ":" + token, JsonUtils.writeValueAsString(tenantInfo), 1800, TimeUnit.SECONDS);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(SystemConst.SYSTEM_TENANT_TOKEN, token);
            // 将token放在cookie中
            CookieUtils.setCookie(response, SystemConst.SYSTEM_TENANT_TOKEN, token);

            return Result.ok("登录成功，欢迎回来！", resultMap);
        } else {
            return Result.error("密码错误，请重新输入！");
        }
    }


    @LogAround("获取微信js-api配置参数")
    @PostMapping("/jsConfig")
    @ResponseBody
    public Result jsConfig(@RequestParam(value = "appType") String appType,
                           @RequestParam(value = "appId") String appId,
                           @RequestParam(value = "agentId") String agentId,
                           @RequestParam(required = true, defaultValue = "", value = "url") String url) {
        if (log.isDebugEnabled()) {
            log.debug("WxController -- jsConfig -- appType=" + appType + ",appId=" + appId + ",agentId=" + agentId + ",url=" + url);
        }


        return Result.ok("");
    }


    /**
     * 字符串加密
     *
     * @param decript
     * @return
     */
    private String SHA1(String decript) {
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

package com.bluewind.shorturl.module.mobile.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.config.security.TenantAuthenticeUtil;
import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.common.util.SHA256Utils;
import com.bluewind.shorturl.common.util.web.CookieUtils;
import com.bluewind.shorturl.module.mobile.service.WxCpServiceImpl;
import com.bluewind.shorturl.module.mobile.util.WxCpUtils;
import com.bluewind.shorturl.module.mobile.entity.WxCpConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author liuxingyu01
 * @date 2022-09-06 19:15
 * @description 企业微信公共请求Controller
 **/
@RestController
@RequestMapping("/mobile/wxcp")
public class WxCpController {
    private final static Logger log = LoggerFactory.getLogger(WxCpController.class);

    /**
     * 密码加密盐值
     */
    @Value("${hash.salt}")
    private String salt;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WxCpServiceImpl wxService;

    @Autowired
    private WxCpUtils wxCpUtils;

    @Autowired
    private TaskExecutor asyncServiceExecutor;


    @LogAround("微信用户绑定")
    @PostMapping("/bind")
    @ResponseBody
    public Result bind(@RequestParam(value = "appType") String appType,
                       @RequestParam(value = "appId") String appId,
                       @RequestParam(value = "wxUserId") String wxUserId,
                       @RequestParam(value = "agentId") String agentId,
                       @RequestParam(value = "username") String username,
                       @RequestParam(value = "password") String password,
                       @RequestParam(required = false, defaultValue = "", value = "deviceId") String deviceId) {
        if (log.isInfoEnabled()) {
            log.info("WxController -- bind -- appType=" + appType + ",appId=" + appId + ",agentId=" + agentId + ",wxUserId=" + wxUserId +  ",deviceId=" + deviceId);
            log.info("WxController -- bind -- username = {}, password = {}", username, password);
        }
        // 第一步：先判断用户密码对不对，不对不行
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

        if (!localPassword.equals(password)) {
            return Result.error("用户名或密码错误！");
        }
        // 第二步：如果对的话，就插入s_wx_usermap表里一条绑定信息
        log.info("WxController - bind - 获取用户成功：{}！", tenantInfo);

        int num = wxService.bind(appId, wxUserId, agentId, deviceId, username);
        if (!(num > 0)) {
            return Result.error("绑定出错！请联系系统管理员");
        }

        return Result.ok("绑定成功，欢迎您！");
    }


    @LogAround("微信用户解绑")
    @PostMapping("/unbind")
    @ResponseBody
    public Result unbind(@RequestParam(value = "appType") String appType,
                         @RequestParam(value = "appId") String appId,
                         @RequestParam(value = "wxUserId") String wxUserId,
                         @RequestParam(value = "agentId") String agentId,
                         @RequestParam(value = "deviceId") String deviceId,
                         HttpServletRequest request) {
        if (log.isInfoEnabled()) {
            log.info("WxController -- unbind -- appType=" + appType + ",appId=" + appId + ",agentId=" + agentId + ",wxUserId=" + wxUserId +  ",deviceId=" + deviceId);
        }

        // 解绑即为使s_wx_usermap表里记录失效
        int num = wxService.unbind(appId, wxUserId, agentId , deviceId);
        if (num > 0) {
            // 删除redis中缓存的会话信息
            redisTemplate.delete(SystemConst.SYSTEM_TENANT_KEY + ":" + TenantAuthenticeUtil.getToken(request));

            // 给当前用户推送一条解绑成功的消息
            // 组织消息体，并进行推送
            WxCpConfig wxCpConfig = wxCpUtils.getWxConfig(appId, agentId);
            asyncServiceExecutor.execute(() -> {
                Map<String, Object> msg = new HashMap<>();
                msg.put("touser", wxUserId);
                msg.put("agentid", agentId);
                msg.put("msgtype", "markdown");

                Map<String, Object> markdown = new HashMap<>();
                markdown.put("content", "##### 解绑成功提醒 "
                        + (char)10 + "> " + "解绑账号：" + wxUserId
                        + (char)10 + "> " + "解绑时间：" + DateTool.getNowTime("yyyy-MM-dd HH:mm:ss"));
                msg.put("markdown", markdown);

                wxCpUtils.sendMsg(wxCpConfig, JsonUtils.writeValueAsString(msg));
            });
            return Result.ok("解绑成功！");
        } else {
            return Result.error("解绑失败，请联系系统管理员");
        }
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
        WxCpConfig wxCpConfig = wxCpUtils.getWxConfig(appId, agentId);
        Map<String, Object> wxUserInfo = wxCpUtils.getWxUserInfo(wxCpConfig, code);
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
                log.info("WxController - getUser - 获取绑定用户成功：{}！", tenantInfo);

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
        log.info("WxController login username = {}, password = {}", username, password);

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
            log.info("WxController - doLogin - {}登陆成功！", username);
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





}

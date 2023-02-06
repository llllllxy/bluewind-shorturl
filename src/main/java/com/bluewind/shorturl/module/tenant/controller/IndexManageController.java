package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.AccessLimit;
import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.config.security.TenantAuthenticeUtil;
import com.bluewind.shorturl.common.config.security.TenantHolder;
import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.GenerateAkAndSk;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.common.util.SHA256Utils;
import com.bluewind.shorturl.common.util.SmsUtils;
import com.bluewind.shorturl.common.util.web.CookieUtils;
import com.bluewind.shorturl.module.tenant.service.IndexManageServiceImpl;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxingyu01
 * @date 2022-05-18 15:24
 * @description 租户控制器
 **/
@Controller
@RequestMapping("/tenant")
public class IndexManageController {
    final static Logger logger = LoggerFactory.getLogger(IndexManageController.class);

    @Autowired
    private IndexManageServiceImpl indexManageService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SmsUtils smsUtils;


    /**
     * 密码加密盐值
     */
    @Value("${hash.salt}")
    private String salt;


    @LogAround("跳转到租户后台登陆页")
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("secret", "@#@iiU70ojmY");
        return "tenant/login";
    }


    @LogAround("跳转到租户后台注册页")
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("secret", "@#@iiU70ojmY");
        return "tenant/register";
    }


    @LogAround("跳转到后台管理首页")
    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("tenantAccount", TenantHolder.getTenantAccount());
        return "tenant/index";
    }


    @LogAround("跳转到后台管理个人信息页")
    @GetMapping("/profile")
    public String profile(Model model) {
        System.out.println(TenantHolder.getTenant());

        model.addAttribute("tenantInfo", TenantHolder.getTenant());
        return "tenant/profile";
    }


    @LogAround("跳转到后台管理密钥凭证页")
    @GetMapping("/akandsk")
    public String akandsk(Model model) {
        System.out.println(TenantHolder.getTenant());

        model.addAttribute("tenantInfo", TenantHolder.getTenant());
        return "tenant/akandsk";
    }


    @LogAround("执行后台管理登录操作")
    @PostMapping("/doLogin")
    @ResponseBody
    public Result doLogin(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String captcha,
                          @RequestParam String verifyKey,
                          HttpServletResponse response) {
        logger.info("LoginController doLogin username = {}, password = {}", username, password);

        String captchaInRedis = redisTemplate.opsForValue().get(SystemConst.CAPTCHA_CODE_KEY + ":" + verifyKey);
        boolean result = captcha.equalsIgnoreCase(captchaInRedis);
        if (result) {
            // 删除掉这个redis缓存值
            redisTemplate.delete(SystemConst.CAPTCHA_CODE_KEY + ":" + verifyKey);
        } else {
            return Result.error("验证码错误，请重新输入！");
        }

        // 根据用户名查找到租户信息
        Map<String, Object> tenantInfo = indexManageService.getTenantInfo(username);

        // 没找到帐号(租户不存在)
        if (tenantInfo == null || tenantInfo.isEmpty()) {
            return Result.error("租户不存在！");
        }

        // 校验租户状态(用户已失效)
        if ("1".equals(tenantInfo.get("status").toString())) {
            return Result.error("该租户账号已被冻结！");
        }

        String localPassword = tenantInfo.get("tenant_password").toString();
        password = SHA256Utils.SHA256Encode(salt + password);

        if (StringUtils.isEmpty(localPassword) || !localPassword.equals(password)) {
            return Result.error("用户名或密码错误，请重新输入！");
        }

        logger.info("IndexManageController - doLogin - {}登录成功！", username);
        tenantInfo.put("tenant_password", "");
        // 数据写入redis，登录成功
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(SystemConst.SYSTEM_TENANT_KEY + ":" + token, JsonUtils.writeValueAsString(tenantInfo), 1800, TimeUnit.SECONDS);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(SystemConst.SYSTEM_TENANT_TOKEN, token);
        // 将token放在cookie中
        CookieUtils.setCookie(response, SystemConst.SYSTEM_TENANT_TOKEN, token);

        return Result.ok("登录成功，欢迎回来！", resultMap);
    }


    @LogAround("生成图形验证码")
    @GetMapping("/getCaptcha")
    @ResponseBody
    public Result getCode(@RequestParam(required = false, defaultValue = "", value = "type") String captchaType) {
        Map<String, String> result = new HashMap<>();

        // 保存验证码信息
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");

        String verifyKey = SystemConst.CAPTCHA_CODE_KEY + ":" + uuid;

        // 生成验证码
        if ("math".equals(captchaType)) {
            ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 38);
            captcha.setLen(3);  // 几位数运算，默认是两位
            String code = captcha.text().toLowerCase();
            // 存入redis
            redisTemplate.opsForValue().set(verifyKey, code, 60, TimeUnit.SECONDS);

            result.put("uuid", uuid);
            result.put("img", captcha.toBase64().split(",")[1]);
            return Result.ok("获取验证码成功", result);
        } else if ("char".equals(captchaType)) {
            // 设置类型，纯数字、纯字母、字母数字混合
            // gif类型的验证码
            GifCaptcha captcha = new GifCaptcha(130, 38, 4);
            captcha.setCharType(Captcha.TYPE_DEFAULT);
            String code = captcha.text().toLowerCase();
            // 存入redis
            redisTemplate.opsForValue().set(verifyKey, code, 60, TimeUnit.SECONDS);

            result.put("uuid", uuid);
            result.put("img", captcha.toBase64().split(",")[1]);
            return Result.ok("获取验证码成功", result);
        } else {
            return Result.error("请传入参数：验证码类型！");
        }
    }


    @LogAround("获取手机验证码")
    @AccessLimit(seconds = 60, maxCount = 1, msg = "60秒内只能获取一次手机验证码")
    @PostMapping("/sendSms")
    @ResponseBody
    public Result sendSms(@RequestParam(value = "tenant_phone") String tenant_phone) throws Exception {
        // 保存验证码信息
        String verifyKey = UUID.randomUUID().toString().trim().replaceAll("-", "");

        String randomCode = smsUtils.sendMessage(tenant_phone);

        if (StringUtils.isNotEmpty(randomCode)) {
            // 存入redis中，有效期10分钟
            redisTemplate.opsForValue().set(SystemConst.SMS_CODE_KEY + ":" + verifyKey, randomCode, 10, TimeUnit.MINUTES);
            Map<String, String> result = new HashMap<>();
            result.put("verifyKey", verifyKey);
            return Result.ok("获取验证码成功", result);
        } else {
            return Result.error("获取手机验证码失败，请联系系统管理页！");
        }

    }


    @LogAround("执行后台管理注册操作")
    @PostMapping("/doRegister")
    @ResponseBody
    public Result doRegister(@RequestParam String tenantAccount,
                             @RequestParam String tenantName,
                             @RequestParam String tenantPassword,
                             @RequestParam String tenantPhone,
                             @RequestParam String smsCode,
                             @RequestParam String verifyKey,
                             HttpServletResponse response) {
        logger.info("IndexManageController doRegister tenantAccount = {}", tenantAccount);

        String smsCodeInRedis = redisTemplate.opsForValue().get(SystemConst.SMS_CODE_KEY + ":" + verifyKey);
        boolean result = smsCode.equalsIgnoreCase(smsCodeInRedis);
        if (result) {
            // 删除掉这个redis缓存值
            redisTemplate.delete(SystemConst.SMS_CODE_KEY + ":" + verifyKey);
        } else {
            return Result.error("手机验证码错误，请重试！");
        }

        // 再次hash密码
        tenantPassword = SHA256Utils.SHA256Encode(salt + tenantPassword);

        // 插入到租户表中
        int num = indexManageService.addTenantInfo(tenantAccount, tenantName, tenantPassword, tenantPhone);
        if (num > 0) {
            logger.info("IndexManageController - doRegister - {}登陆成功！", tenantAccount);
            // 根据用户名查找到租户信息
            Map<String, Object> tenantInfo = indexManageService.getTenantInfo(tenantAccount);
            tenantInfo.put("tenant_password", "");

            // 会话信息数据写入redis，登陆成功
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            redisTemplate.opsForValue().set(SystemConst.SYSTEM_TENANT_KEY + ":" + token, JsonUtils.writeValueAsString(tenantInfo), 1800, TimeUnit.SECONDS);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(SystemConst.SYSTEM_TENANT_TOKEN, token);
            // 将token放在cookie中
            CookieUtils.setCookie(response, SystemConst.SYSTEM_TENANT_TOKEN, token);

            return Result.ok("注册成功，欢迎您！", resultMap);
        } else {
            return Result.error("注册失败，请联系系统管理员！");
        }
    }


    @LogAround("执行后台管理退出登录操作")
    @GetMapping("/logout")
    @ResponseBody
    public Result logout(HttpServletRequest request) {
        String token = TenantAuthenticeUtil.getToken(request);
        // 直接删除redis缓存中的会话信息
        redisTemplate.delete(SystemConst.SYSTEM_TENANT_KEY + ":" + token);
        return Result.ok("退出登录成功！", null);
    }


    @LogAround("执行修改个人信息操作")
    @PostMapping("/updateProfile")
    @ResponseBody
    public Result updateProfile(@RequestParam String tenant_id,
                                @RequestParam String tenant_account,
                                @RequestParam String tenant_name,
                                @RequestParam String tenant_email,
                                HttpServletRequest request) {
        // 更新租户表
        int num = indexManageService.updateProfile(tenant_id, tenant_name, tenant_email);
        if (num > 0) {
            // 根据用户名查找到租户信息
            Map<String, Object> tenantInfo = indexManageService.getTenantInfo(tenant_account);
            tenantInfo.put("tenant_password", "");
            // 刷新缓存的会话信息
            String token = TenantAuthenticeUtil.getToken(request);
            redisTemplate.opsForValue().set(SystemConst.SYSTEM_TENANT_KEY + ":" + token, JsonUtils.writeValueAsString(tenantInfo), 1800, TimeUnit.SECONDS);
            return Result.ok("更新个人信息成功！", null);
        } else {
            return Result.error("更新失败，请联系系统管理员！");
        }
    }


    @LogAround("重置密钥凭证操作")
    @PostMapping("/resetAkandsk")
    @ResponseBody
    public Result resetAkandsk(@RequestParam String tenant_id,
                               @RequestParam String tenant_account,
                               HttpServletRequest request) {
        // 重置accessKey和accessKeySecret
        String accessKey = GenerateAkAndSk.generateAk();
        String accessKeySecret = GenerateAkAndSk.generateSk();
        // 更新租户表
        int num = indexManageService.updateAkAndSk(tenant_id, accessKey, accessKeySecret);
        if (num > 0) {
            // 根据用户名查找到租户信息
            Map<String, Object> tenantInfo = indexManageService.getTenantInfo(tenant_account);
            tenantInfo.put("tenant_password", "");
            // 刷新缓存的会话信息
            String token = TenantAuthenticeUtil.getToken(request);
            redisTemplate.opsForValue().set(SystemConst.SYSTEM_TENANT_KEY + ":" + token, JsonUtils.writeValueAsString(tenantInfo), 1800, TimeUnit.SECONDS);

            return Result.ok("重置密钥凭证成功！", new HashMap<String, Object>() {
                {
                    put("accessKey", accessKey);
                    put("accessKeySecret", accessKeySecret);
                }
            });
        } else {
            return Result.error("重置密钥凭证失败，请联系系统管理员！");
        }
    }

}

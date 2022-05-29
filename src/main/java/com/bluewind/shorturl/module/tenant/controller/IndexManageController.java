package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.AccessLimit;
import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.SHA256Utils;
import com.bluewind.shorturl.common.util.SmsUtils;
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

import javax.servlet.http.HttpSession;
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
     * 加密盐值
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
    public String index() {
        return "tenant/index";
    }


    @LogAround("执行后台管理登陆操作")
    @PostMapping("/doLogin")
    @ResponseBody
    public Result doLogin(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String captcha,
                          @RequestParam String verifyKey,
                          HttpSession session) {
        logger.info("LoginController doLogin username = {}, password = {}", username, password);

        String captchaInRedis = redisTemplate.opsForValue().get(SystemConst.CAPTCHA_CODE_KEY + ":" +verifyKey);
        boolean result = captcha.equalsIgnoreCase(captchaInRedis);
        if (result) {
            // 删除掉这个redis缓存值
            redisTemplate.delete(SystemConst.CAPTCHA_CODE_KEY + ":" +verifyKey);
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
            return Result.error("该租户已被冻结！");
        }

        String localPassword = tenantInfo.get("tenant_password").toString();
        password = SHA256Utils.SHA256Encode(salt + password);

        if (localPassword.equals(password)) {
            logger.info("IndexManageController - doLogin - {}登陆成功！", username);
            // 数据写入session，登陆成功
            tenantInfo.put("tenant_password", "");
            session.setAttribute(SystemConst.TENANT_USER_KEY, tenantInfo);
            return Result.ok("登录成功，欢迎回来！",null);
        } else {
            return Result.error("密码错误，请重新输入！");
        }
    }


    /**
     * 生成验证码
     */
    @LogAround("生成图形验证码")
    @GetMapping("/getCaptcha")
    @ResponseBody
    public Result getCode(@RequestParam(required = false, defaultValue = "", value = "type") String captchaType) {
        Map<String,String> result = new HashMap<>();

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


    /**
     * 获取手机验证码
     */
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
            Map<String,String> result = new HashMap<>();
            result.put("verifyKey", verifyKey);
            return Result.ok("获取验证码成功", result);
        } else {
            return Result.error("获取手机验证码失败，请联系系统管理页！");
        }

    }



    @LogAround("执行后台管理退出登陆操作")
    @GetMapping("/logout")
    @ResponseBody
    public Result logout(HttpSession session) {
        // 退出系统时，清除session，invalidate()方法可以清除session对象中的所有信息。
        session.invalidate();
        return Result.ok("退出登陆成功！",null);
    }

}

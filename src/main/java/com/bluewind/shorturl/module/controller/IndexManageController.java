package com.bluewind.shorturl.module.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.SHA256Utils;
import com.bluewind.shorturl.module.service.IndexManageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 15:24
 * @description
 **/
@Controller
@RequestMapping("/index")
public class IndexManageController {
    final static Logger logger = LoggerFactory.getLogger(IndexManageController.class);

    @Autowired
    private IndexManageServiceImpl indexManageService;


    /**
     * 加密盐值
     */
    @Value("${hash.salt}")
    private String salt;


    @LogAround("跳转到后台管理登陆页")
    @GetMapping("/login")
    public String login() {
        return "manage/login";
    }


    @LogAround("跳转到后台管理首页")
    @GetMapping("/index")
    public String index() {
        return "manage/index";
    }


    @LogAround("执行后台管理登陆操作")
    @PostMapping("/doLogin")
    @ResponseBody
    public Result doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session) {
        logger.info("LoginController doLogin username = {}, password = {}", username, password);

        // 根据用户名查找到用户信息
        Map<String, Object> userInfo = indexManageService.getUserInfo(username);

        // 没找到帐号(用户不存在)
        if (userInfo == null || userInfo.isEmpty()) {
            return Result.error("账户不存在！");
        }

        // 校验用户状态(用户已失效)
        if ("1".equals(userInfo.get("status").toString())) {
            return Result.error("该账户已被冻结！");
        }

        String localPassword = userInfo.get("password").toString();
        password = SHA256Utils.SHA256Encode(salt + password);

        if (localPassword.equals(password)) {
            logger.info("IndexManageController - doLogin - {}登陆成功！", username);
            // 数据写入session
            session.setAttribute(SystemConst.SYSTEM_USER_KEY, userInfo);
            return Result.ok("登录成功，欢迎回来！",null);
        } else {
            return Result.error("密码错误，请重新输入！");
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

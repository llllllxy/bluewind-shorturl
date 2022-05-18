package com.bluewind.shorturl.module.controller;

import com.bluewind.shorturl.common.base.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author liuxingyu01
 * @date 2022-05-18 15:24
 * @description
 **/
@Controller
@RequestMapping("/manage")
public class IndexManageController {



    /**
     * 加密盐值
     */
    @Value("${hash.salt}")
    private String salt;


    @GetMapping("/login")
    public String login() {
        return "manage/login";
    }


    @GetMapping("/index")
    public String index() {
        return "manage/index";
    }


    @GetMapping("/logout")
    @ResponseBody
    public Result logout(HttpSession session) {
        // 退出系统时，清除session，invalidate()方法可以清除session对象中的所有信息。
        session.invalidate();
        return Result.ok("退出登陆成功！",null);
    }

}

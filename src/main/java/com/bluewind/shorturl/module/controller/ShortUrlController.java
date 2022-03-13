package com.bluewind.shorturl.module.controller;

import com.bluewind.shorturl.common.annotation.AccessLimit;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.util.UrlUtils;
import com.bluewind.shorturl.module.service.ShortUrlServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author liuxingyu01
 * @date 2022-03-11-16:54
 **/
@Controller
public class ShortUrlController {

    @Autowired
    private ShortUrlServiceImpl shortUrlServiceImpl;

    @Autowired
    private Environment env;


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/notFound")
    public String notFound() {
        return "not_found";
    }


    @AccessLimit(seconds = 10, maxCount = 2, msg = "10秒内只能生成两次短链接")
    @PostMapping("/generate")
    @ResponseBody
    public Result generateShortURL(@RequestParam String originalUrl) throws UnknownHostException {
        if (UrlUtils.checkURL(originalUrl)) {
            String shortURL = shortUrlServiceImpl.saveUrlMap(originalUrl);
            String host = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
                    + env.getProperty("server.port")
                    + "/";
            return Result.ok("请求成功", host + shortURL);
        }
        return Result.error("请输入正确的网址链接，注意以http://或https://开头");
    }


    @GetMapping("/{shortURL}")
    public String redirect(@PathVariable String shortURL) {
        // 根据断链，获取原始url
        String originalUrl = shortUrlServiceImpl.getOriginalUrlByShortUrl(shortURL);
        if (StringUtils.isNotBlank(originalUrl)) {
            shortUrlServiceImpl.updateUrlViews(shortURL);
            // 查询到对应的原始链接，302重定向
            return "redirect:" + originalUrl;
        }
        // 没有对应的原始链接，则直接返回首页
        return "redirect:/notFound";
    }

}

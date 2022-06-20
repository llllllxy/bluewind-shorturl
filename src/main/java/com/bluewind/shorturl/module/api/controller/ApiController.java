package com.bluewind.shorturl.module.api.controller;


import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.config.filter.ApiFilterHolder;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.UrlUtils;
import com.bluewind.shorturl.module.api.service.ApiServiceImpl;
import com.bluewind.shorturl.module.portal.service.ShortUrlServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api")
public class ApiController {
    final static Logger log = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private ApiServiceImpl apiService;

    @Autowired
    private ShortUrlServiceImpl shortUrlServiceImpl;


    @Autowired
    private Environment env;

    @LogAround("执行后台管理登录操作")
    @GetMapping("/test")
    @ResponseBody
    public Result test() {
        return Result.ok("测试调用成功: " + ApiFilterHolder.getTenantId());
    }

    @LogAround("API调用短链生成，GET/POST均支持")
    @RequestMapping (value = "/generate" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Result generate(@RequestParam String originalUrl,
                           @RequestParam(required = false, defaultValue = "20991231235959", value = "expireDate") String expireDate) throws UnknownHostException, UnsupportedEncodingException {
        if (!DateTool.checkFormat(expireDate, "yyyyMMddHHmmss")) {
            return Result.error("参数expireDate请传入正确的时间格式");
        }
        originalUrl = URLDecoder.decode(originalUrl, "utf-8");
        if (!UrlUtils.checkURL(originalUrl)) {
            return Result.error("请输入正确的网址链接，注意以http://或https://开头");
        }

        String tenantId = ApiFilterHolder.getTenantId();
        String shortURL = shortUrlServiceImpl.generateUrlMap(originalUrl, expireDate, tenantId);
        String host = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
                + env.getProperty("server.port")
                + "/";
        return Result.ok("请求成功", host + shortURL);
    }

}

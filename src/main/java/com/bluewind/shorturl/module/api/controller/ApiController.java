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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("/api")
public class ApiController {
    final static Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private ApiServiceImpl apiService;

    @Autowired
    private ShortUrlServiceImpl shortUrlServiceImpl;

    @Autowired
    private Environment env;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
                           @RequestParam(required = false, defaultValue = "0", value = "status") String status,
                           @RequestParam(required = false, defaultValue = "20991231235959", value = "expireDate") String expireDate) throws UnsupportedEncodingException {
        if (!DateTool.checkFormat(expireDate, "yyyyMMddHHmmss")) {
            return Result.error("参数expireDate请传入正确的时间格式");
        }
        if (!"0".equals(status) && !"1".equals(status)) {
            return Result.error("参数status请传入正确的格式");
        }

        originalUrl = URLDecoder.decode(originalUrl, "utf-8");
        if (!UrlUtils.checkURL(originalUrl)) {
            return Result.error("请输入正确的网址链接，注意以http://或https://开头");
        }

        String tenantId = ApiFilterHolder.getTenantId();
        String shortURL = shortUrlServiceImpl.generateUrlMap(originalUrl, expireDate, tenantId, status, "租户API调用生成");
        String host = env.getProperty("bluewind.inet-address");
        return Result.ok("请求成功", host + shortURL);
    }


    @LogAround("API调用启用短链接")
    @RequestMapping (value = "/enable" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Result enable(@RequestParam String shortUrl) {
        String tenantId = ApiFilterHolder.getTenantId();
        int num = apiService.enable(shortUrl, tenantId);
        if (num > 0) {
            return Result.ok("启用短链" + shortUrl + "成功！" );
        }
        return Result.error("启用短链" + shortUrl + "失败，不存在此短链！");
    }


    @LogAround("API调用禁用短链接")
    @RequestMapping (value = "/disable" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Result disable(@RequestParam String shortUrl) {
        String tenantId = ApiFilterHolder.getTenantId();
        int num = apiService.disable(shortUrl, tenantId);
        if (num > 0) {
            return Result.ok("禁止短链" + shortUrl + "成功！");
        }
        return Result.error("禁止短链" + shortUrl + "失败，不存在此短链！");
    }

    @LogAround("API调用更改短链失效时间")
    @RequestMapping (value = "/expire" , method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Result expire(@RequestParam String shortUrl,
                         @RequestParam String expireDate) {
        String tenantId = ApiFilterHolder.getTenantId();
        int num = apiService.expire(shortUrl, expireDate, tenantId);
        if (num > 0) {
            // 删除redis里的缓存
            try {
                redisTemplate.delete(shortUrl);
            } catch (Exception e) {
                logger.error("ApiController -- expire -- Exception= {e}", e);
            }
            return Result.ok("更改短链失效时间" + shortUrl + "成功，更新后为：" + expireDate + "");
        }
        return Result.error("更改短链失效时间" + shortUrl + "失败，不存在此短链！");
    }

}

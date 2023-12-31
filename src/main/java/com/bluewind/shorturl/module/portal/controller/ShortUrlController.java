package com.bluewind.shorturl.module.portal.controller;

import com.bluewind.shorturl.common.annotation.AccessLimit;
import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.UrlUtils;
import com.bluewind.shorturl.module.portal.service.ShortUrlServiceImpl;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author liuxingyu01
 * @date 2022-03-11-16:54
 * @description 短链主页门户平台控制器
 **/
@Controller
public class ShortUrlController {
    final static Logger log = LoggerFactory.getLogger(ShortUrlController.class);

    @Autowired
    private ShortUrlServiceImpl shortUrlServiceImpl;

    @Autowired
    private Environment env;


    @LogAround("门户首页")
    @GetMapping("/")
    public String index() {
        return "portal/index";
    }

    @LogAround("API接口首页")
    @GetMapping("/apiDoc")
    public String apiDoc() {
        return "portal/api_doc";
    }


    @LogAround("关于我们页")
    @GetMapping("/about")
    public String about() {
        return "portal/about";
    }

    @LogAround("门户短链404页")
    @GetMapping("/notFound")
    public String notFound() {
        return "portal/not_found";
    }


    @LogAround("门户短链过期页")
    @GetMapping("/expirePage")
    public String expirePage() {
        return "portal/expire_page";
    }


    @LogAround("门户短链生成，10秒内只能生成两次短链接，门户免费的，肯定不允许一直免费生成")
    @AccessLimit(seconds = 10, maxCount = 2, msg = "免费用户10秒内只能生成两次短链接")
    @PostMapping("/generate")
    @ResponseBody
    public Result generate(@RequestParam(value = "originalUrl") String originalUrl,
                           @RequestParam(value = "tenantId") String tenantId,
                           @RequestParam(required = false, defaultValue = "sevenday", value = "validityPeriod") String validityPeriod) {
        if (StringUtils.isEmpty(tenantId)) {
            return Result.error("租户ID不能为空，请检查请求参数");
        }
        if (UrlUtils.checkURL(originalUrl)) {
            String expireDate = "";
            String currentTime = DateTool.getCurrentTime("HHmmss");
            // 构建过期时间
            if ("sevenday".equals(validityPeriod)) {
                expireDate = DateTool.nextWeek() + currentTime;
            }
            if ("threemonth".equals(validityPeriod)) {
                expireDate = DateTool.plusMonth(3) + currentTime;
            }
            if ("halfyear".equals(validityPeriod)) {
                expireDate = DateTool.plusMonth(6) + currentTime;
            }
            if ("forever".equals(validityPeriod)) {
                expireDate = "20991231235959";
            }
            if (log.isInfoEnabled()) {
                log.info("ShortUrlController -- generate -- expireDate = {}", expireDate);
            }

            String shortURL = shortUrlServiceImpl.generateUrlMap(originalUrl, expireDate, tenantId, "0", "门户平台生成");
            String host = env.getProperty("bluewind.inet-address");

            return Result.ok("请求成功", host + shortURL);
        }
        return Result.error("请输入正确的网址链接，注意以http://或https://开头");
    }


    @LogAround("短链解析转发")
    @GetMapping("/{shortURL}")
    public String redirect(@PathVariable String shortURL, HttpServletRequest request) {
        // 根据断链，获取原始url
        Map<String, String> urlDataMap = shortUrlServiceImpl.getOriginalUrlByShortUrl(shortURL);

        // 没有对应的原始链接，则直接返回404页
        if (Objects.isNull(urlDataMap) || urlDataMap.isEmpty()) {
            return "redirect:/notFound";
        }

        String originalURL = Optional.ofNullable(urlDataMap.get("originalURL")).orElse("");
        String expireDate = Optional.ofNullable(urlDataMap.get("expireDate")).orElse("");
        String tenantId = Optional.ofNullable(urlDataMap.get("tenantId")).orElse("");
        String nowTime = DateTool.getCurrentTime("yyyyMMddHHmmss");

        // 取不到expireDate，也算短链过期了，则直接返回过期页面
        if (StringUtils.isBlank(expireDate)) {
            return "redirect:/expirePage";
        }

        // 有效期小于今天，说明过期了，拉倒了，不让访问，则直接返回过期页面
        if (expireDate.compareTo(nowTime) < 0) {
            return "redirect:/expirePage";
        }

        // 查询到对应的原始链接，先记录访问日志，然后302重定向到原地址
        shortUrlServiceImpl.updateUrlViews(request, shortURL, tenantId);
        return "redirect:" + originalURL;
    }


    @LogAround("Jfinal-activeRecord测试")
    @GetMapping("/activeRecordTest")
    @ResponseBody
    public Object activeRecordTest() {
        // 测试直接查sql
        List<Record> applyList = Db.find("select * from s_url_map");
        if (log.isInfoEnabled()) {
            log.info("ShortUrlController -- generateShortURL -- applyList = {}", applyList);
        }
        // 测试通过SqlPara查sql
        SqlPara sqlPara = Db.getSqlPara("access_log.getLogList", new HashMap());
        List<Record> accessLogList = Db.find(sqlPara);
        if (log.isInfoEnabled()) {
            log.info("ShortUrlController -- generateShortURL -- accessLogList = {}", accessLogList);
        }

        return Result.ok("测试成功", accessLogList);
    }

}

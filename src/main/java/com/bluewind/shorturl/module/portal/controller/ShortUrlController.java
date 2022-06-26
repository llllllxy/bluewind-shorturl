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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @AccessLimit(seconds = 10, maxCount = 2, msg = "10秒内只能生成两次短链接")
    @PostMapping("/generate")
    @ResponseBody
    public Result generate(@RequestParam(value = "originalUrl") String originalUrl,
                           @RequestParam(value = "tenantId") String tenantId,
                           @RequestParam(required = false, defaultValue = "sevenday", value = "validityPeriod") String validityPeriod) throws UnknownHostException {
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
            String host = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
                    + env.getProperty("server.port")
                    + "/";
            return Result.ok("请求成功", host + shortURL);
        }
        return Result.error("请输入正确的网址链接，注意以http://或https://开头");
    }


    @LogAround("短链解析转发")
    @GetMapping("/{shortURL}")
    public String redirect(@PathVariable String shortURL, HttpServletRequest request) {
        // 根据断链，获取原始url
        Map<String, String> urlDataMap = shortUrlServiceImpl.getOriginalUrlByShortUrl(shortURL);
        if (urlDataMap != null && !urlDataMap.isEmpty()) {
            String originalURL = urlDataMap.get("originalURL") == null ? "" : urlDataMap.get("originalURL");
            String expireDate = urlDataMap.get("expireDate") == null ? "" : urlDataMap.get("expireDate");
            String tenantId = urlDataMap.get("tenantId") == null ? "" : urlDataMap.get("tenantId");

            String nowTime = DateTool.getCurrentTime("yyyyMMddHHmmss");

            if (StringUtils.isNotBlank(expireDate)) {
                // 有效期小于今天，说明过期了，拉倒了，不让访问
                if (expireDate.compareTo(nowTime) < 0) {
                    // 短链过期了，则直接返回过期页面
                    return "redirect:/expirePage";
                } else {
                    shortUrlServiceImpl.updateUrlViews(request, shortURL, tenantId);
                    // 查询到对应的原始链接，302重定向
                    return "redirect:" + originalURL;
                }
            } else {
                // 取不到时间，也算短链过期了，则直接返回过期页面
                return "redirect:/expirePage";
            }
        } else {
            // 没有对应的原始链接，则直接返回404页
            return "redirect:/notFound";
        }
    }


    @LogAround("Jfinal-activeRecord测试")
    @GetMapping("/activeRecordTest")
    @ResponseBody
    public Object activeRecordTest() {
        List<Record> applyList = Db.find("select * from s_url_map");
        if (log.isInfoEnabled()) {
            log.info("ShortUrlController -- generateShortURL -- applyList = {}", applyList);
        }

        SqlPara sqlPara = Db.getSqlPara("access_log.getLogList", new HashMap());
        List<Record> accessLogList = Db.find(sqlPara);
        if (log.isInfoEnabled()) {
            log.info("ShortUrlController -- generateShortURL -- accessLogList = {}", accessLogList);
        }

        return Result.ok("测试成功", accessLogList);
    }

}

package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.BaseController;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.config.security.TenantHolder;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.portal.service.ShortUrlServiceImpl;
import com.bluewind.shorturl.module.tenant.service.UrlManageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:27
 * @description 短链管理控制器
 **/
@Controller
@RequestMapping("/tenant/url")
public class UrlManageController extends BaseController {
    final static Logger logger = LoggerFactory.getLogger(UrlManageController.class);


    @Autowired
    private UrlManageServiceImpl urlManageService;

    @Autowired
    private ShortUrlServiceImpl shortUrlServiceImpl;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @LogAround("跳转到短链列表查询页")
    @GetMapping("/index")
    public String index(Model model) {
        return "tenant/url/index";
    }

    @LogAround("短链列表分页查询")
    @ResponseBody
    @PostMapping(value="/list")
    public Result list(@RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNumber") Integer pageNumber,
                       @RequestParam(required = false, defaultValue = "", value = "surl") String surl,
                       @RequestParam(required = false, defaultValue = "", value = "createdAt") String createdAt,
                       @RequestParam(required = false, defaultValue = "", value = "status") String status,
                       @RequestParam("sortName") String sortName,
                       @RequestParam("sortOrder") String sortOrder) {
        Page page = urlManageService.getPage(pageSize, pageNumber, surl, createdAt, status, sortName, sortOrder);

        Map<String, Object> result = new HashMap<>();
        result.put(RESULT_ROWS, page.getRecords());
        result.put(RESULT_TOTAL, page.getTotal());
        return Result.ok("获取短链列表成功！", result);
    }


    @LogAround("跳转到短链新增页")
    @GetMapping("/addPage")
    public String addPage(Model model) {
        return "tenant/url/add";
    }


    @LogAround("短链新增")
    @ResponseBody
    @PostMapping(value="/add")
    public Result add(@RequestParam(required = false, defaultValue = "", value = "note") String note,
                       @RequestParam("lurl") String lurl,
                       @RequestParam("expireDate") String expireDate,
                       @RequestParam("status") String status) {
        String tenantId = TenantHolder.getTenantId();
        // 日期格式转换
        expireDate = DateTool.dateFormat(expireDate, "yyyy-MM-dd", "yyyyMMdd");
        expireDate = expireDate + "235959";
        // 生成短链
        String shortURL = shortUrlServiceImpl.generateUrlMap(lurl, expireDate, tenantId, status, note);
        return Result.ok("新增短链成功！");
    }



    @LogAround("跳转到短链修改页")
    @GetMapping("/editPage/{id}")
    public String editPage(Model model, @PathVariable String id) {
        Map<String, Object> urlInfo = urlManageService.findById(id);
        urlInfo.put("expire_time", DateTool.timeFormat(urlInfo.get("expire_time").toString(), "yyyyMMddHHmmss", "yyyy-MM-dd"));
        model.addAttribute("urlInfo", urlInfo);

        return "tenant/url/edit";
    }


    @LogAround("短链修改")
    @ResponseBody
    @PostMapping(value="/edit")
    public Result edit(@RequestParam(required = false, defaultValue = "", value = "note") String note,
                       @RequestParam("id") String id,
                       @RequestParam("surl") String surl,
                       @RequestParam("expireDate") String expireDate,
                       @RequestParam("status") String status) {
        // 日期格式转换
        expireDate = DateTool.dateFormat(expireDate, "yyyy-MM-dd", "yyyyMMdd");
        expireDate = expireDate + "235959";
        // 修改短链
        int num = urlManageService.edit(id, status, expireDate, note);
        if (num > 0) {
            // 删除redis里的缓存
            try {
                redisTemplate.delete(surl);
            } catch (Exception e) {
                logger.error("UrlManageController -- edit -- Exception= {e}", e);
            }
            return Result.ok("修改短链【" + id + "】成功");
        }
        return Result.error("修改短链【" + id + "】失败");
    }


    @LogAround("短链删除")
    @ResponseBody
    @GetMapping(value="/del/{id}")
    public Result del(@PathVariable String id) {
        int num = urlManageService.del(id);
        if (num > 0) {
            return Result.ok("删除短链【" + id + "】成功");
        }
        return Result.error("删除短链【" + id + "】失败");
    }


    @LogAround("短链批量删除")
    @ResponseBody
    @PostMapping(value="/batchDel")
    public Result batchDel(@RequestParam String idlistStr) {
        int num = urlManageService.batchDel(idlistStr);
        if (num > 0) {
            return Result.ok("批量删除短链【" + idlistStr + "】成功");
        }
        return Result.error("批量删除短链【" + idlistStr + "】失败");
    }


    @LogAround("短链批量启用")
    @ResponseBody
    @PostMapping(value="/enable")
    public Result enable(@RequestParam String idlistStr) {
        int num = urlManageService.enable(idlistStr);
        if (num > 0) {
            return Result.ok("批量启用短链【" + idlistStr + "】成功");
        }
        return Result.error("批量启用短链【" + idlistStr + "】失败");
    }



    @LogAround("短链批量禁用")
    @ResponseBody
    @PostMapping(value="/disable")
    public Result disable(@RequestParam String idlistStr) {
        int num = urlManageService.disable(idlistStr);
        if (num > 0) {
            return Result.ok("批量禁用短链【" + idlistStr + "】成功");
        }
        return Result.error("批量禁用短链【" + idlistStr + "】失败");
    }
}

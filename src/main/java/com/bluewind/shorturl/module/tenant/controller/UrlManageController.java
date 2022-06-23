package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.BaseController;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.tenant.service.UrlManageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UrlManageServiceImpl urlManageService;


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


    @LogAround("短链删除")
    @ResponseBody
    @GetMapping(value="/del/{id}")
    public Result del(@PathVariable String id) {
        int num = urlManageService.del(id);
        if (num > 0) {
            return Result.ok("删除短链失效时间【" + id + "】成功");
        }
        return Result.error("删除短链失效时间【" + id + "】失败");
    }


    @LogAround("短链批量删除")
    @ResponseBody
    @PostMapping(value="/batchDel")
    public Result batchDel(@RequestParam String idlistStr) {
        int num = urlManageService.batchDel(idlistStr);
        if (num > 0) {
            return Result.ok("批量删除短链失效时间【" + idlistStr + "】成功");
        }
        return Result.error("批量短链失效时间【" + idlistStr + "】失败");
    }
}

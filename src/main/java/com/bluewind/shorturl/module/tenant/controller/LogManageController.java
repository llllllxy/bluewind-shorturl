package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.BaseController;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.ExcelPoiUtil;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.tenant.service.LogManageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:27
 * @description 短链访问日志管理控制器
 **/
@Controller
@RequestMapping("/tenant/log")
public class LogManageController extends BaseController {


    @Autowired
    private LogManageServiceImpl logManageService;

    @LogAround("跳转到访问日志首页")
    @GetMapping("/index")
    public String index(Model model) {
        return "tenant/log/index";
    }


    @LogAround("访问日志列表分页查询")
    @ResponseBody
    @PostMapping(value = "/list")
    public Result list(@RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNumber") Integer pageNumber,
                       @RequestParam(required = false, defaultValue = "", value = "surl") String surl,
                       @RequestParam(required = false, defaultValue = "", value = "startTime") String startTime,
                       @RequestParam(required = false, defaultValue = "", value = "endTime") String endTime,
                       @RequestParam(required = false, defaultValue = "", value = "accessIp") String accessIp,
                       @RequestParam("sortName") String sortName,
                       @RequestParam("sortOrder") String sortOrder) {
        Page page = logManageService.getPage(pageSize, pageNumber, surl, startTime, endTime, accessIp, sortName, sortOrder);

        Map<String, Object> result = new HashMap<>();
        result.put(RESULT_ROWS, page.getRecords());
        result.put(RESULT_TOTAL, page.getTotal());
        return Result.ok("获取短链列表成功！", result);
    }

    @LogAround("跳转到访问日志明细页")
    @GetMapping("/detail/{logId}")
    public String detail(Model model, @PathVariable String logId) {
        Map<String, Object> logInfo = logManageService.findByLogId(logId);
        logInfo.put("access_time", DateTool.timeFormat(logInfo.get("access_time").toString(), "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss"));
        model.addAttribute("logInfo", logInfo);

        return "tenant/log/detail";
    }


    @LogAround("访问日志导出本页Excel")
    @ResponseBody
    @PostMapping(value = "/exportPage")
    public void exportPage(@RequestBody String data, HttpServletResponse response) {
        List<Map> logInfoList = JsonUtils.readArrayValue(data, Map.class);

        List<Map<String, String>> list = new ArrayList<>();

        for (Map logInfo : logInfoList) {
            Map<String, String> map = new HashMap<>();
            map.put("日志ID", Optional.ofNullable(logInfo.get("log_id")).orElse("").toString());
            map.put("短链", Optional.ofNullable(logInfo.get("surl")).orElse("").toString());
            map.put("访问时间", Optional.ofNullable(logInfo.get("access_time")).orElse("").toString());
            map.put("访问IP", Optional.ofNullable(logInfo.get("access_ip")).orElse("").toString());
            map.put("userAgent", Optional.ofNullable(logInfo.get("access_user_agent")).orElse("").toString());
            map.put("创建时间", Optional.ofNullable(logInfo.get("created_at")).orElse("").toString());
            list.add(map);
        }
        List<String> listName = Arrays.asList("日志ID", "短链", "访问时间", "访问IP", "userAgent", "创建时间");

        ExcelPoiUtil.excelPort("访问日志", listName, list, null, response);
    }

}

package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.BaseController;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.tenant.service.StatisticServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/tenant/statistic")
public class StatisticController extends BaseController {

    @Autowired
    private StatisticServiceImpl statisticService;

    @LogAround("跳转到数据统计首页")
    @GetMapping("/index")
    public String index(Model model) {
        return "tenant/statistic/index";
    }


    @LogAround("数据统计列表分页查询")
    @ResponseBody
    @PostMapping(value="/list")
    public Result list(@RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNumber") Integer pageNumber,
                       @RequestParam(required = false, defaultValue = "", value = "surl") String surl,
                       @RequestParam("sortName") String sortName,
                       @RequestParam("sortOrder") String sortOrder) {
        Page page = statisticService.getPage(pageSize, pageNumber, surl, sortName, sortOrder);

        Map<String, Object> result = new HashMap<>();
        result.put(RESULT_ROWS, page.getRecords());
        result.put(RESULT_TOTAL, page.getTotal());
        return Result.ok("获取数据统计列表成功！", result);
    }

}

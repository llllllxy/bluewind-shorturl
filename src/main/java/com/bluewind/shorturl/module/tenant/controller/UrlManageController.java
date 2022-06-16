package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.BaseController;
import com.bluewind.shorturl.common.base.Result;
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


    @LogAround("跳转到短链列表查询页")
    @GetMapping("/index")
    public String index(Model model) {
        return "tenant/url/index";
    }

    @ResponseBody
    @PostMapping(value="/list")
    public Result list(@RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNumber") Integer pageNumber,
                       @RequestParam(required = false, defaultValue = "", value = "surl") String surl,
                       @RequestParam(required = false, defaultValue = "", value = "created_at") String created_at,
                       @RequestParam("sortName") String sortName,
                       @RequestParam("sortOrder") String sortOrder){



        Map<String, Object> result = new HashMap<>();
//        result.put(RESULT_ROWS, rows);
//        result.put(RESULT_TOTAL, total);
        return Result.ok("获取短链列表成功！", result);
    }
}

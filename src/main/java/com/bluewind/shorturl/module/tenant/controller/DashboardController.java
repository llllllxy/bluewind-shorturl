package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.BaseController;
import com.bluewind.shorturl.module.tenant.service.DashboardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:27
 * @description 仪表盘控制器
 **/
@Controller
@RequestMapping("/tenant/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private DashboardServiceImpl dashboardService;

    @LogAround("跳转到仪表盘页")
    @GetMapping("/index")
    public String index(Model model) {
        Map<String, Object> dashboardInfo = dashboardService.getDashboardInfo();

        model.addAttribute("dashboardInfo", dashboardInfo);
        return "tenant/dashboard/index";
    }

}

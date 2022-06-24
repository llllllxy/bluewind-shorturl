package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tenant/dashboard")
public class DashboardController {

    @LogAround("跳转到仪表盘页")
    @GetMapping("/index")
    public String index(Model model) {
        return "tenant/dashboard/index";
    }

}

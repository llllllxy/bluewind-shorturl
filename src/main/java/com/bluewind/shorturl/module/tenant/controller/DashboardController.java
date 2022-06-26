package com.bluewind.shorturl.module.tenant.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:27
 * @description 仪表盘控制器
 **/
@Controller
@RequestMapping("/tenant/dashboard")
public class DashboardController extends BaseController {

    @LogAround("跳转到仪表盘页")
    @GetMapping("/index")
    public String index(Model model) {
        return "tenant/dashboard/index";
    }


}

package com.bluewind.shorturl.module.mobile.controller;

import com.bluewind.shorturl.common.annotation.LogAround;
import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.module.mobile.service.NavigationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-09-07 12:48
 * @description
 **/
@RestController
@RequestMapping("/mobile")
public class NavigationController {

    @Autowired
    private NavigationServiceImpl navigationService;

    @LogAround("获取导航菜单列表")
    @GetMapping("/navigation")
    public Result navigation() {
        List<Map<String, Object>> menus = navigationService.listNavigation();
        if (CollectionUtils.isEmpty(menus)) {
            return Result.error("获取导航失败！");
        }

        List<Map<String, Object>> modules = new ArrayList<>();

        // 初始moduleId
        String moduleId = (String) menus.get(0).get("module_id");
        // 初始moduleName
        String moduleName = (String) menus.get(0).get("module_name");

        Map<String, Object> moduleMap = new HashMap<>();
        List<Map<String, Object>> funList = new ArrayList<>();


        for (int i = 0; i < menus.size(); i++) {
            Map<String, Object> record = menus.get(i);
            String pModuleId = record.get("module_id") == null ? "" : (String) record.get("module_id");
            String pModuleName = record.get("module_name") == null ? "" : (String) record.get("module_name");
            if (!moduleId.equals(pModuleId)) {
                moduleMap.put("module_id", moduleId);
                moduleMap.put("module_name", moduleName);
                moduleMap.put("functions", funList);
                modules.add(moduleMap);

                // 重新初始化moduleId,moduleName等
                moduleId = pModuleId;
                moduleName = pModuleName;
                funList = new ArrayList<>();
                funList.add(record);
                moduleMap = new HashMap<>();
            } else {
                funList.add(record);
            }
            // 最后一个了
            if (i == (menus.size() - 1)) {
                moduleMap.put("module_id", moduleId);
                moduleMap.put("module_name", moduleName);
                moduleMap.put("functions", funList);
                modules.add(moduleMap);
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("modules", modules);

        return Result.ok("获取导航成功", resultMap);
    }


}

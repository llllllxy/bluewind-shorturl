package com.bluewind.shorturl.module.tenant.service;

import com.bluewind.shorturl.common.config.security.TenantHolder;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.module.tenant.dao.DashboardDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-06-27 13:10
 * @description
 **/
@Service
public class DashboardServiceImpl {

    @Autowired
    private DashboardDaoImpl dashboardDao;

    public Map<String, Object>  getDashboardInfo() {
        String tenantId = TenantHolder.getTenantId();
        String today = DateTool.getToday();

        Map<String, Object> dashboardInfo = new HashMap<>();

        // 1、获取今日点击量Top25
        List<Map<String, Object>> topList = dashboardDao.getTopList(tenantId, today);
        // 2、获取总访问量
        int accessTotalNumber = dashboardDao.getAccessTotalNumber(tenantId);
        // 3、获取总链接数
        int urlTotalNumber = dashboardDao.getUrlTotalNumber(tenantId);
        // 4、获取今日访问量
        int accessTodayNumber = dashboardDao.getAccessTodayNumber(tenantId, today);
        // 5、获取今日独立IP数
        int accessTodayIpNumber = dashboardDao.getAccessTodayIpNumber(tenantId, today);

        dashboardInfo.put("topList", topList);
        dashboardInfo.put("accessTotalNumber", accessTotalNumber);
        dashboardInfo.put("urlTotalNumber", urlTotalNumber);
        dashboardInfo.put("accessTodayNumber", accessTodayNumber);
        dashboardInfo.put("accessTodayIpNumber", accessTodayIpNumber);

        return dashboardInfo;
    }
}

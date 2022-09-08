package com.bluewind.shorturl.module.weixin.service;

import com.bluewind.shorturl.module.weixin.dao.WxDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-09-07 11:20
 * @description
 **/
@Service
public class WxServiceImpl {

    @Autowired
    private WxDaoImpl wxDao;

    /**
     * 根据租户账户查询租户信息
     * @param tenantAccount
     * @return
     */
    public Map<String, Object> getTenantInfo(String tenantAccount) {
        return wxDao.getTenantInfo(tenantAccount);
    }


    public List<Map<String, Object>> getWxUserMapInfo(String appId, String agentId, String wxUserId, String deviceId) {
        return wxDao.getWxUserMapInfo(appId, agentId, wxUserId, deviceId);
    }

}

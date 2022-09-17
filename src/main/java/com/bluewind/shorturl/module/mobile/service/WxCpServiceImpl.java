package com.bluewind.shorturl.module.mobile.service;

import com.bluewind.shorturl.module.mobile.dao.WxCpDaoImpl;
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
public class WxCpServiceImpl {

    @Autowired
    private WxCpDaoImpl wxDao;

    /**
     * 根据租户账户查询租户信息
     * @param tenantAccount
     * @return
     */
    public Map<String, Object> getTenantInfo(String tenantAccount) {
        return wxDao.getTenantInfo(tenantAccount);
    }

    /**
     * 获取微信用户绑定信息
     * @param appId
     * @param agentId
     * @param wxUserId
     * @param deviceId
     * @return
     */
    public List<Map<String, Object>> getWxUserMapInfo(String appId, String agentId, String wxUserId, String deviceId) {
        return wxDao.getWxUserMapInfo(appId, agentId, wxUserId, deviceId);
    }


    /**
     * 解绑（使status=02）
     * @param appId
     * @param wxUserId
     * @param agentId
     * @param deviceId
     */
    public int unbind(String appId, String wxUserId, String agentId ,String deviceId) {
        return wxDao.unbind(appId, wxUserId, agentId, deviceId);
    }

    /**
     * 绑定
     * @param appId
     * @param wxUserId
     * @param agentId
     * @param deviceId
     * @param username
     * @return
     */
    public int bind(String appId, String wxUserId, String agentId, String deviceId, String username) {
        return wxDao.bind(appId, wxUserId, agentId, deviceId, username);
    }
}

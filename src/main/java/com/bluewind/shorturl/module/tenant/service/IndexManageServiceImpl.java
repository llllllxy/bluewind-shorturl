package com.bluewind.shorturl.module.tenant.service;

import com.bluewind.shorturl.module.tenant.dao.IndexManageDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 15:25
 * @description
 **/
@Service
public class IndexManageServiceImpl {

    @Autowired
    private IndexManageDaoImpl indexManageDao;

    /**
     * 根据租户账户查询租户信息
     * @param tenantAccount
     * @return
     */
    public Map<String, Object> getTenantInfo(String tenantAccount) {
        return indexManageDao.getTenantInfo(tenantAccount);
    }

    /**
     * 新增一个租户账户
     * @param tenantAccount
     * @param tenantName
     * @param tenantPassword
     * @param tenantPhone
     * @return
     */
    public int addTenantInfo(String tenantAccount, String tenantName, String tenantPassword, String tenantPhone) {
        return indexManageDao.addTenantInfo(tenantAccount, tenantName, tenantPassword, tenantPhone);
    }
}

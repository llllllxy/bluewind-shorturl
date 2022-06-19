package com.bluewind.shorturl.module.tenant.dao;

import com.bluewind.shorturl.common.util.GenerateAkAndSk;
import com.bluewind.shorturl.common.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 15:24
 * @description
 **/
@Repository
public class IndexManageDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据租户账户查询租户信息
     * @param tenantAccount
     * @return
     */
    public Map<String, Object> getTenantInfo(String tenantAccount) {
        try {
            String sql = "select * from s_tenant where tenant_account = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, tenantAccount);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
        String tenantId = Snowflake.nextId();
        // 注册成功时，自动生成accessKey和accessKeySecret
        String accessKey = GenerateAkAndSk.generateAk();
        String accessKeySecret = GenerateAkAndSk.generateSk();

        String sql = "insert into s_tenant (tenant_id, tenant_account, tenant_password, tenant_name, tenant_phone, access_key, access_key_secret) values (?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, tenantId, tenantAccount, tenantPassword, tenantName, tenantPhone, accessKey, accessKeySecret);
    }

}

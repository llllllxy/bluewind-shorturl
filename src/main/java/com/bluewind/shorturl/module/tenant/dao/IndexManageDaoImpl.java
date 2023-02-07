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
     * @param tenantEmail
     * @return
     */
    public int addTenantInfo(String tenantAccount, String tenantName, String tenantPassword, String tenantEmail) {
        String tenantId = Snowflake.nextId();
        // 注册成功时，自动生成accessKey和accessKeySecret
        String accessKey = GenerateAkAndSk.generateAk();
        String accessKeySecret = GenerateAkAndSk.generateSk();

        String sql = "insert into s_tenant (tenant_id, tenant_account, tenant_password, tenant_name, tenant_email, access_key, access_key_secret) values (?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, tenantId, tenantAccount, tenantPassword, tenantName, tenantEmail, accessKey, accessKeySecret);
    }


    /**
     * 更新一个租户账户
     * @param tenant_id
     * @param tenant_name
     * @param tenant_email
     * @return
     */
    public int updateProfile(String tenant_id, String tenant_name, String tenant_email) {
        String sql = "update s_tenant set tenant_name = ?, tenant_email = ? where tenant_id = ?";
        return jdbcTemplate.update(sql, tenant_name, tenant_email, tenant_id);
    }


    /**
     * 更新ak和sk
     * @param tenant_id
     * @param accessKey
     * @param accessKeySecret
     * @return
     */
    public int updateAkAndSk(String tenant_id, String accessKey, String accessKeySecret) {
        String sql = "update s_tenant set access_key = ?, access_key_secret = ? where tenant_id = ?";
        return jdbcTemplate.update(sql, accessKey, accessKeySecret, tenant_id);
    }
}

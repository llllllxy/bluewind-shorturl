package com.bluewind.shorturl.module.api.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ApiDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getConfigData(String accessKey) {
        try {
            String sql = "select tenant_id, tenant_account, access_key, access_key_secret from s_tenant where access_key = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, accessKey);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public int expire(String shortUrl, String expireDate, String tenantId) {
        String sql = "update s_url_map set expire_time = ? where tenant_id = ? and surl = ?";
        return jdbcTemplate.update(sql, expireDate, tenantId, shortUrl);
    }


    public int disable(String shortUrl, String tenantId) {
        String sql = "update s_url_map set status = '1' where tenant_id = ? and surl = ?";
        return jdbcTemplate.update(sql, tenantId, shortUrl);
    }


    public int enable(String shortUrl, String tenantId) {
        String sql = "update s_url_map set status = '0' where tenant_id = ? and surl = ?";
        return jdbcTemplate.update(sql, tenantId, shortUrl);
    }
}

package com.bluewind.shorturl.module.tenant.dao;

import org.springframework.beans.factory.annotation.Autowired;
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

    public Map<String, Object> getUserInfo(String account) {
        String sql = "select id, status, password from s_user where account = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, account);
        return result;
    }


}

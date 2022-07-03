package com.bluewind.shorturl.module.tenant.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-06-27 13:09
 * @description
 **/
@Repository
public class DashboardDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getTopList(String tenantId, String today) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select count(sc.log_id) accessCount, ");
        sql.append(" count(distinct sc.access_ip) ipCount, ");
        sql.append(" sc.surl, ");
        sql.append(" sc.tenant_id, ");
        sql.append(" max(su.created_at) created_at, ");
        sql.append(" max(su.lurl) lurl ");
        sql.append(" from s_access_log sc ");
        sql.append(" join s_url_map su on su.surl = sc.surl ");
        sql.append(" where sc.tenant_id = '").append(tenantId).append("' ");
        sql.append(" and left(sc.access_time, 8) = '").append(today).append("' ");
        sql.append(" group by ");
        sql.append(" sc.tenant_id, sc.surl limit 25");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
        return result;
    }


    public int getAccessTotalNumber(String tenantId) {
        String sql = "select count(log_id) from s_access_log where tenant_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, tenantId);
        return count;
    }

    public int getUrlTotalNumber(String tenantId) {
        String sql = "select count(id) from s_url_map where tenant_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, tenantId);
        return count;
    }

    public int getAccessTodayNumber(String tenantId, String today) {
        String sql = "select count(log_id) from s_access_log where tenant_id = ? and DATE_FORMAT(access_time, '%Y%m%d') = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, tenantId, today);
        return count;
    }


    public int getAccessTodayIpNumber(String tenantId, String today) {
        String sql = "select count(log_id) from s_access_log where tenant_id = ? and DATE_FORMAT(access_time, '%Y%m%d') = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, tenantId, today);
        return count;
    }

}

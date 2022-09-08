package com.bluewind.shorturl.module.weixin.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-09-07 11:20
 * @description
 **/
@Repository
public class WxDaoImpl {

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


    public List<Map<String, Object>> getWxUserMapInfo(String corpId, String agentId, String wxUserId, String deviceId) {
        String sql = "select * from s_wx_usermap where corp_id = ? and agent_id = ? and wx_user_id = ? and device_id = ? and status = '01'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, corpId, agentId, wxUserId, deviceId);
        return list;
    }


}

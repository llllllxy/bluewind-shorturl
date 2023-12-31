package com.bluewind.shorturl.module.tenant.dao;

import com.bluewind.shorturl.common.config.security.TenantHolder;
import com.bluewind.shorturl.common.util.page.IPageHandle;
import com.bluewind.shorturl.common.util.page.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:28
 * @description
 **/
@Repository
public class UrlManageDaoImpl {
    @Autowired
    private IPageHandle pageHandle;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Page getPage(Integer pageSize, Integer pageNumber, String surl, String createdAt, String status, String sortName, String sortOrder) {
        String tenantId = TenantHolder.getTenantId();
        StringBuilder sb =  new StringBuilder();
        sb.append("select * from s_url_map where tenant_id = '").append(tenantId).append("' ");
        if (StringUtils.isNotBlank(surl)) {
            sb.append("and surl like '").append(surl).append("%' ");
        }
        if (StringUtils.isNotBlank(createdAt)) {
            sb.append("and created_at like '").append(createdAt).append("%' ");
        }
        if (StringUtils.isNotBlank(status)) {
            sb.append("and status = '").append(status).append("' ");
        }
        sb.append("and del_flag = '0' ");
        if (StringUtils.isNotBlank(sortName) && StringUtils.isNotBlank(sortOrder)) {
            sb.append("order by ").append(sortName).append(" ").append(sortOrder);
        }
        Page page = pageHandle.getPage(sb.toString(), pageNumber, pageSize);
        return page;
    }


    public Map<String, Object> findById(String id) {
        try {
            String sql = "select * from s_url_map where id = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public int del(String id) {
        String sql = "update s_url_map set del_flag = '1' where id = ?";
        return jdbcTemplate.update(sql, id);
    }


    public int batchDel(String idlistStr) {
        List<String> idList = new ArrayList<>();
        Collections.addAll(idList, idlistStr.split(","));

        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> param = new HashMap<>();
        param.put("idList", idList);

        String sql = "update s_url_map set del_flag = '1' where id in (:idList)";
        return namedJdbcTemplate.update(sql, param);
    }


    public int disable(String idlistStr) {
        List<String> idList = new ArrayList<>();
        Collections.addAll(idList, idlistStr.split(","));

        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> param = new HashMap<>();
        param.put("idList", idList);

        String sql = "update s_url_map set status = '1' where id in (:idList)";
        return namedJdbcTemplate.update(sql, param);
    }

    public int enable(String idlistStr) {
        List<String> idList = new ArrayList<>();
        Collections.addAll(idList, idlistStr.split(","));

        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> param = new HashMap<>();
        param.put("idList", idList);

        String sql = "update s_url_map set status = '0' where id in (:idList)";
        return namedJdbcTemplate.update(sql, param);
    }


    public int edit(String id, String status, String expireDate, String note) {
        String sql = "update s_url_map set status = ?, expire_time = ?, note = ? where id = ?";
        return jdbcTemplate.update(sql, status, expireDate, note, id);
    }
}

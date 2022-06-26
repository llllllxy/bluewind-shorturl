package com.bluewind.shorturl.module.tenant.dao;

import com.bluewind.shorturl.common.config.security.TenantHolder;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.page.IPageHandle;
import com.bluewind.shorturl.common.util.page.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:28
 * @description
 **/
@Repository
public class LogManageDaoImpl {

    @Autowired
    private IPageHandle pageHandle;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Page getPage(Integer pageSize, Integer pageNumber, String surl, String startTime, String  endTime, String accessIp , String sortName, String sortOrder) {
        String tenantId = TenantHolder.getTenantId();
        StringBuilder sb =  new StringBuilder();
        sb.append("select * from s_access_log where tenant_id = '").append(tenantId).append("' ");
        if (StringUtils.isNotBlank(surl)) {
            sb.append("and surl like '").append(surl).append("%' ");
        }
        if (StringUtils.isNotBlank(startTime)) {
            startTime = DateTool.dateFormat(startTime, "yyyy-MM-dd", "yyyyMMdd") + "000000";
            sb.append("and access_time >= '").append(startTime).append("' ");
        }
        if (StringUtils.isNotBlank(endTime)) {
            endTime = DateTool.dateFormat(endTime, "yyyy-MM-dd", "yyyyMMdd") + "235959";
            sb.append("and access_time <= '").append(endTime).append("' ");
        }
        if (StringUtils.isNotBlank(accessIp)) {
            sb.append("and access_ip like '").append(accessIp).append("%' ");
        }
        if (StringUtils.isNotBlank(sortName) && StringUtils.isNotBlank(sortOrder)) {
            sb.append("order by ").append(sortName).append(" ").append(sortOrder);
        } else {
            sb.append("order by access_time desc");
        }

        Page page = pageHandle.getPage(sb.toString(), pageNumber, pageSize);
        return page;
    }


    public Map<String, Object> findByLogId(String logId) {
        try {
            String sql = "select * from s_access_log where log_id = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, logId);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}

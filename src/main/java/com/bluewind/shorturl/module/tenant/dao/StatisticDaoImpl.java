package com.bluewind.shorturl.module.tenant.dao;

import com.bluewind.shorturl.common.config.security.TenantHolder;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.page.IPageHandle;
import com.bluewind.shorturl.common.util.page.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticDaoImpl {

    @Autowired
    private IPageHandle pageHandle;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Page getPage(Integer pageSize, Integer pageNumber, String surl, String sortName, String sortOrder) {
        String tenantId = TenantHolder.getTenantId();
        String today = DateTool.getToday();
        String yesterday = DateTool.getYesterday();
        String month = DateTool.getCurrentDate().substring(0, 6);

        StringBuilder sb = new StringBuilder();
        sb.append("select su.surl, su.tenant_id, max(su.created_at) created_at, max(su.lurl) lurl, ");
        sb.append("(SELECT count(log_id) FROM s_access_log WHERE left(access_time, 8) = '").append(today).append("' AND surl = su.surl) AS accessTodayNumber, ");
        sb.append("(SELECT count(distinct access_ip) FROM s_access_log WHERE left(access_time, 8) = '").append(today).append("' AND surl = su.surl) AS accessTodayIpNumber, ");
        sb.append("(SELECT count(log_id) FROM s_access_log WHERE left(access_time, 8) = '").append(yesterday).append("' AND surl = su.surl) AS accessYesterdayNumber, ");
        sb.append("(SELECT count(distinct access_ip) FROM s_access_log WHERE left(access_time, 8) = '").append(yesterday).append("' AND surl = su.surl) AS accessYesterdayIpNumber, ");
        sb.append("(SELECT count(log_id) FROM s_access_log WHERE left(access_time, 6) = '").append(month).append("' AND surl = su.surl) AS accessMonthNumber, ");
        sb.append("(SELECT count(distinct access_ip) FROM s_access_log WHERE left(access_time, 6) = '").append(month).append("' AND surl = su.surl) AS accessMonthIpNumber, ");
        sb.append("(SELECT count(log_id) FROM s_access_log WHERE surl = su.surl) AS accessTotalNumber, ");
        sb.append("(SELECT count(distinct access_ip) FROM s_access_log WHERE surl = su.surl) AS accessTotalIpNumber ");
        sb.append("from s_url_map su ");
        sb.append("where su.tenant_id = '").append(tenantId).append("' ");
        if (StringUtils.isNotBlank(surl)) {
            sb.append("and su.surl like '").append(surl).append("%' ");
        }
        sb.append("group by  ");
        sb.append("su.tenant_id, su.surl ");
        if (StringUtils.isNotBlank(sortName) && StringUtils.isNotBlank(sortOrder)) {
            sb.append("order by ").append(sortName).append(" ").append(sortOrder);
        } else {
            sb.append("order by created_at");
        }

        Page page = pageHandle.getPage(sb.toString(), pageNumber, pageSize);
        return page;
    }

}

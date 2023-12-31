package com.bluewind.shorturl.module.portal.dao;

import com.bluewind.shorturl.common.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-03-13-9:47
 **/
@Repository
public class ShortUrlDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 根据短链，获取列表数据
     *
     * @param shortURL 短链
     * @return
     */
    public List<Map<String, Object>> queryListByshortURL(String shortURL) {
        String sql = "select lurl, tenant_id, expire_time from s_url_map where surl = ? and del_flag = '0' and status = '0'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, shortURL);
        return result;
    }


    /**
     * 根据短链，判断此短链是否存在
     *
     * @param shortURL 短链
     * @return
     */
    public boolean ifExistByShortUrl(String shortURL) {
        String sql = "select id from s_url_map where surl = ? limit 1";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, shortURL);
        if (result != null && !result.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 更新shortUrl的访问次数
     *
     * @param shortURL 短链
     * @return
     */
    public int updateUrlViews(String shortURL) {
        String sql = "update s_url_map set views = views + 1 where surl = ?";
        return jdbcTemplate.update(sql, shortURL);
    }


    /**
     * 插入访问日志表s_access_log
     *
     * @param shortURL        短链
     * @param accessIp        请求IP
     * @param accessTime      请求时间
     * @param accessUserAgent 请求UserAgent
     * @return
     */
    public int insertAccessLogs(String shortURL, String accessIp, String accessAddress, String accessTime, String accessUserAgent, String tenantId) {
        String logId = Snowflake.nextId();
        String sql = "insert into s_access_log (log_id, surl, access_time, access_ip, access_address, access_user_agent, tenant_id) values (?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, logId, shortURL, accessTime, accessIp, accessAddress, accessUserAgent, tenantId);
    }


    /**
     * 新增保存一条短链数据
     *
     * @param shortURL    短链
     * @param originalURL 原始链接
     * @param expireDate  过期时间
     * @param tenantId    租户ID
     * @return
     */
    public int insertUrlMap(String shortURL, String originalURL, String expireDate, String tenantId, String status, String note) {
        String id = Snowflake.nextId();
        String sql = "insert into s_url_map (id, surl, lurl, views, expire_time, tenant_id, status, note) values (?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, id, shortURL, originalURL, 0, expireDate, tenantId, status, note);
    }
}

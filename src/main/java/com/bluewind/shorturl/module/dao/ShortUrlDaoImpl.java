package com.bluewind.shorturl.module.dao;

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
     * @param shortURL
     * @return
     */
    public List<Map<String, Object>> queryListByshortURL(String shortURL) {
        String sql = "select lurl, expire_time from s_url_map where surl = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, shortURL);
        return result;
    }


    /**
     * 根据短链，判断此短链是否存在
     * @param shortURL
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
     * @param shortURL
     * @return
     */
    public int updateUrlViews(String shortURL) {
        String sql = "update s_url_map set views = views + 1 where surl = ?";
        return jdbcTemplate.update(sql, shortURL);
    }

    /**
     * 插入访问日志表s_access_log
     * @param shortURL
     * @param accessIp
     * @param accessTime
     * @param accessUserAgent
     * @return
     */
    public int insertAccessLogs(String shortURL, String accessIp, String accessTime, String accessUserAgent) {
        String sql = "insert into s_access_log (surl, access_time, access_ip, access_user_agent) values (?,?,?,?)";
        return jdbcTemplate.update(sql, shortURL, accessTime, accessIp, accessUserAgent);
    }

    /**
     * 新增一条短链
     * @param shortURL
     * @param originalURL
     * @param expireDate
     * @return
     */
    public int insertOne(String shortURL, String originalURL, String expireDate) {
        String sql = "insert into s_url_map (surl, lurl, views, expire_time) values (?,?,?,?)";
        return jdbcTemplate.update(sql, shortURL, originalURL, 0, expireDate);
    }
}

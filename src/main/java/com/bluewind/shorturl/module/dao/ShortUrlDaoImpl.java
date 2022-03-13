package com.bluewind.shorturl.module.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-03-13-9:47
 **/
@Component
public class ShortUrlDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<Map<String, Object>> queryListByshortURL(String shortURL) {
        String sql = "select lurl, expire_time from url_map where surl = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, shortURL);
        return result;
    }


    public boolean ifExistByShortUrl(String shortURL) {
        String sql = "select id from url_map where surl = ? limit 1";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, shortURL);
        if (result != null && !result.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    public int updateUrlViews(String shortURL) {
        String sql = "update url_map set views = views + 1 where surl = ?";
        return jdbcTemplate.update(sql, shortURL);
    }

    public int insertOne(String shortURL, String originalURL, String expireDate) {
        String sql = "insert into url_map (surl, lurl, views, expire_time) values (?,?,?,?)";
        return jdbcTemplate.update(sql, shortURL, originalURL, 0, expireDate);
    }
}
package com.bluewind.shorturl.module.service;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import com.bluewind.shorturl.common.util.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxingyu01
 * @date 2022-03-11-16:56
 **/
@Service
public class ShortUrlServiceImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 自定义长链接防重复字符串
    private static final String DUPLICATE = "*";

    // 最近使用的短链接缓存过期时间(分钟)
    private static final long TIMEOUT = 10;

    // 创建布隆过滤器
    private static final BitMapBloomFilter FILTER = BloomFilterUtil.createBitMap(10);


    public String getOriginalUrlByShortUrl(String shortURL) {
        // 查找Redis中是否有缓存
        String longURL = redisTemplate.opsForValue().get(shortURL);
        if (longURL != null) {
            // 有缓存，延迟缓存时间
            redisTemplate.expire(shortURL, TIMEOUT, TimeUnit.MINUTES);
            return longURL;
        }
        // Redis没有缓存，从数据库查找
        String sql = "select lurl from url_map where surl = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, shortURL);
        if (result != null && !result.isEmpty()) {
            longURL = result.get(0).get("lurl") == null ? null : (String) result.get(0).get("lurl");
        }

        if (longURL != null) {
            // 数据库有此短链接，添加缓存
            redisTemplate.opsForValue().set(shortURL, longURL, TIMEOUT, TimeUnit.MINUTES);
        }
        return longURL;
    }


    public String saveUrlMap(String shortURL, String longURL, String originalURL) {
        // 保留长度为1的短链接
        if (shortURL.length() == 1) {
            longURL += DUPLICATE;
            shortURL = saveUrlMap(HashUtils.hashToBase62(longURL), longURL, originalURL);
        }
        // 在布隆过滤器中查找是否存在
        else if (FILTER.contains(shortURL)) {
            // 存在，从Redis中查找是否有缓存
            String redisLongURL = redisTemplate.opsForValue().get(shortURL);
            if (redisLongURL != null && originalURL.equals(redisLongURL)) {
                //Redis有缓存，重置过期时间
                redisTemplate.expire(shortURL, TIMEOUT, TimeUnit.MINUTES);
                return shortURL;
            }
            // 没有缓存，在长链接后加上指定字符串，重新hash
            longURL += DUPLICATE;
            shortURL = saveUrlMap(HashUtils.hashToBase62(longURL), longURL, originalURL);
        } else {
            // 不存在，直接存入数据库
            try {
                String sql = "insert into url_map (surl, lurl, views) values (?,?,?)";
                jdbcTemplate.update(sql, shortURL, originalURL, 0);

                FILTER.add(shortURL);
                // 添加redis缓存
                redisTemplate.opsForValue().set(shortURL, originalURL, TIMEOUT, TimeUnit.MINUTES);
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    // 数据库已经存在此短链接，则可能是布隆过滤器误判，在长链接后加上指定字符串，重新hash
                    longURL += DUPLICATE;
                    shortURL = saveUrlMap(HashUtils.hashToBase62(longURL), longURL, originalURL);
                } else {
                    throw e;
                }
            }
        }
        return shortURL;
    }


    public void updateUrlViews(String shortURL) {
        String sql = "update url_map set views = views + 1 where surl = ?";
        jdbcTemplate.update(sql, shortURL);
    }
}

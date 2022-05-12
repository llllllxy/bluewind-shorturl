package com.bluewind.shorturl.module.service;

import com.bluewind.shorturl.common.util.HashUtils;
import com.bluewind.shorturl.common.util.JacksonUtils;
import com.bluewind.shorturl.module.dao.ShortUrlDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxingyu01
 * @date 2022-03-11-16:56
 * @description
 * @reference https://juejin.cn/post/6844904090602848270?share_token=4ba00e67-783a-496a-9b49-fe5cb9b1a4c9
 **/
@Service
public class ShortUrlServiceImpl {
    final static Logger log = LoggerFactory.getLogger(ShortUrlServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ShortUrlDaoImpl shortUrlDao;

    // 自定义长链接防重复字符串
    private static final String DUPLICATE = "*";

    // 最近使用的短链接缓存过期时间(分钟)
    private static final long TIMEOUT = 10;

    /**
     * 根据短链获取原始链接
     *
     * @param shortURL 短链
     * @return
     */
    public Map<String, String> getOriginalUrlByShortUrl(String shortURL) {
        // 查找Redis中是否有缓存
        Map<String, String> urlDataMap = redisGet(shortURL);

        if (urlDataMap != null && !urlDataMap.isEmpty()) {
            // Redis中有缓存，则刷新缓存时间
            redisTemplate.expire(shortURL, TIMEOUT, TimeUnit.MINUTES);
            return urlDataMap;
        }
        // Redis没有缓存，则从数据库查找
        List<Map<String, Object>> result = shortUrlDao.queryListByshortURL(shortURL);
        String originalURL = "";
        String expireDate = "";
        if (result != null && !result.isEmpty()) {
            originalURL = result.get(0).get("lurl") == null ? null : (String) result.get(0).get("lurl");
            expireDate = result.get(0).get("expire_time") == null ? null : (String) result.get(0).get("expire_time");

            // 数据库有此短链接，则添加缓存
            redisSave(shortURL, originalURL, expireDate);

            urlDataMap = new HashMap<>();
            urlDataMap.put("shortURL", shortURL);
            urlDataMap.put("originalURL", originalURL);
            urlDataMap.put("expireDate", expireDate);

            return urlDataMap;
        } else {
            return null;
        }
    }


    /**
     * 保存短链
     *
     * @param originalURL 原始链接
     * @param expireDate 过期时间
     * @return 生成的短链
     */
    public String saveUrlMap(String originalURL, String expireDate) {
        if (log.isInfoEnabled()) {
            log.info("ShortUrlServiceImpl -- saveUrlMap -- originalURL = {}", originalURL);
        }
        String tempURL = originalURL;
        String shortURL = HashUtils.hashToBase62(tempURL);

        // 保留长度为1的短链接（不允许长度为1，长度为1的强行再hash一次）
        while (shortURL.length() == 1) {
            tempURL += DUPLICATE;
            shortURL = HashUtils.hashToBase62(tempURL);
        }

        // 直接存入数据库，如果触发异常的话，说明违反了 surl 的唯一性索引，那说明数据库中已经存在此shortURL了
        // 则拼接上DUPLICATE，继续hash，直到不再冲突
        boolean ifContinue = true;
        while (ifContinue) {
            try {
                // 直到数据库中不存在此shortURL，那则可以进行数据库插入了
                shortUrlDao.insertOne(shortURL, originalURL, expireDate);
                // 同时添加redis缓存
                redisSave(shortURL, originalURL, expireDate);
                ifContinue = false;
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    tempURL += DUPLICATE;
                    shortURL = HashUtils.hashToBase62(tempURL);
                } else {
                    log.info("ShortUrlServiceImpl -- saveUrlMap -- Exception = {e}", e);
                    throw e;
                }
            }
        }

        return shortURL;
    }

    /**
     * 存入redis
     *
     * @param shortURL 短链
     * @param originalURL 原始链接
     * @param expireDate 过期时间
     */
    public void redisSave(String shortURL, String originalURL, String expireDate) {
        Map<String, String> map = new HashMap<>();
        map.put("shortURL", shortURL);
        map.put("originalURL", originalURL);
        map.put("expireDate", expireDate);
        redisTemplate.opsForValue().set(shortURL, JacksonUtils.writeValueAsString(map), TIMEOUT, TimeUnit.MINUTES);
    }


    /**
     * 取出缓存的数据
     * @param shortURL 短链
     * @return
     */
    public Map<String, String> redisGet(String shortURL) {
        String mapString = redisTemplate.opsForValue().get(shortURL);
        if (mapString != null) {
            return (Map<String,String>) JacksonUtils.readValue(mapString, Map.class);
        } else {
            return null;
        }
    }

    /**
     * 更新访问次数
     *
     * @param shortURL 短链
     */
    public void updateUrlViews(String shortURL) {
        shortUrlDao.updateUrlViews(shortURL);
    }
}

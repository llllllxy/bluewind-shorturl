package com.bluewind.shorturl.module.service;

import com.bluewind.shorturl.common.util.HashUtils;
import com.bluewind.shorturl.module.dao.ShortUrlDaoImpl;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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

    // 布隆过滤器预计要插入多少数据
    private static final int SIZE = 1000000;

    // 布隆过滤器期望的误判率
    private static final double FPP = 0.01;

    // guava提供的jvm级别的布隆过滤器
    private static final BloomFilter<String> BLOOM_FILTER = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), SIZE, FPP);


    /**
     * 根据短链获取原始链接
     *
     * @param shortURL 短链
     * @return
     */
    public String getOriginalUrlByShortUrl(String shortURL) {
        // 查找Redis中是否有缓存
        String originalUrl = redisTemplate.opsForValue().get(shortURL);
        if (originalUrl != null) {
            // Redis中有缓存，则刷新缓存时间
            redisTemplate.expire(shortURL, TIMEOUT, TimeUnit.MINUTES);
            return originalUrl;
        }
        // Redis没有缓存，则从数据库查找
        List<Map<String, Object>> result = shortUrlDao.queryListByshortURL(shortURL);
        if (result != null && !result.isEmpty()) {
            originalUrl = result.get(0).get("lurl") == null ? null : (String) result.get(0).get("lurl");
        }

        if (originalUrl != null) {
            // 数据库有此短链接，添加缓存
            redisTemplate.opsForValue().set(shortURL, originalUrl, TIMEOUT, TimeUnit.MINUTES);
        }
        return originalUrl;
    }


    /**
     * 保存短链
     *
     * @param originalURL
     * @return
     */
    public String saveUrlMap(String originalURL) {
        if (log.isInfoEnabled()) {
            log.info("ShortUrlServiceImpl -- saveUrlMap -- originalURL = {}", originalURL);
        }
        String tempURL = originalURL;
        String shortURL = HashUtils.hashToBase62(tempURL);

        if (log.isInfoEnabled()) {
            log.info("ShortUrlServiceImpl -- saveUrlMap -- shortURL = {}", shortURL);
        }

        // 保留长度为1的短链接（不允许长度为1，长度为1的强行再hash一次）
        while (shortURL.length() == 1) {
            tempURL += DUPLICATE;
            shortURL = HashUtils.hashToBase62(tempURL);
        }

        // 在布隆过滤器中查找是否存在
        if (BLOOM_FILTER.mightContain(shortURL)) {
            // 存在，从Redis中查找是否有缓存
            String redisLongURL = redisTemplate.opsForValue().get(shortURL);
            if (redisLongURL != null && originalURL.equals(redisLongURL)) {
                // Redis有相同缓存，那刷新过期时间，直接返回即可
                redisTemplate.expire(shortURL, TIMEOUT, TimeUnit.MINUTES);
                return shortURL;
            }

            // Redis中不存在或者过期的话，就去主动去插入数据库，如果触发异常的话，说明违反了 surl 的唯一性索引，那说明数据库中已经存在此shortURL了
            // 则拼接上DUPLICATE，继续hash，直到不再冲突
            boolean ifContinue = true;
            while (ifContinue) {
                try {
                    // 直到数据库中不存在此shortURL，那则可以进行数据库插入了
                    shortUrlDao.insertOne(shortURL, originalURL);
                    // 放入到布隆过滤器中去
                    BLOOM_FILTER.put(shortURL);
                    // 同时添加redis缓存
                    redisTemplate.opsForValue().set(shortURL, originalURL, TIMEOUT, TimeUnit.MINUTES);
                    ifContinue = false;
                } catch (Exception e) {
                    if (e instanceof DuplicateKeyException) {
                        tempURL += DUPLICATE;
                        shortURL = HashUtils.hashToBase62(tempURL);
                    } else {
                        throw e;
                    }
                }
            }
        } else {
            // 布隆过滤器中不存在的话，那就直接存入数据库，如果触发异常的话，说明违反了 surl 的唯一性索引，那说明数据库中已经存在此shortURL了
            // 则拼接上DUPLICATE，继续hash，直到不再冲突
            boolean ifContinue = true;
            while (ifContinue) {
                try {
                    // 直到数据库中不存在此shortURL，那则可以进行数据库插入了
                    shortUrlDao.insertOne(shortURL, originalURL);
                    // 放入到布隆过滤器中去
                    BLOOM_FILTER.put(shortURL);
                    // 同时添加redis缓存
                    redisTemplate.opsForValue().set(shortURL, originalURL, TIMEOUT, TimeUnit.MINUTES);
                    ifContinue = false;
                } catch (Exception e) {
                    if (e instanceof DuplicateKeyException) {
                        tempURL += DUPLICATE;
                        shortURL = HashUtils.hashToBase62(tempURL);
                    } else {
                        throw e;
                    }
                }
            }
        }

        return shortURL;
    }


    /**
     * 更新访问次数
     *
     * @param shortURL
     */
    public void updateUrlViews(String shortURL) {
        shortUrlDao.updateUrlViews(shortURL);
    }
}

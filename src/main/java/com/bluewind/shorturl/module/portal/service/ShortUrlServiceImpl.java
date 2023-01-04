package com.bluewind.shorturl.module.portal.service;

import com.bluewind.shorturl.common.util.*;
import com.bluewind.shorturl.common.util.web.UserAgentUtils;
import com.bluewind.shorturl.module.portal.dao.ShortUrlDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxingyu01
 * @date 2022-03-11-16:56
 * @description 核心代码，短链生成和短链寻址
 * @reference https://juejin.cn/post/6844904090602848270
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
        if (result != null && !result.isEmpty()) {
            String originalURL = result.get(0).get("lurl") == null ? null : (String) result.get(0).get("lurl");
            String expireDate = result.get(0).get("expire_time") == null ? null : (String) result.get(0).get("expire_time");
            String tenantId = result.get(0).get("tenant_id") == null ? null : (String) result.get(0).get("tenant_id");

            // 数据库有此短链接，则添加缓存
            redisSave(shortURL, originalURL, expireDate, tenantId);

            urlDataMap = new HashMap<>();
            urlDataMap.put("shortURL", shortURL);
            urlDataMap.put("originalURL", originalURL);
            urlDataMap.put("expireDate", expireDate);
            urlDataMap.put("tenantId", tenantId);

            return urlDataMap;
        } else {
            return null;
        }
    }


    /**
     * 生成并保存短链（一开始是递归，后来改成while循环了）
     *
     * @param originalURL 原始链接
     * @param expireDate 过期时间
     * @return 生成的短链
     */
    public String generateUrlMap(String originalURL, String expireDate, String tenantId, String status, String note) {
        if (log.isInfoEnabled()) {
            log.info("ShortUrlServiceImpl -- generateUrlMap -- originalURL = {}", originalURL);
        }
        String tempURL = originalURL;
        String shortURL = HashUtils.hashToBase62(tempURL);

        // 保留长度为1的短链接（不允许长度为1，长度为1的强行再hash一次）
        while (shortURL.length() == 1) {
            tempURL += DUPLICATE;
            shortURL = HashUtils.hashToBase62(tempURL);
        }

        // 在布隆过滤器中查找是否存在，如果已经存在了，则再重新HASH一次（还是为了提高性能，减少插入时撞主键的概率，减少数据库的压力嘛）
        boolean ifBloonContain = true;
        while (ifBloonContain) {
            // 可能存在，重新HASH
            if (BLOOM_FILTER.mightContain(shortURL)) {
                tempURL += DUPLICATE;
                shortURL = HashUtils.hashToBase62(tempURL);
            } else {
                // 不存在则跳出循环，继续往下走
                ifBloonContain = false;
            }
        }

        // 直接存入数据库，如果触发异常的话，说明违反了 surl 的唯一性索引，那说明数据库中已经存在此shortURL了
        // 则拼接上DUPLICATE，继续hash，直到不再冲突
        boolean ifContinue = true;
        while (ifContinue) {
            try {
                // 直到数据库中不存在此shortURL，那则可以进行数据库插入了
                shortUrlDao.insertUrlMap(shortURL, originalURL, expireDate, tenantId, status, note);
                // 放入到布隆过滤器中去
                BLOOM_FILTER.put(shortURL);
                // 同时添加redis缓存
                redisSave(shortURL, originalURL, expireDate, tenantId);
                ifContinue = false;
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    tempURL += DUPLICATE;
                    shortURL = HashUtils.hashToBase62(tempURL);
                } else {
                    log.error("ShortUrlServiceImpl -- generateUrlMap -- Exception = {e}", e);
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
    public void redisSave(String shortURL, String originalURL, String expireDate, String tenantId) {
        Map<String, String> map = new HashMap<>();
        map.put("shortURL", shortURL);
        map.put("originalURL", originalURL);
        map.put("expireDate", expireDate);
        map.put("tenantId", tenantId);
        redisTemplate.opsForValue().set(shortURL, JsonUtils.writeValueAsString(map), TIMEOUT, TimeUnit.MINUTES);
    }


    /**
     * 取出缓存的数据
     * @param shortURL 短链
     * @return
     */
    public Map<String, String> redisGet(String shortURL) {
        String mapString = redisTemplate.opsForValue().get(shortURL);
        if (mapString != null) {
            return (Map<String,String>) JsonUtils.readValue(mapString, Map.class);
        } else {
            return null;
        }
    }

    /**
     * 更新访问次数，插入访问日志
     *
     * @param shortURL 短链
     */
    @Async("asyncServiceExecutor") // 耗时操作放进线程池去操作,注意：异步方法使用注解@Async的返回值只能为void或者Future
    public void updateUrlViews(HttpServletRequest request, String shortURL, String tenantId) {
        // 首先更新s_url_map表里的views字段
        shortUrlDao.updateUrlViews(shortURL);
        // 然后插入访问日志表
        String accessIp = IpAddressUtils.getIpAddress(request);
        String accessAddress = AddressUtils.getAddressByIP(accessIp);
        String accessTime = DateTool.getCurrentTime();
        String accessUserAgent = JsonUtils.writeValueAsString(UserAgentUtils.getUserAgent(request));

        shortUrlDao.insertAccessLogs(shortURL, accessIp, accessAddress, accessTime, accessUserAgent, tenantId);
    }
}

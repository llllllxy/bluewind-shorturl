package com.bluewind.shorturl.common.config.filter;

import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.consts.HttpStatus;
import com.bluewind.shorturl.common.util.DateTool;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.module.api.service.ApiServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxingyu01
 * @date 2022-06-19 21:05
 * @description
 **/
@WebFilter(filterName = "ApiFilter",
        urlPatterns = {"/api/*"},
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class ApiFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ApiFilter.class);

    private static final String TENANT_API_CONFIG = "tenant_api_config";

    public ApiFilter() {
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ApiServiceImpl apiServiceImpl;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String access_key = httpRequest.getHeader("access_key");
            String reqtime = httpRequest.getHeader("reqtime");
            // access_key + access_key_secret + reqtime然后进行MD5加密(16进制的)
            String signature = httpRequest.getHeader("signature");
            if (logger.isDebugEnabled()) {
                logger.debug("ApiFilter doFilter access_key : {}, signature: {}, reqtime: {}", access_key, signature, reqtime);
            }
            if (StringUtils.isBlank(access_key)) {
                outWrite(response, "access_key不允许为空，认证未通过!");
                return;
            }
            if (StringUtils.isBlank(signature)) {
                outWrite(response, "signature不允许为空，认证未通过!");
                return;
            }

            // 判断时间戳是否合格
            if (StringUtils.isBlank(reqtime)) {
                outWrite(response, "reqtime不允许为空，认证未通过!");
                return;
            } else { // 判断时间戳是否是前后五分钟内
                String nowTime = DateTool.getNowTime();
                try {
                    long timeDiff = DateTool.timeDiffSeconds(nowTime, reqtime);
                    // 前后超过五分钟的时间戳也不合格
                    if (timeDiff > 300) {
                        outWrite(response, "reqtime时间戳只有在当前时间的前后5分钟内有效，认证未通过!");
                        return;
                    }
                } catch (Exception e) {
                    logger.error("ApiFilter -- doFilter -- timeDiffSeconds - Exception = {e}", e);
                    outWrite(response, "reqtime格式不正确，认证未通过!");
                    return;
                }
            }

            Map<String, Object> configMap = new HashMap<>();

            String cacheStr = redisTemplate.opsForValue().get(TENANT_API_CONFIG + ":" + access_key);
            if (StringUtils.isNotBlank(cacheStr)) {
                configMap = (Map<String, Object>) JsonUtils.readValue(cacheStr, Map.class);
            } else {
                // redis缓存里拿不到的话，就从数据库里查
                configMap = apiServiceImpl.getConfigData(access_key);
                if (configMap != null && !configMap.isEmpty()) {
                    // 缓存到redis中，减小数据库压力
                    redisTemplate.opsForValue().set(TENANT_API_CONFIG + ":" + access_key, JsonUtils.writeValueAsString(configMap) , 300, TimeUnit.SECONDS );
                }
            }
            if (configMap != null && !configMap.isEmpty()) {
                // 检验signature是否正确，规则是 access_key + access_key_secret + reqtime 然后进行MD5加密(16进制的)
                String access_key_secret = (String) configMap.get("access_key_secret");
                String mysignature = getMD5String(access_key + access_key_secret + reqtime);
                if (!signature.equalsIgnoreCase(mysignature)) {
                    outWrite(response, "signature不正确，认证未通过!");
                    return;
                }
                // 全部验证通过，放入ThreadLocal
                ApiFilterHolder.set(configMap);
                chain.doFilter(request, response);
            } else {
                outWrite(response, "您的access_key_secret不正确，认证未通过!");
            }
        } catch (Exception e) {
            logger.error("ApiFilter -- doFilter - Exception = {e}", e);
            outWrite(response, "毁灭性错误，请联系客服处理!");
        } finally {
            // ThreadLocal用完一定要remove
            ApiFilterHolder.clear();
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("ApiFilter init config : {}", config);
        }
    }


    @Override
    public void destroy() {
        if (logger.isDebugEnabled()) {
            logger.debug("ApiFilter destroy");
        }
    }


    public void outWrite(ServletResponse response, String message) throws IOException {
        Result result = Result.create(HttpStatus.ERROR, message);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.append(JsonUtils.writeValueAsString(result));
        out.close();
    }


    /**
     * Md5加密
     *
     * @param plainText
     * @return
     */
    public static String getMD5String(String plainText) {
        byte[] temp;
        StringBuilder buffer = new StringBuilder();
        try {
            temp = plainText.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(temp);
            temp = md.digest();
            int i = 0;
            for (int offset = 0; offset < temp.length; offset++) {
                i = temp[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buffer.append("0");
                buffer.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


}

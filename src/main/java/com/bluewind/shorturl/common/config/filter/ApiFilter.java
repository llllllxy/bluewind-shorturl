package com.bluewind.shorturl.common.config.filter;


import com.bluewind.shorturl.common.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
/**
 * @author liuxingyu01
 * @date 2022-06-19 21:05
 * @description
 **/
@WebFilter(filterName = "ItfcCommonFilter",
        urlPatterns = {"/anon/itfc/*"},
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class ApiFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiFilter.class);

    private final static String TIME_FORMAT = "yyyyMMddHHmmss";

    private static final String ITFC_IDENTITY_CONFIG = "itfc_identity_config";

    public ApiFilter() {
    }

    public void destroy() {
        if (logger.isDebugEnabled()) {
            logger.debug("ApiFilter destroy");
        }
    }


    private static ItfcCommonFilterMapper itfcCommonFilterMapper;

    private static ItfcCommonFilterMapper getItfcCommonFilterMapper() {
        if (itfcCommonFilterMapper == null) {
            Object bean = SpringContextUtil.getBean("itfcCommonFilterMapper");
            if (bean == null) {
                logger.error("itfcCommonFilterMapper bean is null!");
            }
            itfcCommonFilterMapper = (ItfcCommonFilterMapper) bean;
        }
        return itfcCommonFilterMapper;
    }


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String sysZone = "";
        try {
            sysZone = RuleUtil.getRuleValue("SYS_ZONE", "00");
        } catch (Exception e) {
            sysZone = "17"; // 江苏
        }
        if (logger.isDebugEnabled()) {
            logger.debug("ItfcCommonFilter -- doFilter -- sysZone = {}", sysZone);
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 加了配置的才会走这个过滤器，所以不会对江苏产生影响
        if (StringUtils.isNotBlank(sysZone) && !"17".equals(sysZone)) {
            String ak = httpRequest.getHeader("ak");
            String reqtime = httpRequest.getHeader("reqtime");
            // ak + sk + reqtime然后进行MD5加密(16进制的)
            String signature = httpRequest.getHeader("signature");

            if (logger.isDebugEnabled()) {
                logger.debug("ItfcCommonFilter doFilter ak : {}, signature: {}, reqtime: {}", ak, signature, reqtime);
            }

            if (StringUtils.isBlank(ak)) {
                outWrite(response, "ak不允许为空，认证未通过!");
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
                String nowTime = getNowTime();
                try {
                    long timeDiff = timeDiffSeconds(nowTime, reqtime);
                    // 超过五分钟的时间戳也不合格
                    if (timeDiff > 300) {
                        outWrite(response, "reqtime时间戳只有在当前时间的前后5分钟内有效，认证未通过!");
                        return;
                    }
                } catch (Exception e) {
                    logger.debug("ItfcCommonFilter -- doFilter -- timeDiffSeconds - Exception = {e}", e);
                    outWrite(response, "reqtime格式不正确，认证未通过!");
                    return;
                }
            }

            Map configMap = new HashMap();

            String cacheStr = RedisUtil.get(ITFC_IDENTITY_CONFIG + ":" + ak);
            if (StringUtils.isNotBlank(cacheStr)) {
                configMap = JSON.parseObject(cacheStr);
            } else {
                // redis里拿不到的话，就从数据库里查
                configMap = getItfcCommonFilterMapper().getConfigData(ak);
                if (MapUtils.isNotEmpty(configMap)) {
                    // 缓存到redis中，减小数据库压力
                    String configMapJson = JSON.toJSONString(configMap);
                    RedisUtil.set(ITFC_IDENTITY_CONFIG + ":" + ak, configMapJson, 300);
                }
            }
            if (MapUtils.isNotEmpty(configMap)) {
                // 检验signature是否正确，规则是 ak + sk 然后进行MD5加密(16进制的)
                String sk = (String) configMap.get("sk");
                String mysignature = getMD5String(ak + sk + reqtime);
                if (!signature.equalsIgnoreCase(mysignature)) {
                    outWrite(response, "signature不正确，认证未通过!");
                    return;
                }
                // 判断密钥是否过期
                String expireDate = configMap.get("expire_date") == null ? "" : configMap.get("expire_date").toString();
                if (StringUtils.isNotBlank(expireDate)) {
                    // 获取今天日期
                    String today = getToday("yyyyMMdd");
                    if (expireDate.compareTo(today) < 0) { // 有效期小于今天，那就过期了呀，不允许调用
                        outWrite(response, "您的密钥已过有效期，认证未通过!");
                    } else {
                        // 全部验证通过
                        chain.doFilter(request, response);
                    }
                } else { // 如果为空的话，则为无限期可用，直接通过
                    // 全部验证通过
                    chain.doFilter(request, response);
                }
            } else {
                outWrite(response, "您的ak不正确，认证未通过!");
            }
        } else {
            // 验证通过
            chain.doFilter(request, response);
        }
    }


    public void init(FilterConfig config) throws ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("ItfcCommonFilter init config : {}", config);
        }
    }


    public void outWrite(ServletResponse response, String message) throws IOException {
        JSONObject res = new JSONObject();
        res.put("code", "401");
        res.put("message", message);
        res.put("success", false);
        res.put("data", (Object) null);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.append(res.toString());
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


    /**
     * 获取当前日期 比如 DateTool.getToday("yyyyMMdd"); 返回值为 20120515
     *
     * @param format
     * @return
     */
    public static String getToday(String format) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(today);
    }


    /**
     * 获取当前时间，返回固定格式 yyyyMMddHHmmss
     *
     * @return
     */
    public static String getNowTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        return dateTimeFormatter.format(dateTime);
    }



    /**
     * 计量时间差 (time2 - time1)，返回秒数 比如 DateTool.timeDiffSeconds("2012-10-25
     * 02:49:15","2012-10-25 02:50:30") 返回值为 75
     *
     * @param previousTime
     * @param nextTime
     * @return
     */
    public static long timeDiffSeconds(String previousTime, String nextTime) {
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern(TIME_FORMAT);
        LocalDateTime previousDateTime = LocalDateTime.parse(previousTime, df1);

        DateTimeFormatter df2 = DateTimeFormatter.ofPattern(TIME_FORMAT);
        LocalDateTime nextDateTime = LocalDateTime.parse(nextTime, df2);

        Duration duration = Duration.between(previousDateTime, nextDateTime);
        long millis = duration.toMillis(); // 相差毫秒数(所有的)

        return Math.abs(Math.round(millis / 1000));
    }



    /**
     * 记录调用日志（先留下，暂时不用）
     *
     * @param request
     * @param ak
     * @param sk
     * @param note
     * @return
     */
    public static void signLog(ServletRequest request, String ak, String sk, String note) {


    }


}

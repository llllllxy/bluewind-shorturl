package com.bluewind.shorturl.common.config.security;

import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.consts.HttpStatus;
import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.JsonUtils;
import com.bluewind.shorturl.common.util.web.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxingyu01
 * @date 2022-03-26 20:48
 * @description 租户会话拦截器
 **/
@Component
public class TenantAuthenticeInterceptor implements HandlerInterceptor {
    final static Logger logger = LoggerFactory.getLogger(TenantAuthenticeInterceptor.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    /*
     * 进入controller层之前拦截请求
     * 返回值：表示是否将当前的请求拦截下来  false：拦截请求，请求别终止。true：请求不被拦截，继续执行
     * Object obj:表示被拦的请求的目标对象（controller中方法）
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 判断请求类型，如果是OPTIONS，直接返回
        String options = HttpMethod.OPTIONS.toString();
        if (options.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 先判断token是否为空
        String token = TenantAuthenticeUtil.getToken(request);
        if (logger.isInfoEnabled()) {
            logger.info("TenantAuthenticeInterceptor -- preHandle -- token = {}", token);
        }
        if (StringUtils.isBlank(token)) {
            responseError(request, response);
            return false;
        }

        // 再判断token是否存在
        String tenantInfoString = redisTemplate.opsForValue().get(SystemConst.SYSTEM_TENANT_KEY + ":" + token);
        if (StringUtils.isBlank(tenantInfoString)) {
            responseError(request, response);
            return false;
        }

        // 再判断token是否合法
        Map<String, Object> tenantInfo = (Map<String,Object>) JsonUtils.readValue(tenantInfoString, Map.class);
        if (logger.isInfoEnabled()) {
            logger.info("TenantAuthenticeInterceptor -- preHandle -- tenantInfo = {}", tenantInfo);
        }
        if (tenantInfo == null || tenantInfo.isEmpty()) {
            responseError(request, response);
            return false;
        }
        // 刷新会话缓存时长
        redisTemplate.expire(SystemConst.SYSTEM_TENANT_KEY + ":" + token, 1800, TimeUnit.SECONDS);

        TenantHolder.setTenant(tenantInfo);

        // 合格不需要拦截，放行
        return true;
    }

    /*
     * 处理请求完成后视图渲染之前的处理操作
     * 通过ModelAndView参数改变显示的视图，或发往视图的方法
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        //logger.info("TenantAuthenticeInterceptor -- postHandle -- 执行了");
    }

    /*
     * 视图渲染之后的操作
     */
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
        //logger.info("TenantAuthenticeInterceptor -- afterCompletion -- 执行了");
        TenantHolder.clearTenant();
    }


    /**
     * 无权限时的返回
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void responseError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 如果是ajax请求，直接返回401状态码
        if (ServletUtils.isAjaxRequest(request)) {
            Result result = Result.create(HttpStatus.UNAUTHORIZED, "会话已失效，请重新登录");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.write(JsonUtils.writeValueAsString(result));
            out.close();
        } else {
            // 拦截后跳转至登录页
            response.sendRedirect("/tenant/login");
        }
    }

}
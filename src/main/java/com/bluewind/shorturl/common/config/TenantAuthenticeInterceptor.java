package com.bluewind.shorturl.common.config;

import com.bluewind.shorturl.common.base.Result;
import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.JsonUtils;
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

/**
 * @author liuxingyu01
 * @date 2022-03-26 20:48
 * @description 会话拦截器
 **/
@Component
public class TenantAuthenticeInterceptor implements HandlerInterceptor {
    final static Logger logger = LoggerFactory.getLogger(TenantAuthenticeInterceptor.class);

    /*
     * 进入controller层之前拦截请求
     * 返回值：表示是否将当前的请求拦截下来  false：拦截请求，请求别终止。true：请求不被拦截，继续执行
     * Object obj:表示被拦的请求的目标对象（controller中方法）
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Map<String, Object> userInfo = (Map<String, Object>) request.getSession().getAttribute(SystemConst.TENANT_USER_KEY);
        logger.info("TenantAuthenticeInterceptor -- preHandle -- userInfo = {}", userInfo);

        if (userInfo == null || userInfo.isEmpty()) {
            logger.error("TenantAuthenticeInterceptor -- preHandle -- 请求已拦截");
            // 如果是ajax请求，直接返回302状态码
            if (isAjaxRequest(request)) {
                Result result = Result.create(401, "会话已失效");

                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.write(JsonUtils.writeValueAsString(result));
                out.close();
            } else {
                // 拦截后跳转至登录页
                response.sendRedirect("/tenant/login");
            }
            return false;
        }

        // 合格不需要拦截，放行
        return true;
    }

    /*
     * 处理请求完成后视图渲染之前的处理操作
     * 通过ModelAndView参数改变显示的视图，或发往视图的方法
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        //System.out.println("执行了postHandle方法");
    }

    /*
     * 视图渲染之后的操作
     */
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
        //System.out.println("执行到了afterCompletion方法");
    }


    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1) {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
            return true;
        }

        String uri = request.getRequestURI();
        if (inStringIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (inStringIgnoreCase(ajax, "json", "xml")) {
            return true;
        }

        return false;
    }


    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(StringUtils.trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

}
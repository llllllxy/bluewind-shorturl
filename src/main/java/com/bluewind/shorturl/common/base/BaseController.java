package com.bluewind.shorturl.common.base;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BaseController {
    public static final String RESULT_ROWS = "rows";
    public static final String RESULT_TOTAL = "total";

    public BaseController() {
    }

    /**
     * 获取当前请求对象
     *
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return request;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前响应对象
     *
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = null;
        try {
            response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            return response;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取session
     *
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }
}

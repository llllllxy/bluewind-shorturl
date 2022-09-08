package com.bluewind.shorturl.common.config.security;

import com.bluewind.shorturl.common.consts.SystemConst;
import com.bluewind.shorturl.common.util.web.CookieUtils;
import com.bluewind.shorturl.common.util.web.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liuxingyu01
 * @date 2022-09-06 10:27
 * @description 租户会话工具类
 **/
public class TenantAuthenticeUtil {


    /**
     *
     * 获取租户会话token
     *
     * @param request HttpServletRequest
     * @return String
     */
    public static String getToken(HttpServletRequest request) {
        String token = "";
        if (request != null) {
            // 从请求中获取token，先从Header里取，取不到的话再从cookie里取（适配前后端分离的模式）
            token = request.getHeader(SystemConst.SYSTEM_TENANT_TOKEN);
            if (StringUtils.isBlank(token)) {
                token = CookieUtils.getCookie(request, SystemConst.SYSTEM_TENANT_TOKEN);
            }
        }
        return token;
    }

    /**
     * 获取租户会话token
     *
     * @return String
     */
    public static String getToken() {
        HttpServletRequest request = ServletUtils.getRequest();
        String token = "";
        if (request != null) {
            // 从请求中获取token，先从Header里取，取不到的话再从cookie里取（适配前后端分离的模式）
            token = request.getHeader(SystemConst.SYSTEM_TENANT_TOKEN);
            if (StringUtils.isBlank(token)) {
                token = CookieUtils.getCookie(request, SystemConst.SYSTEM_TENANT_TOKEN);
            }
        }
        return token;
    }

}

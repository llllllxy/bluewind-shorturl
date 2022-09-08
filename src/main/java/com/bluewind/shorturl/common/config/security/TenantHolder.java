package com.bluewind.shorturl.common.config.security;

import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author liuxingyu01
 * @date 2022-06-14 13:58
 * @description 本地线程变量-缓存租户会话信息
 **/
public class TenantHolder {
    private final static ThreadLocal<Map<String, Object>> tenant = new ThreadLocal<>();

    public static Map<String, Object> getTenant() {
        return tenant.get();
    }

    public static String getTenantId() {
        Map<String, Object> temp = getTenant();
        if (CollectionUtils.isEmpty(temp)) {
            return "";
        } else {
            return Optional.ofNullable(temp.get("tenant_id")).orElse("").toString();
        }
    }

    public static String getTenantAccount() {
        Map<String, Object> temp = getTenant();
        if (CollectionUtils.isEmpty(temp)) {
            return "";
        } else {
            return Optional.ofNullable(temp.get("tenant_account")).orElse("").toString();
        }
    }

    public static void setTenant(Map<String, Object> t) {
        tenant.set(t);
    }

    public static void clearTenant() {
        tenant.remove();
    }
}

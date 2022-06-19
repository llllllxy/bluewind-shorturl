package com.bluewind.shorturl.common.config.filter;

import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Optional;

public class ApiFilterHolder {
    private final static ThreadLocal<Map<String, Object>> apiConfig = new ThreadLocal<>();

    public static Map<String, Object> get() {
        return apiConfig.get();
    }

    public static String getTenantId() {
        Map<String, Object> temp = get();
        if (CollectionUtils.isEmpty(temp)) {
            return "";
        } else {
            return Optional.ofNullable(temp.get("tenant_id")).orElse("").toString();
        }
    }

    public static void set(Map<String, Object> t) {
        apiConfig.set(t);
    }

    public static void clear() {
        apiConfig.remove();
    }
}

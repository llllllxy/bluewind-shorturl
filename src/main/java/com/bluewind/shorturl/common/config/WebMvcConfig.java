package com.bluewind.shorturl.common.config;

import com.bluewind.shorturl.common.config.logtracking.TraceIdInterceptor;
import com.bluewind.shorturl.common.config.security.TenantAuthenticeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * WebMvcConfig
 *
 * @author liuxingyu01
 * @date 2022-03-13-13:47
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    @Autowired
    private TraceIdInterceptor traceIdInterceptor;

    @Autowired
    private TenantAuthenticeInterceptor tenantAuthenticeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 配置需要拦截上下文列表（除了配置的这些，剩下的都不拦）
        // PC端租户管理开始
        List<String> tenantIncludePaths = new ArrayList<>();
        tenantIncludePaths.add("/tenant/index"); // 租户首页
        tenantIncludePaths.add("/tenant/logout"); // 租户退出登录
        tenantIncludePaths.add("/tenant/profile"); // 租户个人信息页
        tenantIncludePaths.add("/tenant/updateProfile"); // 租户修改个人信息
        tenantIncludePaths.add("/tenant/akandsk"); // 凭证密钥信息页
        tenantIncludePaths.add("/tenant/resetAkandsk"); // 租户修改凭证密钥信息页
        tenantIncludePaths.add("/tenant/url/**"); // 租户url短链管理
        tenantIncludePaths.add("/tenant/log/**"); // 租户短链日志管理
        tenantIncludePaths.add("/tenant/dashboard/**"); // 租户仪表盘
        tenantIncludePaths.add("/tenant/statistic/**"); // 租户数据统计管理

        // 移动端(租户)开始
        tenantIncludePaths.add("/mobile/navigation"); // 移动端-获取导航
        tenantIncludePaths.add("/mobile/wxcp/unbind"); // 企业微信-用户解绑
        tenantIncludePaths.add("/mobile/wxcp/jsConfig"); // 企业微信-jsConfig


        // 注册租户会话拦截器
        registry.addInterceptor(tenantAuthenticeInterceptor)
                .addPathPatterns(tenantIncludePaths); // 只拦截这几个，其余的不拦截

        // 注册限流拦截器
        registry.addInterceptor(accessLimitInterceptor)
                .addPathPatterns("/**");

        // 注册traceId拦截器
        registry.addInterceptor(traceIdInterceptor)
                .addPathPatterns("/**");
    }

}

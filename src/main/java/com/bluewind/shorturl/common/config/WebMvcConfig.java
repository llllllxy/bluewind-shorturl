package com.bluewind.shorturl.common.config;

import com.bluewind.shorturl.common.config.security.TenantAuthenticeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuxingyu01
 * @date 2022-03-13-13:47
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    AccessLimitInterceptor accessLimitInterceptor;

    @Autowired
    TenantAuthenticeInterceptor tenantAuthenticeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置拦截上下文列表
        List<String> pathList = new ArrayList<>();
        pathList.add("/tenant/index"); // 租户首页
        pathList.add("/tenant/logout"); // 租户退出登录
        pathList.add("/tenant/profile"); // 租户个人信息页
        pathList.add("/tenant/updateProfile"); // 租户修改个人信息
        pathList.add("/tenant/url/**"); // 租户url短链管理
        pathList.add("/tenant/log/**"); // 租户短链日志管理
        pathList.add("/tenant/dashboard/**"); // 租户仪表盘
        pathList.add("/tenant/statistic/**"); // 租户数据统计管理



        // 注册会话拦截器
        registry.addInterceptor(tenantAuthenticeInterceptor)
                .addPathPatterns(pathList); // 只拦截这几个，其余的不拦截

        // 注册限流拦截器
        registry.addInterceptor(accessLimitInterceptor);
    }

}

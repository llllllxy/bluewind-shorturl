package com.bluewind.shorturl.common.config;

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
    AuthenticeInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> pathList = new ArrayList<>();
        pathList.add("/manage/**");
        pathList.add("/index/index");

        // 注册会话拦截器
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns(pathList); // 只拦截这几个，其余的不拦截

        // 注册限流拦截器
        registry.addInterceptor(accessLimitInterceptor);
    }

}

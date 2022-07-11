package com.bluewind.shorturl.common.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * @author liuxingyu01
 * @date 2022-07-08 17:08
 * @description Jackson全局转化long类型为String，解决jackson序列化时long类型缺失精度问题
 **/

@Configuration
public class JacksonConfig {
    /**
     * 自定义序列化类型转换
     * 注：此处解决Long型转换后，前端js损失精度的问题，将Long型转换为字符串类型
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Jackson2ObjectMapperBuilderCustomizer customizer = jacksonObjectMapperBuilder ->
                jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance)
                        .serializerByType(Long.TYPE, ToStringSerializer.instance);
        return customizer;
    }

}

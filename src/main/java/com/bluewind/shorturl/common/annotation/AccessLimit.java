package com.bluewind.shorturl.common.annotation;

import java.lang.annotation.*;

/**
 * @author liuxingyu01
 * @date 2022-03-11-16:49
 * @description one minutes request frequency is Fifty times, exceeding the wait five minutes
 * 一分钟的请求频率是五十次，超过了，则禁止访问300秒
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /**
     * 限制周期(秒)
     */
    int seconds();

    /**
     * 规定周期内限制次数
     */
    int maxCount();

    /**
     * 触发限制时的消息提示
     */
    String msg() default "操作频率过高！";

}

package com.bluewind.shorturl.common.annotation;

import java.lang.annotation.*;

/**
 * @author liuxingyu01
 * @date 2022-03-11-16:49
 * @description seconds 秒内只能执行 maxCount 次
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
     * 规定周期内最大次数
     */
    int maxCount();

    /**
     * 触发限制时的消息提示
     */
    String msg() default "操作频率过高，请稍后再试！";
}

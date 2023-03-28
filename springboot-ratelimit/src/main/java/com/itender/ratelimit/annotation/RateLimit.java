package com.itender.ratelimit.annotation;

import com.itender.ratelimit.enums.LimitTypeEnum;

import java.lang.annotation.*;

import static com.itender.ratelimit.enums.LimitTypeEnum.CUSTOMER;


/**
 * @author itender
 * @date 2022/5/30 18:47
 * @desc
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RateLimit {

    /**
     * 名称
     */
    String name() default "";

    /**
     * key
     */
    String key() default "";

    /**
     * Key的前缀
     */
    String prefix() default "";

    /**
     * 给定的时间范围 单位(秒)
     */
    int period();

    /**
     * 一定时间内最多访问次数
     */
    int count();

    /**
     * 限流的类型(用户自定义key 或者 请求ip)
     */
    LimitTypeEnum limitType() default CUSTOMER;
}

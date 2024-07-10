package com.itender.redis.annotation;

import java.lang.annotation.*;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:51
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DoubleCache {
    /**
     * 缓存名称
     *
     * @return
     */
    String cacheName();

    /**
     * 缓存的key，支持springEL表达式
     *
     * @return
     */
    String[] key();

    /**
     * 过期时间，单位：秒
     *
     * @return
     */
    long expireTime() default 120;

    /**
     * 缓存类型
     *
     * @return
     */
    CacheType type() default CacheType.FULL;

    enum CacheType {
        /**
         * 存取
         */
        FULL,

        /**
         * 只存
         */
        PUT,

        /**
         * 删除
         */
        DELETE
    }
}

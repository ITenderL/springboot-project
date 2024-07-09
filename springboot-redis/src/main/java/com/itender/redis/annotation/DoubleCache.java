package com.itender.redis.annotation;

import java.lang.annotation.*;

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
    String key();

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

    public enum CacheType {
        FULL,   //存取
        PUT,    //只存
        DELETE  //删除
    }
}

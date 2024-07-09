package com.itender.redis.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:16
 * @description
 */
@Configuration
public class CaffeineConfig {

    /**
     * Caffeine 配置类
     *  initialCapacity：初始缓存空间大小
     *  maximumSize：缓存的最大数量，设置这个值避免内存溢出
     *  expireAfterWrite：指定缓存的过期时间，是最后一次写操作的一个时间
     *
     * @return
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始大小
                .initialCapacity(128)
                //最大数量
                .maximumSize(1024)
                //过期时间
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager=new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(128)
                .maximumSize(1024)
                .expireAfterWrite(60, TimeUnit.SECONDS));
        return cacheManager;
    }
}

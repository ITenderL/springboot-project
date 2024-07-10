package com.itender.redis.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Lists;
import com.itender.redis.annotation.DoubleCache;
import com.itender.redis.constant.RedisConstants;
import com.itender.redis.util.DoubleCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:51
 * @description
 */
@Slf4j
@Component
@Aspect
public class DoubleCacheAspect {

    @Resource
    private Cache<String, Object> caffeineCache;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.itender.redis.annotation.DoubleCache)")
    public void doubleCachePointcut() {
    }

    @Around("doubleCachePointcut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 拼接解析springEl表达式的map
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i], args[i]);
        }
        DoubleCache annotation = method.getAnnotation(DoubleCache.class);
        String elResult = DoubleCacheUtil.arrayParse(Lists.newArrayList(annotation.key()), treeMap);
        String realKey = annotation.cacheName() + RedisConstants.COLON + elResult;
        // 强制更新
        if (annotation.type() == DoubleCache.CacheType.PUT) {
            Object object = point.proceed();
            redisTemplate.opsForValue().set(realKey, object, annotation.expireTime(), TimeUnit.SECONDS);
            caffeineCache.put(realKey, object);
            return object;
        }
        // 删除
        else if (annotation.type() == DoubleCache.CacheType.DELETE) {
            redisTemplate.delete(realKey);
            caffeineCache.invalidate(realKey);
            return point.proceed();
        }
        // 读写，查询Caffeine
        Object caffeineCacheObj = caffeineCache.getIfPresent(realKey);
        if (Objects.nonNull(caffeineCacheObj)) {
            log.info("get data from caffeine");
            return caffeineCacheObj;
        }
        // 查询Redis
        Object redisCache = redisTemplate.opsForValue().get(realKey);
        if (Objects.nonNull(redisCache)) {
            log.info("get data from redis");
            caffeineCache.put(realKey, redisCache);
            return redisCache;
        }
        log.info("get data from database");
        Object object = point.proceed();
        if (Objects.nonNull(object)) {
            // 写入Redis
            log.info("get data from database write to cache: {}", object);
            redisTemplate.opsForValue().set(realKey, object, annotation.expireTime(), TimeUnit.SECONDS);
            // 写入Caffeine
            caffeineCache.put(realKey, object);
        }
        return object;
    }
}

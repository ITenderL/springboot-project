package com.itender.ratelimit.interceptor;

import com.google.common.collect.ImmutableList;
import com.itender.ratelimit.annotation.RateLimit;
import com.itender.ratelimit.enums.LimitTypeEnum;
import com.itender.ratelimit.exception.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

import static com.itender.ratelimit.enums.ResultEnum.REQUEST_TOO_FREQUENTLY;
import static com.itender.ratelimit.enums.ResultEnum.SERVER_EXCEPTION;


/**
 * @author itender
 * @date 2022/6/2 14:33
 * @desc 切面
 */
@Slf4j
@Aspect
@Configuration
public class LimitInterceptor {

    private final RedisTemplate<String, Serializable> limitRedisTemplate;

    @Autowired
    public LimitInterceptor(RedisTemplate<String, Serializable> limitRedisTemplate) {
        this.limitRedisTemplate = limitRedisTemplate;
    }

    private static final String UNKNOWN = "unknown";

    @Around("@annotation(com.itender.ratelimit.annotation.RateLimit)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RateLimit annotation = method.getAnnotation(RateLimit.class);
        LimitTypeEnum limitType = annotation.limitType();
        String name = annotation.name();
        int limitPeriod = annotation.period();
        int limitCount = annotation.count();
        String key = getLimiterKey(method, limitType, annotation);
        ImmutableList<String> keys = ImmutableList.of(StringUtils.join(annotation.prefix(), key));
        try {
            String luaScript = buildLuaScript();
            RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            Long count = limitRedisTemplate.execute(redisScript, keys, limitCount, limitPeriod);
            log.info("Access try count is {} for name: {} and key: {}", count, name, key);
            assert count != null;
            if (limitCount < count.intValue()) {
                throw new RateLimitException(REQUEST_TOO_FREQUENTLY);
            }
            return pjp.proceed();
        } catch (Throwable e) {
            throw new RateLimitException(SERVER_EXCEPTION);
        }
    }

    /**
     * 获取限流的key
     *
     * @param method
     * @param limitType
     * @param annotation
     * @return
     */
    private String getLimiterKey(Method method, LimitTypeEnum limitType, RateLimit annotation) {
        String key;
        switch (limitType) {
            case IP:
                key = getIpAddress();
                break;
            case CUSTOMER:
                key = annotation.key();
                break;
            default:
                key = StringUtils.upperCase(method.getName());
        }
        return key;
    }

    /**
     * 组装lua脚本
     *
     * @return
     */
    public String buildLuaScript() {
        StringBuilder lua = new StringBuilder();
        lua.append("local c");
        lua.append("\nc = redis.call('get', KEYS[1])");
        // 调用不超过最大值，则直接返回
        lua.append("\nif c and tonumber(c) > tonumber(ARGV[1]) then");
        lua.append("\nreturn c;");
        lua.append("\nend");
        // 执行计算器自加
        lua.append("\nc = redis.call('incr', KEYS[1])");
        lua.append("\nif tonumber(c) == 1 then");
        // 从第一次调用开始限流，设置对应键值的过期
        lua.append("\nredis.call('expire', KEYS[1], ARGV[2])");
        lua.append("\nend");
        lua.append("\nreturn c;");
        return lua.toString();
    }

    /**
     * 获取请求的ip
     *
     * @return
     */
    public String getIpAddress() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "";
        if (Objects.isNull(requestAttributes)) {
            return ip;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

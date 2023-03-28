package com.itender.ratelimit.controller;

import com.itender.ratelimit.annotation.RateLimit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.itender.ratelimit.enums.LimitTypeEnum.CUSTOMER;
import static com.itender.ratelimit.enums.LimitTypeEnum.IP;

/**
 * @author itender
 * @date 2022/6/2 17:45
 * @desc 分布式限流
 */
@RestController
@RequestMapping("rateLimit")
public class RateLimitController {

    private static final String UNKNOWN = "unknown";
    private static final AtomicInteger ATOMIC_INTEGER_1 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_2 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_3 = new AtomicInteger();


    @RateLimit(key = "limitTest", period = 10, count = 3)
    @GetMapping("/test1")
    public int testLimiter1() {
        return ATOMIC_INTEGER_1.incrementAndGet();
    }


    @RateLimit(key = "customer_limit_test", period = 10, count = 3, limitType = CUSTOMER)
    @GetMapping("/test2")
    public int testLimiter2() {

        return ATOMIC_INTEGER_2.incrementAndGet();
    }


    @RateLimit(key = "ip_limit_test", period = 10, count = 3, limitType = IP)
    @GetMapping("/test3")
    public int testLimiter3() {
        return ATOMIC_INTEGER_3.incrementAndGet();
    }

    @GetMapping("/ip")
    public void getIpAddress() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "";
        if (Objects.isNull(requestAttributes)) {
            return;
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
        String host = request.getRemoteHost();
    }

}

package com.itender.redis.controller;

import com.itender.redis.service.RedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itender
 * @date 2022/12/2 17:30
 * @desc
 */
@RestController
@RequestMapping("lock")
public class RedisLockController {

    private final RedisLockService redisLockService;

    @Autowired
    public RedisLockController(RedisLockService redisLockService) {
        this.redisLockService = redisLockService;
    }

    @GetMapping("/redis")
    public String redisLock() {
        return redisLockService.redisLock();
    }

    @GetMapping("/redisson")
    public String redissonLock() {
        return redisLockService.redissonLock();
    }
}

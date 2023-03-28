package com.itender.redis.service;

/**
 * @author itender
 * @date 2022/12/2 17:31
 * @desc
 */
public interface RedisLockService {
    /**
     * redis实现分布式锁
     *
     * @return
     */
    String redisLock();

    /**
     * redisson 实现分布式锁
     *
     * @return
     */
    String redissonLock();
}

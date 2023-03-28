package com.itender.redis.service.impl;

import com.itender.redis.service.RedisLockService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author itender
 * @date 2022/12/2 17:32
 * @desc
 */
@Slf4j
@Service
public class RedisLockServiceImpl implements RedisLockService {
    private final StringRedisTemplate stringRedisTemplate;

    private final Redisson redisson;

    @Autowired
    public RedisLockServiceImpl(StringRedisTemplate stringRedisTemplate, Redisson redisson) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisson = redisson;
    }

    @Override
    public String redisLock() {
        String lua = "";
        stringRedisTemplate.opsForValue().setIfAbsent("", "", 10L, TimeUnit.SECONDS);
        return "redis lock is ok!";
    }

    @Override
    public String redissonLock() {
        RLock lock = redisson.getLock("lock");
        try {
            boolean locked = lock.tryLock(-1, 5L, TimeUnit.SECONDS);
            if (locked) {
                log.info("线程ID:{}，获取到了锁！", Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程ID：{}执行出错", Thread.currentThread().getName());
        } finally {
            lock.unlock();
            log.info("线程ID:{}，解锁成功！", Thread.currentThread().getName());
        }
        return "redisson lock is ok!";
    }
}

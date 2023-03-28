package com.itender.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author itender
 * @date 2022/12/2 11:47
 * @desc 基于redis和redisson实现分布式锁
 */
@Slf4j
@Component
public class RedisLock {

    private final StringRedisTemplate stringRedisTemplate;

    private final Redisson redisson;

    @Autowired
    public RedisLock(StringRedisTemplate stringRedisTemplate, Redisson redisson) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisson = redisson;
    }

    public String redisLock() {
        String lua = "";
        return "redis lock is ok!";
    }


    public String redissonLock() {
        RLock lock = redisson.getLock("lock");
        lock.lock(10L, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功，执行后续代码。线程 ID：" + Thread.currentThread().getId());
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            //TODO
        } finally {
            lock.unlock();
            // 3.解锁
            System.out.println("Finally，释放锁成功。线程 ID：" + Thread.currentThread().getId());
        }

        return "redisson lock is ok!";
    }
}

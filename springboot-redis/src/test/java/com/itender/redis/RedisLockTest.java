package com.itender.redis;

import com.itender.redis.service.RedisLockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author itender
 * @date 2022/12/2 14:35
 * @desc
 */
@SpringBootTest
public class RedisLockTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisLockService redisLockService;

    @Test
    void redisTest() {
        redisTemplate.opsForValue().set("key", "itender");
        System.out.println(redisTemplate.opsForValue().get("key"));
        System.out.println(redisTemplate.opsForValue().get("lock"));
    }

    @Test
    void redissonTest() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                redisLockService.redissonLock();
            }, String.valueOf(i)).start();
        }
        TimeUnit.SECONDS.sleep(15);
    }

    @Test
    void redissonTest1() {
        // redisLockService.redissonLock();
        new Thread(() -> redisLockService.redissonLock(), "").start();

    }
}

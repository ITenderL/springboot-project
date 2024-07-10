package com.itender.redis;

import com.itender.redis.pojo.EstimatedArrivalDateEntity;
import com.itender.redis.service.RedisLockService;
import com.itender.redis.util.DoubleCacheUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author itender
 * @date 2022/12/2 14:35
 * @desc
 */
@SpringBootTest
class RedisLockTest {
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

    @Test
    void test() {
        String elString = "#estimatedArrivalDate.city";
        String elString2 = "#estimatedArrivalDate.deliveryDate";
        String elString3 = "#estimatedArrivalDate.warehouseId";
        String elString4 = "#user";

        TreeMap<String, Object> map = new TreeMap<>();
        EstimatedArrivalDateEntity estimatedArrivalDate = new EstimatedArrivalDateEntity();
        estimatedArrivalDate.setCity("深圳");
        estimatedArrivalDate.setDeliveryDate("2024-05-28");
        estimatedArrivalDate.setWarehouseId("60");
        map.put("estimatedArrivalDate", estimatedArrivalDate);
        map.put("user", "Hydra");

        String val = DoubleCacheUtil.parse(elString, map);
        String val2 = DoubleCacheUtil.parse(elString2, map);
        String val3 = DoubleCacheUtil.parse(elString3, map);
        String val4 = DoubleCacheUtil.parse(elString4, map);

        System.out.println(val);
        System.out.println(val2);
        System.out.println(val3);
        System.out.println(val4);
    }
}

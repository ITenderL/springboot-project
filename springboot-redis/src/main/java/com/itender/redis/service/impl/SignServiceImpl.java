package com.itender.redis.service.impl;

import com.itender.redis.constant.RedisConstants;
import com.itender.redis.service.SignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author itender
 * @date 2023/3/28 19:48
 * @desc
 */
@Slf4j
@Service
public class SignServiceImpl implements SignService {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public SignServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void sign(Long userId) {
        // 1. 获取登录用户
        // Long userId = UserHolder.getUser().getId();
        // 2. 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3. 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = RedisConstants.USER_SIGN_KEY + userId + keySuffix;
        // 4. 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5. 写入redis setbit key offset 1
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1L, true);
    }

    /**
     * 签到次数
     *
     * @param userId
     */
    @Override
    public Long signCount(Long userId) {
        // 1. 获取登录用户
        // Long userId = UserHolder.getUser().getId();
        // 2. 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3. 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = RedisConstants.USER_SIGN_KEY + userId + keySuffix;
        // 4. 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5. 获取本月截至今天为止的所有的签到记录，返回的是一个十进制的数字 BITFIELD sign:5:202301 GET u3 0
        List<Long> result = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));
        // 没有任务签到结果
        if (result == null || result.isEmpty()) {
            return 0L;
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return 0L;
        }
        // 6. 循环遍历
        Long count = 0L;
        // 如果为0，签到结束
        while ((num & 1) != 0) {
            // 6.1 让这个数字与1 做与运算，得到数字的最后一个bit位 判断这个数字是否为0
            count++;
            num >>>= 1;
        }
        return count;
    }
}

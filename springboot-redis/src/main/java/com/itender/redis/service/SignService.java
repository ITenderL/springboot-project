package com.itender.redis.service;

/**
 * @author itender
 * @date 2023/3/28 19:47
 * @desc
 */
public interface SignService {
    /**
     * 签到
     *
     * @param userId
     * @return
     */
    void sign(Long userId);

    /**
     * 签到次数
     *
     * @param userId
     */
    Long signCount(Long userId);
}

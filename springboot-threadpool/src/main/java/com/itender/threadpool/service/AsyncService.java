package com.itender.threadpool.service;

import com.itender.threadpool.mapper.LogOutputMapper;
import com.itender.threadpool.entity.LogOutputResult;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author itender
 * @date 2022/6/9 15:16
 * @desc
 */
public interface AsyncService {

    /**
     * 执行异步任务
     *
     * @param logOutputResults
     * @param logOutputMapper
     * @param countDownLatch
     */
    void executeAsync(List<LogOutputResult> logOutputResults, LogOutputMapper logOutputMapper, CountDownLatch countDownLatch);
}

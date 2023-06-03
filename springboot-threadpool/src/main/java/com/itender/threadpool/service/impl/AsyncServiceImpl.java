package com.itender.threadpool.service.impl;

import com.itender.threadpool.mapper.LogOutputMapper;
import com.itender.threadpool.entity.LogOutputResult;
import com.itender.threadpool.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author itender
 * @date 2022/6/9 15:16
 * @desc
 */
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    @Async("visibleAsyncServiceExecutor")
    @Override
    public void executeAsync(List<LogOutputResult> logOutputResults, LogOutputMapper logOutputResultMapper, CountDownLatch countDownLatch) {
        log.info("{} execute async started!", Thread.currentThread().getName());
        try {
            log.warn("start executeAsync");
            //异步线程要做的事情
            logOutputResultMapper.bachInsert(logOutputResults);
            log.warn("end executeAsync");
        } finally {
            // 很关键, 无论上面程序是否异常必须执行countDown,否则await无法释放
            countDownLatch.countDown();
        }
        log.info("{} execute async finished!", Thread.currentThread().getName());
    }
}

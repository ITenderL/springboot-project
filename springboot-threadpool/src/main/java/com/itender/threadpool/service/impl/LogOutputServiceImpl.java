package com.itender.threadpool.service.impl;

import com.google.common.collect.Lists;
import com.itender.threadpool.entity.LogOutputResult;
import com.itender.threadpool.mapper.LogOutputMapper;
import com.itender.threadpool.service.AsyncService;
import com.itender.threadpool.service.LogOutputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author itender
 * @date 2023/1/29 10:37
 * @desc
 */
@Slf4j
@Service
public class LogOutputServiceImpl implements LogOutputService {

    private final AsyncService asyncService;
    private final LogOutputMapper logOutputMapper;

    @Autowired
    public LogOutputServiceImpl(AsyncService asyncService, LogOutputMapper logOutputMapper) {
        this.asyncService = asyncService;
        this.logOutputMapper = logOutputMapper;
    }

    @Override
    public int multiThreadInsert() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("multiThread get data");
        List<LogOutputResult> logOutputResults = getTestResults();
        stopWatch.stop();
        List<List<LogOutputResult>> partitions = Lists.partition(logOutputResults, 200);
        CountDownLatch countDownLatch = new CountDownLatch(partitions.size());
        stopWatch.start("multiThread start insert");
        partitions.forEach(partition -> asyncService.executeAsync(partition, logOutputMapper, countDownLatch));
        // 保证所有的线程都执行完成后再往下走
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("阻塞异常: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        stopWatch.stop();
        log.info("多线程插入数据，总耗时：{}，耗时详情：{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
        return logOutputResults.size();
    }

    @Override
    public int singleThreadInsert() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("singleThread get data");
        List<LogOutputResult> logOutputResults = getTestResults();
        stopWatch.stop();
        List<List<LogOutputResult>> partitions = Lists.partition(logOutputResults, 200);
        stopWatch.start("singleThread start insert");
        partitions.forEach(logOutputMapper::singleBachInsert);
        stopWatch.stop();
        log.info("单线程插入数据，总耗时：{}，耗时详情：{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
        return logOutputResults.size();
    }

    /**
     * 获取测试数据
     *
     * @return
     */
    public static List<LogOutputResult> getTestResults() {
        List<LogOutputResult> results = Lists.newArrayList();
        for (int i = 1; i <= 1200000; i++) {
            LogOutputResult result = new LogOutputResult();
            String type = "get";
            int flag = i % 3;
            if (flag == 1) {
                type = "insert";
            } else if (flag == 2) {
                type = "update";
            }
            result
                    .setType(type)
                    .setOperator("itender_" + i)
                    .setCreateTime(new Date())
                    .setUpdateTime(new Date());
            results.add(result);
        }
        return results;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> future = CompletableFuture
                .completedFuture("hello!")
                .thenApply(s -> s + " world!")
                .thenApply(s -> s + " nice!");
        System.out.println(future.get(2, TimeUnit.SECONDS));


        CompletableFuture<Object> future1 = CompletableFuture
                .supplyAsync(LogOutputServiceImpl::getTestResults)
                .thenApply(result -> {
                    System.out.println(result.size());
                    return result.size();
                });
        System.out.println(future1.get());
    }
}

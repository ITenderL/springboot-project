package com.itender.threadpool.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.itender.threadpool.executors.VisibleThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author itender
 * @date 2022/6/9 15:04
 * @desc 线程池配置类
 */
@Slf4j
@Configuration
@EnableAsync
public class ExecutorConfig {

    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;
    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;
    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;
    @Value("${async.executor.thread.name.prefix}")
    private String namePrefix;

    // 线程工厂，用于为线程池中的每条线程命名
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("customer-executor-pool-%d")
            .setUncaughtExceptionHandler((thread, throwable) -> log.error("ThreadPool {} got exception", thread, throwable))
            .build();

    @Bean(name = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 任务队列大小
        executor.setQueueCapacity(queueCapacity);
        // 线程名称前缀
        executor.setThreadNamePrefix(namePrefix);
        // 拒绝策略
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * 自定义可见的线程池
     *
     * @return
     */
    @Bean(name = "visibleAsyncServiceExecutor")
    public Executor visibleAsyncServiceExecutor() {
        log.info("start visibleAsyncServiceExecutor");
        // 在这里修改
        ThreadPoolTaskExecutor executor = new VisibleThreadPoolTaskExecutor();
        // 配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 配置队列大小
        executor.setQueueCapacity(queueCapacity);
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix);
        // 配置线程工厂
        executor.setThreadFactory(namedThreadFactory);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }


    @Bean(name = "customerExecutor")
    public Executor customerExecutor() {
        log.info("start customerExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(4);
        // 最大线程数
        executor.setMaxPoolSize(6);
        // 任务队列大小
        executor.setQueueCapacity(20);
        // 线程名称前缀
        // executor.setThreadNamePrefix("completableFuture-async-");
        // 配置线程工厂
        executor.setThreadFactory(namedThreadFactory);
        // 拒绝策略
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }
}

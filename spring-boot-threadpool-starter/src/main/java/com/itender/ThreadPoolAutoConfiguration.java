package com.itender;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author itender
 * @date 2023/11/14/ 20:42
 * @desc
 */
@Configuration
public class ThreadPoolAutoConfiguration {
    @Bean
    @ConditionalOnClass(ThreadPoolExecutor.class)
    public ThreadPoolExecutor myThreadPool() {
        return new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));
    }
}

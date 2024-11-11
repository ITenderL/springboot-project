package com.springboot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author analytics
 * @date 2024/11/11 13:18
 * @description
 */
@Configuration
public class CustomerThreadPoolConfiguration {

    @Bean
    @ConditionalOnClass({ThreadPoolExecutor.class})
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
    }
}

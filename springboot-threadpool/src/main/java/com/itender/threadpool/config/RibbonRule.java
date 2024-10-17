package com.itender.threadpool.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author itender
 * @date 2022/6/9 15:04
 * @desc 线程池配置类
 */
@Configuration
public class RibbonRule {
    @Bean
    public IRule myRibbonRule(){
        return new RandomRule();// 随机负载均衡算法
    }
}

package com.itender;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author itender
 * @date 2022/6/9 15:08
 * @desc
 */
@MapperScan("com.itender.threadpool.mapper")
@SpringBootApplication
public class ThreadPoolApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThreadPoolApplication.class, args);
    }
}

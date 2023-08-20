package com.itender.clickhouse.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


/**
 * @author itender
 * @date 2023/6/9 16:03
 * @desc
 */
@Configuration
public class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.click")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }
}

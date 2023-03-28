package com.itender.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author itender
 * @date 2022/5/25 14:45
 * @desc kafka配置类
 */
@Configuration
public class KafkaInitialConfiguration {
    /**
     * 创建一个名为topic-test的Topic并设置分区数为8，分区副本数为2
     *
     * @return
     */
    @Bean
    public NewTopic initialTopic() {
        return new NewTopic("topic-test", 1, (short) 1);
    }

    /**
     * 如果要修改分区数，只需修改配置值重启项目即可
     * 修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小
     *
     * @return
     */
    @Bean
    public NewTopic updateTopic() {
        return new NewTopic("topic-callback", 1, (short) 1);
    }

    @Bean
    public NewTopic transactionTopic() {
        return new NewTopic("topic-transaction", 1, (short) 1);
    }
}

package com.itender.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author itender
 * @date 2022/8/2 16:26
 * @desc kafka集群测试
 */
@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void sendTest() {
        kafkaTemplate.send("itender-kafka", "test send message to itender-kafka topic!");
    }
}

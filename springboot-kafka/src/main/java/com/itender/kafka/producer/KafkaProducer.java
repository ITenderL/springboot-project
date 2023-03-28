package com.itender.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itender
 * @date 2022/5/25 14:48
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送消息,不带回调
     *
     * @param normalMessage
     * @return
     */
    @GetMapping("/normal/{message}")
    public String sendMessage1(@PathVariable("message") String normalMessage) {
        kafkaTemplate.send("topic-test", normalMessage);
        log.info("【kafka发送消息】成功，消息内容为：{}", normalMessage);
        return "ok";
    }

    /**
     * kafka发送消息回调  方式一
     *
     * @param callbackMessage
     */
    @GetMapping("/callbackOne/{message}")
    public void sendMessage2(@PathVariable("message") String callbackMessage) {
        kafkaTemplate.send("topic-callback-1", callbackMessage).addCallback(success -> {
            if (success == null || success.getRecordMetadata() == null) {
                return;
            }
            // 消息发送到的topic
            String topic = success.getRecordMetadata().topic();
            // 消息发送到的分区
            int partition = success.getRecordMetadata().partition();
            // 消息在分区内的offset
            long offset = success.getRecordMetadata().offset();
            log.info("【kafka发送消息】成功！消息内容为：" + topic + "-" + partition + "-" + offset);
        }, failure -> log.info("【kafka发送消息】失败！消息内容为：{}", failure.getMessage()));
    }

    /**
     * kafka发送消息，回调方法  方式二
     *
     * @param callbackMessage
     */
    @GetMapping("/callbackTwo/{message}")
    public void sendMessage3(@PathVariable("message") String callbackMessage) {
        kafkaTemplate.send("topic-callback-2", callbackMessage).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("【kafka发送消息】失败！消息内容为：{}", ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("【kafka发送消息】成功！消息内容为：{}", result.getRecordMetadata().topic() + "-"
                        + result.getRecordMetadata().partition() + "-" + result.getRecordMetadata().offset());
            }
        });
    }

    /**
     * kafka发送消息，事务方式
     */
    @GetMapping("/transaction")
    public void sendMessageTransaction() {
        // 声明事务，后面报错消息不会发出去
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send("topic-transaction", "test executeInTransaction");
            throw new RuntimeException("fail in transaction!");
        });

        // 不声明事务，后面报错但消息已经发送成功了
        kafkaTemplate.send("topic-transaction", "test executeInTransaction");
        throw new RuntimeException("fail not in transaction!");
    }
}

package com.itender.rabbitmq.service.impl;

import com.itender.rabbitmq.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author itender
 * @date 2022/9/5 16:46
 * @desc
 */
@Slf4j
@Component
public class MessageServiceImpl implements MessageService, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMessage(String exchange, String routingKey, Object msg) {
        // 消息发送失败返回队列中，yml需要配置publisher-return：true
        rabbitTemplate.setMandatory(true);
        // 消息消费者确认收到消息后，手动ack回执
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
        // 发送消息
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("------【returnedMessage】------ replyCode: {}, replyText: {}", returnedMessage.getReplyCode(), returnedMessage.getReplyText());
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("------【confirm】------ ack: true,  cause: {}", cause);
        } else {
            log.info("------【confirm】------ ack: false  cause: {}", cause);
        }
    }
}

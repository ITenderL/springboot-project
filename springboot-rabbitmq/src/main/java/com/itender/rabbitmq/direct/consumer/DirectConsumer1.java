package com.itender.rabbitmq.direct.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author itender
 * @date 2022/9/5 10:50
 * @desc
 */
@Slf4j
@Component
@RabbitListener(queues = "directQueue")
public class DirectConsumer1 {
    @RabbitHandler
    public void process(String msg, Channel channel, Message message) throws IOException {
        // 拿到消息延迟消费
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {

            // 确认一条消息
            // channel.basicAck(deliveryTag, false);deliveryTag:该消息的index; multiple： 是否批量， true：将一次性ack所有的小于deliveryTag的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("DirectConsumer1 get msg1 success msg: {} ", msg);
        } catch (Exception e) {
            // 消费者处理异常，通知队列消息消费失败
            // 拒绝确认消息 channel.basicNack(long deliveryTag, boolean mulitiple, boolean requeue);deliveryTag:该消息的index;requeue: 被拒绝的是否重新入队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            log.info("DirectConsumer1 get msg1 failed basicNack msg：{} ", msg);
        }
    }
}

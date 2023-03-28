package com.itender.rabbitmq.direct.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class DirectConsumer2 {

    @RabbitHandler
    public void process(String msg, Channel channel, Message message) throws IOException {
        // 拿到消息延迟消费
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            if (StringUtils.isNotBlank(msg)) {
                String numStr = msg.substring(4);
                int num = Integer.parseInt(numStr);
                if (num >= 3) {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    log.info("DirectConsumer2 get msg2 basicNack msg: {}", msg);
                }
            } else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("DirectConsumer2 get msg2 basicAck msg: {}", msg);
            }
        } catch (Exception e) {
            // 消费者处理异常，通知队列消息消费失败
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            log.info("DirectConsumer2 get msg2 basicNack failed msg: {}", msg);
        }
    }
}

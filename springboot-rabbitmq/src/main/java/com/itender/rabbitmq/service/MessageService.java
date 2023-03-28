package com.itender.rabbitmq.service;

/**
 * @author itender
 * @date 2022/9/5 16:54
 * @desc
 */
public interface MessageService {
    /**
     * 发送消息
     *
     * @param exchange
     * @param routingKey
     * @param msg
     */
    void sendMessage(String exchange, String routingKey, Object msg);
}

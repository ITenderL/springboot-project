package com.itender.rabbitmq.controller;

import com.itender.rabbitmq.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itender
 * @date 2022/9/5 17:12
 * @desc
 */
@Slf4j
@RestController
@RequestMapping(value = "/rabbit")
public class RabbitMqController {

    private final MessageService messageService;

    @Autowired
    public RabbitMqController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/direct/send")
    public String directSendMessage() {
        for (int i = 0; i < 10; i++) {
            String msg = "msg_" + i;
            log.info("-----发送消息 msg: {}------", msg);
            messageService.sendMessage("directExchange", "directRoutingKey", msg);
        }
        return "send ok!";
    }
}

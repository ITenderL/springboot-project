package com.itender.threadpool.controller;

import com.itender.threadpool.entity.User;
import com.itender.threadpool.feign.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author itender
 * @date 2023/8/15 15:07
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("user")
@RefreshScope
public class UserController {

    @Value("${pattern.dateformat}")
    private String dateFormat;

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/list")
    public List<User> userList() {
        log.info("开始feign调用，查询用户列表");
        return userClient.userList();
    }

    @GetMapping("/config")
    public String getConfig() {
        log.info("查询配置文件");
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
    }
}

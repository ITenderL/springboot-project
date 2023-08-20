package com.itender.easyexcel.controller;

import com.itender.easyexcel.pojo.User;
import com.itender.easyexcel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author itender
 * @date 2023/8/15 14:46
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public List<User> userList() {
        log.info("调用了excel-service服务，查询用户列表！");
        return userService.userList();
    }
}

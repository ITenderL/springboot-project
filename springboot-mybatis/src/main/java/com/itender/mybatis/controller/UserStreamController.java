package com.itender.mybatis.controller;

import com.itender.mybatis.entity.User;
import com.itender.mybatis.service.UserService;
import com.itender.mybatis.service.UserStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yuanhewei
 * @date 2024/1/20 11:50
 * @description
 */
@RestController
@RequestMapping ("/user")
public class UserStreamController {

    private final UserStreamService userStreamService;

    @Autowired
    public UserStreamController(UserService userService) {
        this.userStreamService = userStreamService;
    }

    @GetMapping("/list")
    public List<User> getUsers() {
        // return userStreamService.getUsers();
        return null;
    }
}

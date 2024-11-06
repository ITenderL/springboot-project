package com.itender.mybatis.controller;

import com.itender.mybatis.entity.User;
import com.itender.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuanhewei
 * @date 2024/1/20 11:50
 * @description
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private  UserService userService;


    @GetMapping("/list")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public void save(@RequestBody User user) {
        userService.saveUser(user);
    }
}

package com.itender.swagger.controller;

import com.google.common.collect.Lists;
import com.itender.swagger.pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author itender
 * @date 2023/2/3 11:40
 * @desc
 */
@Api("用户信息管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation("Add User")
    @ApiImplicitParam(name = "user", type = "body", dataTypeClass = User.class, required = true)
    @PostMapping("/add")
    public String add(@RequestBody User user) {
        return "ok";
    }

    @ApiOperation("Query User List")
    @GetMapping("/list")
    public List<User> list() {
        List<User> list = Lists.newArrayList();
        User user1 = User.builder()
                .id(1L).userName("itender")
                .address("广东深圳").gender("男")
                .email("itender@163.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .userName("itender2")
                .address("广东深圳2")
                .gender("男")
                .email("itender@163.com")
                .build();
        list.add(user1);
        list.add(user2);
        return list;
    }
}

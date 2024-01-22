package com.itender.mybatis.service.impl;

import com.itender.mybatis.entity.User;
import com.itender.mybatis.mapper.mysql.UserMapper;
import com.itender.mybatis.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yuanhewei
 * @date 2024/1/20 11:49
 * @description
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getUsers() {
        return userMapper.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return null;
    }

    @Override
    public void addUser(User user) {
        userMapper.addUser(user);
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteById(Integer id) {

    }
}

package com.itender.threadpool.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itender.threadpool.entity.UserEntity;
import com.itender.threadpool.mapper.UserMapper;
import com.itender.threadpool.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author itender
 * @date 2023/4/28 14:27
 * @desc
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @DS("master")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<UserEntity> list() {
        return userMapper.selectList(new QueryWrapper<>());
    }
}

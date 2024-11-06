package com.itender.mybatis.service;

import com.itender.mybatis.entity.User;
import com.itender.mybatis.mapper.mysql.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author analytics
 * @date 2024/11/5 16:18
 * @description
 */
@Service
public class TestTransactionService2 {
    @Resource
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void saveUser() {
        User user = new User();
        user.setName("张三");
        user.setUsername("zhangsan");
        user.setPassword("123456");
        userMapper.saveUser(user);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateUser(User user) {
        user.setId(1);
        user.setName("胖胖");
        user.setUsername("pangpang");
        userMapper.updateUser(user);
    }
}

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
public class TestTransactionService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private TestTransactionService2 testTransactionService2;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void saveUser(User user) {
        userMapper.saveUser(user);
        new Thread(() -> testTransactionService2.updateUser(new User())).start();
        throw new RuntimeException("插入失败");
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateUser(User user) {
        user.setId(1);
        user.setName("胖胖");
        user.setUsername("pangpang");
        userMapper.updateUser(user);
    }
}

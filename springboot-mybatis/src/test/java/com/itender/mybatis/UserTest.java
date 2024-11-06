package com.itender.mybatis;

import com.itender.mybatis.entity.User;
import com.itender.mybatis.service.TestTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author yuanhewei
 * @date 2024/1/20 17:08
 * @description
 */
@SpringBootTest
class UserTest {

    @Resource
    private TestTransactionService testTransactionService;

    @Test
    void test() {
        User user = new User();
        user.setName("analytics");
        user.setUsername("analytics");
        user.setPassword("123456");
        testTransactionService.saveUser(user);
    }
}

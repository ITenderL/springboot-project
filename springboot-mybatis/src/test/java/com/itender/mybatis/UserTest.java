package com.itender.mybatis;

import com.itender.mybatis.entity.User;
import com.itender.mybatis.mapper.mysql.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yuanhewei
 * @date 2024/1/20 17:08
 * @description
 */
@SpringBootTest
public class UserTest {

    @Autowired
    private UserMapper userMapper;
    @Test
    public void testAdd() {
        User user = User.builder().age(20).address("山东临沂").phone("15555555555").sex(1).name("李四").email("lisi@126.com").status(true).build();
        userMapper.addUser(user);
    }
}

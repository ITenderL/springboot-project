package com.itender.mybatis.service;

import com.itender.mybatis.entity.User;

import java.util.List;

/**
 * @author yuanhewei
 * @date 2024/1/20 11:49
 * @description
 */
public interface UserService {

    /**
     * 查询所有用户
     *
     * @return
     */
    List<User> getUsers();

    /**
     * 新增用户
     *
     * @param user
     */
    void saveUser(User user);

    /**
     * 更新用户
     *
     * @param user
     */
    void updateUser(User user);
}

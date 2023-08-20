package com.itender.threadpool.service;

import com.itender.threadpool.entity.UserEntity;

import java.util.List;

/**
 * @author itender
 * @date 2023/4/28 14:25
 * @desc
 */
public interface UserService {

    /**
     * 查询用户集合
     *
     * @return
     */
    List<UserEntity> list();
}

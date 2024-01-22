package com.itender.mybatis.mapper.mysql;

import com.itender.mybatis.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yuanhewei
 * @date 2024/1/20 11:50
 * @description
 */
@Repository
public interface UserMapper {

    /**
     * 查询所有用户
     *
     * @return
     */
    List<User> findAll();

    /**
     * 根据id查询用户
     *
     * @param id
     */
    User findById(@Param("id") Integer id);

    /**
     * 新增用户
     *
     * @param user
     */
    void addUser(@Param("user") User user);

    /**
     * 更新用户
     *
     * @param user
     */
    void updateUser(@Param("user") User user);

    /**
     * 删除用户
     *
     * @param id
     */
     void deleteById(@Param("id") Integer id);
}

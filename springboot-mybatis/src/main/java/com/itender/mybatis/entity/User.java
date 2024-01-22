package com.itender.mybatis.entity;

import lombok.*;

/**
 * @author yuanhewei
 * @date 2024/1/20 12:45
 * @description
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * id主键
     */
    private Integer id;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 用户名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 电话
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 1：有效 0：无效
     */
    private Boolean status;
}

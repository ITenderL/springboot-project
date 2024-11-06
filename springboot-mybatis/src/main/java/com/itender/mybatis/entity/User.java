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
     * 用户名
     */
    private String name;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}

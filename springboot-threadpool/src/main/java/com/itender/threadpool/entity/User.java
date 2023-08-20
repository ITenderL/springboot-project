package com.itender.threadpool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author itender
 * @date 2023/1/30 17:30
 * @desc
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User {

    /**
     * user id.
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名.
     */
    @TableField(value = "username")
    private String userName;

    /**
     * 性别.
     */
    @TableField(exist = false)
    private String gender;

    /**
     * 地址.
     */
    @TableField(exist = false)
    private String address;

    /**
     * 邮箱.
     */
    @TableField(exist = false)
    private String email;

    /**
     * 手机号码.
     */
    @TableField(exist = false)
    private Long phoneNumber;

    /**
     * 描述.
     */
    @TableField(exist = false)
    private String description;
}

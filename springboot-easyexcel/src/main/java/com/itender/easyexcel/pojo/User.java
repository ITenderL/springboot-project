package com.itender.easyexcel.pojo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名.
     */
    @ExcelProperty("姓名")
    @TableField(value = "username")
    private String userName;

    /**
     * 性别.
     */
    @ExcelProperty("性别")
    @TableField(exist = false)
    private String gender;

    /**
     * 地址.
     */
    @ExcelProperty("地址")
    @TableField(exist = false)
    private String address;

    /**
     * 邮箱.
     */
    @ExcelProperty("邮箱")
    @TableField(exist = false)
    private String email;

    /**
     * 手机号码.
     */
    @ExcelProperty("手机号码")
    @TableField(exist = false)
    private Long phoneNumber;

    /**
     * 描述.
     */
    @ExcelIgnore
    @ExcelProperty("描述")
    @TableField(exist = false)
    private String description;
}

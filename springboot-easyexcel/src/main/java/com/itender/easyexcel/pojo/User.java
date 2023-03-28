package com.itender.easyexcel.pojo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
public class User {

    /**
     * user id.
     */
    @ExcelProperty("ID")
    private Long id;

    /**
     * 姓名.
     */
    @ExcelProperty("姓名")
    private String userName;

    /**
     * 性别.
     */
    @ExcelProperty("性别")
    private String gender;

    /**
     * 地址.
     */
    @ExcelProperty("地址")
    private String address;

    /**
     * 邮箱.
     */
    @ExcelProperty("邮箱")
    private String email;

    /**
     * 手机号码.
     */
    @ExcelProperty("手机号码")
    private Long phoneNumber;

    /**
     * 描述.
     */
    @ExcelIgnore
    @ExcelProperty("描述")
    private String description;
}

package com.itender.easyexcel.pojo;

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
public class UserUnannotated {


    private Long id;

    /**
     * 姓名.
     */
    private String userName;

    /**
     * 性别.
     */
    private String gender;

    /**
     * 地址.
     */
    private String address;

    /**
     * 邮箱.
     */
    private String email;

    /**
     * 手机号码.
     */
    private Long phoneNumber;

    /**
     * 描述.
     */
    private String description;
}

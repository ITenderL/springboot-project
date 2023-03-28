package com.itender.mapstruct.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author itender
 * @date 2022/9/22 17:29
 * @desc
 */
@Data
@Accessors(chain = true)
public class UserDto {

    private Integer id;

    private String name;

    private Integer age;

    private Date birthday;

    private String address;
}

package com.itender.mapstruct.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author itender
 * @date 2022/9/22 17:29
 * @desc
 */
@Data
@Accessors(chain = true)
public class UserVo {

    private Integer id;

    private String name;

    private Integer age;

    private String birthday;

    private String address;
}

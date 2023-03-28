package com.itender.mapstruct.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author itender
 * @date 2022/9/23 11:08
 * @desc
 */
@Data
@Accessors(chain = true)
public class Zoo {

    private Dog dog;

    @Data
    @Accessors(chain = true)
    public static class Dog{

        private String name;

        private Integer age;

        private String type;
    }
}

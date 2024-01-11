package com.itender.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: ITender
 * @date: 2022/05/01/ 11:04
 * @description:
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;

    private String sex;

    private Integer age;
}

package com.itender.ratelimit.enums;

import lombok.Getter;

/**
 * @author itender
 * @date 2022/6/2 14:30
 * @desc
 */
@Getter
public enum LimitTypeEnum {

    /**
     * 根据请求ip
     */
    IP,

    /**
     * 根据用户自定义key
     */
    CUSTOMER;
}

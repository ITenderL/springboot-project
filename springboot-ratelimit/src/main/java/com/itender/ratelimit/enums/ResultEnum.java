package com.itender.ratelimit.enums;

import lombok.Getter;

/**
 * @author itender
 * @date 2022/6/2 16:44
 * @desc
 */
@Getter
public enum ResultEnum {

    /**
     * 请求过于频繁
     */
    REQUEST_TOO_FREQUENTLY(1000, "request too frequently!"),
    /**
     * 服务异常
     */
    SERVER_EXCEPTION(5000, "server exception"),
    ;

    /**
     * 返回状态码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

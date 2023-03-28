package com.itender.ratelimit.exception;

import com.itender.ratelimit.enums.ResultEnum;
import lombok.Getter;

import java.text.MessageFormat;

/**
 * @author itender
 * @date 2022/6/2 15:45
 * @desc
 */
@Getter
public class RateLimitException extends RuntimeException {
    /**
     * 返回状态码
     */
    private final Integer code;

    /**
     * 返回信息
     */
    private final String message;

    public RateLimitException(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    @Override
    public String toString() {
        return MessageFormat.format("code: {0}, msg: {1}", this.code, this.message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

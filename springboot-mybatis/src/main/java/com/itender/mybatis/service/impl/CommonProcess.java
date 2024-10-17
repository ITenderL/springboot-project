package com.itender.mybatis.service.impl;

import com.itender.spi.ProcessSpiInterface;

/**
 * @author analytics
 * @date 2024/10/17 20:29
 * @description 通用处理
 */
public class CommonProcess implements ProcessSpiInterface {
    @Override
    public String process() {
        return "这是一个通用处理的方法";
    }
}

package com.itender.mybatis.service.impl;

import com.itender.spi.ProcessSpiInterface;

/**
 * @author analytics
 * @date 2024/10/17 20:29
 * @description 特殊处理
 */
public class SpecialProcess implements ProcessSpiInterface {
    @Override
    public String process() {
        return "这是一个特殊处理的方法";
    }
}
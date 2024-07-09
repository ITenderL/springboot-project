package com.itender.redis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itender.redis.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yuanhewei
 * @date 2024/7/9 15:29
 * @description
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}

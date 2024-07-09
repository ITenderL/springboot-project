package com.itender.redis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itender.redis.annotation.DoubleCache;
import com.itender.redis.mapper.OrderMapper;
import com.itender.redis.pojo.Order;
import com.itender.redis.service.DoubleCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:26
 * @description
 */
@Slf4j
@Service
public class DoubleCacheServiceImpl implements DoubleCacheService {

    @Resource
    private OrderMapper orderMapper;

    @DoubleCache(cacheName = "order", key = "#id",
            type = DoubleCache.CacheType.FULL)
    public Order getOrderById(Long id) {
        return orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, id));
    }

    @DoubleCache(cacheName = "order",key = "#order.id",
            type = DoubleCache.CacheType.PUT)
    public Order updateOrder(Order order) {
        orderMapper.updateById(order);
        return order;
    }

    @DoubleCache(cacheName = "order",key = "#id",
            type = DoubleCache.CacheType.DELETE)
    public void deleteOrder(Long id) {
        orderMapper.deleteById(id);
    }
}

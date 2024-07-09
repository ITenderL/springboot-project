package com.itender.redis.service;

import com.itender.redis.pojo.Order;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:25
 * @description
 */
public interface DoubleCacheService {

    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    Order getOrderById(Long id);

    /**
     * 更新订单
     *
     * @param order
     * @return
     */
    Order updateOrder(Order order);

    /**
     * 删除订单
     *
     * @param id
     */
    void deleteOrder(Long id);
}

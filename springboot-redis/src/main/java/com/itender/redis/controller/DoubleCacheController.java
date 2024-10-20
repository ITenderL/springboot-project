package com.itender.redis.controller;

import com.itender.redis.pojo.EstimatedArrivalDateEntity;
import com.itender.redis.pojo.Order;
import com.itender.redis.service.DoubleCacheService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:14
 * @description
 */
@RestController
@RequestMapping("/doubleCache")
public class DoubleCacheController {

    @Resource
    private DoubleCacheService doubleCacheService;

    @PostMapping("/common")
    public EstimatedArrivalDateEntity getEstimatedArrivalDateCommon(@RequestBody EstimatedArrivalDateEntity estimatedArrivalDate) {
        return doubleCacheService.getEstimatedArrivalDateCommon(estimatedArrivalDate);
    }

    @PostMapping("/annotation")
    public EstimatedArrivalDateEntity getEstimatedArrivalDate(@RequestBody EstimatedArrivalDateEntity estimatedArrivalDate) {
        return doubleCacheService.getEstimatedArrivalDate(estimatedArrivalDate);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable("id") Long id) {
        return doubleCacheService.getOrderById(id);
    }

    @PutMapping
    public Order updateOrder(@RequestBody Order order) {
        return doubleCacheService.updateOrder(order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrderById(@PathVariable("id") Long id) {
        doubleCacheService.deleteOrder(id);
    }
}

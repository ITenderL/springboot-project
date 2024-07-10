package com.itender.redis.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.itender.redis.annotation.DoubleCache;
import com.itender.redis.constant.RedisConstants;
import com.itender.redis.mapper.EstimatedArrivalDateMapper;
import com.itender.redis.mapper.OrderMapper;
import com.itender.redis.pojo.EstimatedArrivalDateEntity;
import com.itender.redis.pojo.Order;
import com.itender.redis.service.DoubleCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:26
 * @description
 */
@Slf4j
@Service
public class DoubleCacheServiceImpl implements DoubleCacheService {

    @Resource
    private Cache<String, Object> caffeineCache;

    @Resource
    private RedisTemplate<String, Object> RedisTemplate;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private EstimatedArrivalDateMapper estimatedArrivalDateMapper;

    @DoubleCache(cacheName = "order", key = "#id",
            type = DoubleCache.CacheType.FULL)
    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @DoubleCache(cacheName = "order", key = "#order.id",
            type = DoubleCache.CacheType.PUT)
    @Override
    public Order updateOrder(Order order) {
        orderMapper.updateById(order);
        return order;
    }

    @DoubleCache(cacheName = "order", key = "#id",
            type = DoubleCache.CacheType.DELETE)
    @Override
    public void deleteOrder(Long id) {
        orderMapper.deleteById(id);
    }

    @Override
    public EstimatedArrivalDateEntity getEstimatedArrivalDateCommon(EstimatedArrivalDateEntity request) {
        String key = request.getDeliveryDate() + RedisConstants.COLON + request.getWarehouseId() + RedisConstants.COLON + request.getCity();
        log.info("Cache key: {}", key);
        Object value = caffeineCache.getIfPresent(key);
        if (Objects.nonNull(value)) {
            log.info("get from caffeine");
            return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(value.toString()).build();
        }
        value = RedisTemplate.opsForValue().get(key);
        if (Objects.nonNull(value)) {
            log.info("get from redis");
            caffeineCache.put(key, value);
            return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(value.toString()).build();
        }
        log.info("get from mysql");
        LocalDate deliveryDate = LocalDate.parse(request.getDeliveryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        EstimatedArrivalDateEntity estimatedArrivalDateEntity = estimatedArrivalDateMapper.selectOne(new QueryWrapper<EstimatedArrivalDateEntity>()
                .eq("delivery_date", deliveryDate)
                .eq("warehouse_id", request.getWarehouseId())
                .eq("city", request.getCity())
        );
        RedisTemplate.opsForValue().set(key, estimatedArrivalDateEntity.getEstimatedArrivalDate(), 120, TimeUnit.SECONDS);
        caffeineCache.put(key, estimatedArrivalDateEntity.getEstimatedArrivalDate());
        return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(estimatedArrivalDateEntity.getEstimatedArrivalDate()).build();
    }

    @DoubleCache(cacheName = "estimatedArrivalDate", key = {"#request.deliveryDate", "#request.warehouseId", "#request.city"},
            type = DoubleCache.CacheType.FULL)
    @Override
    public EstimatedArrivalDateEntity getEstimatedArrivalDate(EstimatedArrivalDateEntity request) {
        DateTime deliveryDate = DateUtil.parse(request.getDeliveryDate(), "yyyy-MM-dd");
        EstimatedArrivalDateEntity estimatedArrivalDateEntity = estimatedArrivalDateMapper.selectOne(new QueryWrapper<EstimatedArrivalDateEntity>()
                .eq("delivery_date", deliveryDate)
                .eq("warehouse_id", request.getWarehouseId())
                .eq("city", request.getCity())
        );
        return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(estimatedArrivalDateEntity.getEstimatedArrivalDate()).build();
    }
}

package com.itender.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yuanhewei
 * @date 2024/7/4 16:06
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/pipelines")
public class PipelineController {

    @PostConstruct
    public void init() {
        long startTime = System.currentTimeMillis();
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.hasKey((K) "");
                return null;
            }
        });
        log.info("redis初始化管道请求end，耗时：{}ms", System.currentTimeMillis() - startTime);
    }

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/execute")
    public String getPipelines() {
        List<Object> objects = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                ValueOperations<String, Object> valueOperations = (ValueOperations<String, Object>) operations.opsForValue();
                valueOperations.set("aa", "hello world");
                valueOperations.set("bb", "redis");
                valueOperations.get("aa");
                valueOperations.get("bb");
                // 返回null即可，因为返回值会被管道的返回值覆盖，外层取不到这里的返回值
                return null;
            }
        });
        log.info("objects: {}", objects);
        // 获取所有 pipeline 列表
        return "success";
    }


    /**
     * 批量添加
     *
     * @param map
     */
    public void batchSet(Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }


    /**
     * 批量添加 并且设置失效时间
     *
     * @param map
     * @param seconds
     */
    public void batchSetOrExpire(Map<String, String> map, Long seconds) {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined((RedisCallback<String>) connection -> {
            map.forEach((key, value) ->
                    connection.set(Objects.requireNonNull(serializer.serialize(key)), Objects.requireNonNull(serializer.serialize(value)), Expiration.seconds(seconds), RedisStringCommands.SetOption.UPSERT)
            );
            return null;
        }, serializer);
    }
}

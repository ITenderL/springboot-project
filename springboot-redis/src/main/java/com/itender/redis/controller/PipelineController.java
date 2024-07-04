package com.itender.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuanhewei
 * @date 2024/7/4 16:06
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/pipelines")
public class PipelineController {

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
}

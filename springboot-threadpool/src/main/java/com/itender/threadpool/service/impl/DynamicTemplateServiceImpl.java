package com.itender.threadpool.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.itender.threadpool.entity.DynamicTemplateEntity;
import com.itender.threadpool.entity.UserEntity;
import com.itender.threadpool.mapper.DynamicTemplateMapper;
import com.itender.threadpool.mapper.UserMapper;
import com.itender.threadpool.service.DynamicTemplateService;
import com.itender.threadpool.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author itender
 * @date 2023/4/28 11:15
 * @desc
 */
@Service
public class DynamicTemplateServiceImpl implements DynamicTemplateService {

    private final DynamicTemplateMapper dynamicTemplateMapper;

    private final UserMapper userMapper;

    private final UserService userService;

    @Autowired
    public DynamicTemplateServiceImpl(DynamicTemplateMapper dynamicTemplateMapper, UserMapper userMapper, UserService userService) {
        this.dynamicTemplateMapper = dynamicTemplateMapper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public List<DynamicTemplateEntity> list() {
        List<DynamicTemplateEntity> templateList = dynamicTemplateMapper.selectList(new QueryWrapper<>());
        if (CollectionUtils.isEmpty(templateList)) {
            return Lists.newArrayList();
        }
        List<UserEntity> userList = userMapper.selectList(new QueryWrapper<>());
        if (CollectionUtils.isEmpty(userList)) {
            return templateList;
        }
        Map<Integer, String> userMap = userList.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getUsername, (key1, key2) -> key1));
        templateList.forEach(template -> template.setCreatedByName(userMap.get(template.getCreatedBy())));
        return templateList;
    }

    @DS("slave")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public Integer add(DynamicTemplateEntity template) {
        // List<UserEntity> userList = userMapper.selectList(new QueryWrapper<>());
        List<UserEntity> userList = userService.list();
        if (CollectionUtils.isEmpty(userList)) {
            template.setCreatedByName("");
        }
        Map<Integer, String> userMap = userList.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getUsername, (key1, key2) -> key1));
        template.setCreatedByName(userMap.get(template.getCreatedBy()));
        template.setCreatedTime(new Date());
        dynamicTemplateMapper.insert(template);
        return template.getId();
    }
}

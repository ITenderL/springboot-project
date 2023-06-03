package com.itender.threadpool.service;

import com.itender.threadpool.entity.DynamicTemplateEntity;

import java.util.List;

/**
 * @author itender
 * @date 2023/4/28 10:36
 * @desc
 */
public interface DynamicTemplateService {

    /**
     * 查询模板集合
     *
     * @return
     */
    List<DynamicTemplateEntity> list();

    /**
     * 添加模板
     *
     * @param template
     * @return
     */
    Integer add(DynamicTemplateEntity template);
}

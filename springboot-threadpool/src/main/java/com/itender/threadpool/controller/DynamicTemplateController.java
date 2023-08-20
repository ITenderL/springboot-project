package com.itender.threadpool.controller;

import com.itender.threadpool.entity.DynamicTemplateEntity;
import com.itender.threadpool.service.DynamicTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author itender
 * @date 2023/4/28 10:34
 * @desc
 */
@RestController
@RequestMapping("template")
public class DynamicTemplateController {

    private final DynamicTemplateService dynamicTemplateService;

    @Autowired
    public DynamicTemplateController(DynamicTemplateService dynamicTemplateService) {
        this.dynamicTemplateService = dynamicTemplateService;
    }

    @GetMapping
    public List<DynamicTemplateEntity> list() {
        return dynamicTemplateService.list();
    }

    @PostMapping
    public Integer add(@RequestBody DynamicTemplateEntity template) {
        return dynamicTemplateService.add(template);
    }
}

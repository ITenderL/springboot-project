package com.itender.threadpool.controller;

import com.itender.threadpool.service.LogOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itender
 * @date 2023/1/29 10:39
 * @desc
 */
@RestController
@RequestMapping("logOutput")
public class LogOutputController {

    private final LogOutputService logOutputService;

    @Autowired
    public LogOutputController(LogOutputService logOutputService) {
        this.logOutputService = logOutputService;
    }


    @GetMapping("/multiThreadInsert")
    public int multiThreadInsert() {
        return logOutputService.multiThreadInsert();
    }

    @GetMapping("/singleThreadInsert")
    public int singleThreadInsert() {
        return logOutputService.singleThreadInsert();
    }
}

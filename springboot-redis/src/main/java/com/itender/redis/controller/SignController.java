package com.itender.redis.controller;

import com.itender.redis.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itender
 * @date 2023/3/28 19:45
 * @desc
 */
@RestController
@RequestMapping("user")
public class SignController {

    private final SignService signService;

    @Autowired
    public SignController(SignService signService) {
        this.signService = signService;
    }

    @GetMapping("/sign/{userId}")
    public void sign(@PathVariable("userId") Long userId) {
        signService.sign(userId);
    }

    @GetMapping("/signCount/{userId}")
    public Long signCount(@PathVariable("userId") Long userId) {
        return signService.signCount(userId);
    }
}

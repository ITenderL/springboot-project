package com.itender.docker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itender
 * @date 2022/8/4 14:35
 * @desc
 */
@RestController
@RequestMapping("/docker")
public class DockerController {
    @GetMapping("/{name}")
    public String hello(@PathVariable(value = "name") String name) {
        return "Hello, " + name + "!" + "This is Docker!";
    }
}

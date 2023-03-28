package com.itender.easyexcel.controller;

import com.itender.easyexcel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author itender
 * @date 2023/1/30 17:42
 * @desc
 */
@Slf4j
@RestController
@RequestMapping("excel")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/export")
    public void exportUserInfo(HttpServletResponse response) {
        try {
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = "导出用户信息列表";
            // 注意：这里要加上filename*=utf-8'zh_cn'否则可能会导致导出文件名乱码
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8'zh_cn'" + fileName + System.currentTimeMillis() + ".xlsx");
            userService.exportUserInfo(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/import")
    public void importUserInfo(@RequestParam(value = "file") MultipartFile file) {
        try {
            userService.importUserInfo(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/fill")
    public void fill(HttpServletResponse response) {
        try {
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition",
                    "attachment;filename=user_excel_template_" + System.currentTimeMillis() + ".xlsx");
            userService.fill(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.itender.threadpool.feign;

import com.itender.threadpool.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author itender
 * @date 2023/8/15 14:44
 * @desc
 */
@FeignClient(name = "easyexcelService")
public interface UserClient {

    /**
     * 查询用户集合
     *
     * @return
     */
    @GetMapping("/user/list")
    List<User> userList();
}

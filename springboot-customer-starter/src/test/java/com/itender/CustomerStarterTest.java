package com.itender;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author itender
 * @date 2023/11/14/ 21:11
 * @desc
 */
@SpringBootTest
class CustomerStarterTest {

    @Autowired
    private ThreadPoolExecutor myThreadPool;

    @Test
    void test() {
        System.out.println("corePoolSize: " + myThreadPool.getCorePoolSize());
    }
}

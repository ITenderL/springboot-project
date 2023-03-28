package com.itender.threadpool;

import com.itender.threadpool.mapper.LogOutputMapper;
import com.itender.threadpool.pojo.LogOutputResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author itender
 * @date 2022/12/14 14:40
 * @desc
 */
@Slf4j
@SpringBootTest
class MybatisTest {

    @Autowired
    private LogOutputMapper logOutputMapper;

    @Test
    void insertTest() {
        LogOutputResult logOutputResult = new LogOutputResult();
        logOutputResult.setOperator("itender").setType("get").setCreateTime(new Date()).setUpdateTime(new Date());
        int insert = logOutputMapper.insert(logOutputResult);
        System.out.println(insert);
    }

    @Test
    void selectByTypeTest() {
        System.out.println(logOutputMapper.selectByType("", 5L));
    }

}

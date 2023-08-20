package com.itender.threadpool.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itender.threadpool.entity.LogOutputResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author itender
 * @date 2022/12/14 14:35
 * @desc
 */
@DS("master")
@Mapper
public interface LogOutputMapper extends BaseMapper<LogOutputResult> {

    /**
     * 根据类型查询
     *
     * @param type
     * @param id
     * @return
     */
    List<LogOutputResult> selectByType(@Param("type") String type, @Param("id") Long id);

    /**
     * 多线程批量插入数据
     *
     * @param logOutputResults
     */
    void bachInsert(@Param("list") List<LogOutputResult> logOutputResults);

    /**
     * 单线程批量插入数据
     *
     * @param logOutputResults
     */
    void singleBachInsert(@Param("list") List<LogOutputResult> logOutputResults);
}

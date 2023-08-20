package com.itender.threadpool.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itender.threadpool.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author itender
 * @date 2023/4/28 11:08
 * @desc
 */
@Mapper
@Repository
@DS("master")
public interface UserMapper extends BaseMapper<UserEntity> {
}

package com.itender.easyexcel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itender.easyexcel.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author itender
 * @date 2023/8/15 14:51
 * @desc
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
}

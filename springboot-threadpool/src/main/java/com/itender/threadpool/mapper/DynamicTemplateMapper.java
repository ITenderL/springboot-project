package com.itender.threadpool.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itender.threadpool.entity.DynamicTemplateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author itender
 * @date 2023/4/28 11:09
 * @desc
 */
@DS("slave")
@Mapper
@Repository
public interface DynamicTemplateMapper extends BaseMapper<DynamicTemplateEntity> {
}

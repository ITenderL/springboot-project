package com.itender.threadpool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author itender
 * @date 2023/4/28 11:01
 * @desc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_dynamic_template")
public class DynamicTemplateEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 语言
     */
    private String language;

    /**
     * 语言编码
     */
    @TableField("language_code")
    private String languageCode;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 创建人
     */
    @TableField("created_by")
    private Integer createdBy;

    /**
     * 创建人名称
   */
    @TableField("created_by_name")
    private String createdByName;
}

package com.itender.threadpool.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author itender
 * @date 2022/12/14 11:39
 * @desc
 */
@Data
@ToString
@Accessors(chain = true)
@TableName("t_output_result")
public class LogOutputResult implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;

    private String operator;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}

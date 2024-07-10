package com.itender.redis.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:48
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_estimated_arrival_date")
public class EstimatedArrivalDateEntity {

    /**
     * 发货时间
     */
    @TableField("delivery_date")
    private String deliveryDate;

    /**
     * 发货仓库
     */
    @TableField("warehouse_id")
    private String warehouseId;

    /**
     * 收货城市
     */
    @TableField("city")
    private String city;

    /**
     * 预计到达时间
     */
    @TableField("estimated_arrival_date")
    private String estimatedArrivalDate;
}

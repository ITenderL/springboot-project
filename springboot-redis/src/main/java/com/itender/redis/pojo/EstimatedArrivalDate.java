package com.itender.redis.pojo;

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
public class EstimatedArrivalDate {

    /**
     * 发货时间
     */
    private String deliveryDate;

    /**
     * 发货仓库
     */
    private String warehouseId;

    /**
     * 收货城市
     */
    private String city;

    /**
     * 预计到达时间
     */
    private String estimatedArrivalDate;
}

package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.trade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Orders extends BaseEntity {

    private String orderNo;

    private Long buyerId;

    private Long sellerId;

    private BigDecimal totalAmount;

    private String status;

    private String remark;
}
package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {

    private Long id;

    private Long orderId;

    private Long goodsId;

    private String goodsTitle;

    private BigDecimal goodsPrice;

    private Integer quantity;

    private BigDecimal subtotalAmount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
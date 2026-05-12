package com.campus.trade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long goodsId;

    private String goodsTitle;

    private BigDecimal goodsPrice;

    private Integer quantity;

    private BigDecimal subtotalAmount;
}
package com.campus.trade.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {

    private Long id;

    private Long goodsId;

    private String title;

    private String coverImage;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;
}
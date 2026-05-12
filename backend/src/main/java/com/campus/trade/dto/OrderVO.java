package com.campus.trade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long buyerId;

    private String buyerName;

    private Long sellerId;

    private String sellerName;

    private BigDecimal totalAmount;

    private String status;

    private String remark;

    private LocalDateTime createTime;

    private List<OrderItemVO> items;
}
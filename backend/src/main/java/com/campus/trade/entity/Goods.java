package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.trade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("goods")
public class Goods extends BaseEntity {

    private Long sellerId;

    private Long categoryId;

    private String title;

    private BigDecimal price;

    private String description;

    private String coverImage;

    private String status;

    private Integer stock;
}
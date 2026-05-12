package com.campus.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDistributionVO {

    private Long categoryId;

    private String categoryName;

    private Long goodsCount;
}
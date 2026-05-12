package com.campus.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOverviewVO {

    private Long userCount;

    private Long goodsCount;

    private Long orderCount;

    private Long pendingGoodsCount;
}
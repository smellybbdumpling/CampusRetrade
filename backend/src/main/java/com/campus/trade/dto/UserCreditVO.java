package com.campus.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreditVO {

    private Long publishedGoodsCount;

    private Long approvedGoodsCount;

    private Long finishedBuyOrderCount;

    private Long finishedSellOrderCount;

    private Long totalReportCount;

    private Long effectiveReportCount;

    private Long penaltyCount;

    private Integer creditScore;

    private String creditLevel;
}
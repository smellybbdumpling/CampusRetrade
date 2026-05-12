package com.campus.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminTrendVO {

    private List<TrendPointVO> goodsTrend;

    private List<TrendPointVO> orderTrend;
}
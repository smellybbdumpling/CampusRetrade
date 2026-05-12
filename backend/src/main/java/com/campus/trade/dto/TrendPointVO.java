package com.campus.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrendPointVO {

    private String date;

    private Long count;
}
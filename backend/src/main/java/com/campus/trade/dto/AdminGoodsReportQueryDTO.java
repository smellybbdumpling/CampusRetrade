package com.campus.trade.dto;

import lombok.Data;

@Data
public class AdminGoodsReportQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private String status;
}
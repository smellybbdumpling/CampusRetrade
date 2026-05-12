package com.campus.trade.dto;

import lombok.Data;

@Data
public class AdminGoodsQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private Long categoryId;

    private String status;
}
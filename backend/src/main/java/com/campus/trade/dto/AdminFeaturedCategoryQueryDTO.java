package com.campus.trade.dto;

import lombok.Data;

@Data
public class AdminFeaturedCategoryQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String name;

    private String status;
}
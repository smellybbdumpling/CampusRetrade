package com.campus.trade.dto;

import lombok.Data;

import java.util.List;

@Data
public class FeaturedSectionVO {

    private Long id;

    private String name;

    private List<GoodsVO> goodsList;
}
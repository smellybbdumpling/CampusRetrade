package com.campus.trade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsVO {

    private Long id;

    private Long categoryId;

    private String categoryName;

    private Long sellerId;

    private String sellerName;

    private String title;

    private BigDecimal price;

    private String description;

    private String coverImage;

    private List<GoodsImageVO> images;

    private String status;

    private Integer stock;

    private String latestAuditRemark;

    private Boolean favorited;

    private List<GoodsMessageVO> messages;

    private List<GoodsAuditLogVO> auditLogs;

    private List<FeaturedCategoryVO> featuredCategories;
}
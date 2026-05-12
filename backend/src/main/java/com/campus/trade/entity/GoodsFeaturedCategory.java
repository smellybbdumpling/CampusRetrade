package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("goods_featured_category")
public class GoodsFeaturedCategory {

    private Long id;

    private Long goodsId;

    private Long featuredCategoryId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
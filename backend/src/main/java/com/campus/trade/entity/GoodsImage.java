package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("goods_image")
public class GoodsImage {

    private Long id;

    private Long goodsId;

    private String imageUrl;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.trade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("goods_message")
public class GoodsMessage extends BaseEntity {

    private Long goodsId;

    private Long senderId;

    private Long parentId;

    private String content;
}
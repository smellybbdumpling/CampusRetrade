package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.trade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("favorite_goods")
public class FavoriteGoods extends BaseEntity {

    private Long userId;

    private Long goodsId;
}
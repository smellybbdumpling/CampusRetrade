package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.trade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("goods_report")
public class GoodsReport extends BaseEntity {

    private Long goodsId;

    private Long reporterId;

    private Long reportedUserId;

    private String reportType;

    private String reportReason;

    private String reportDescription;

    private String status;

    private String handleResult;

    private String handleRemark;

    private Long handlerId;

    private LocalDateTime handleTime;
}
package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("goods_audit_log")
public class GoodsAuditLog {

    private Long id;

    private Long goodsId;

    private Long adminId;

    private String auditStatus;

    private String auditRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
package com.campus.trade.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsAuditLogVO {

    private Long id;

    private Long adminId;

    private String adminName;

    private String auditStatus;

    private String auditRemark;

    private LocalDateTime createTime;
}
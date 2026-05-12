package com.campus.trade.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsReportVO {

    private Long id;

    private Long goodsId;

    private String goodsTitle;

    private Long reporterId;

    private String reporterName;

    private Long reportedUserId;

    private String reportedUserName;

    private String reportType;

    private String reportReason;

    private String reportDescription;

    private String status;

    private String handleResult;

    private String handleRemark;

    private Long handlerId;

    private String handlerName;

    private LocalDateTime handleTime;

    private LocalDateTime createTime;
}
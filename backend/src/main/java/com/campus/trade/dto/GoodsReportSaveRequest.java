package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoodsReportSaveRequest {

    @NotBlank(message = "举报类型不能为空")
    private String reportType;

    @NotBlank(message = "举报原因不能为空")
    private String reportReason;

    private String reportDescription;
}
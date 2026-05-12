package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminHandleGoodsReportRequest {

    @NotBlank(message = "处理结果不能为空")
    private String handleResult;

    private String handleRemark;
}
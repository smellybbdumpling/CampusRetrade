package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AdminAuditGoodsRequest {

    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    @NotBlank(message = "审核备注不能为空")
    private String auditRemark;

    private List<Long> featuredCategoryIds;
}
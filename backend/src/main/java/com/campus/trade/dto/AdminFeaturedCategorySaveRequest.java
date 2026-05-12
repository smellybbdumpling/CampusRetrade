package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminFeaturedCategorySaveRequest {

    @NotBlank(message = "特色分类名称不能为空")
    private String name;

    @NotBlank(message = "状态不能为空")
    private String status;

    @NotNull(message = "排序值不能为空")
    private Integer sortOrder;
}
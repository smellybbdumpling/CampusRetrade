package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUserStatusUpdateRequest {

    @NotBlank(message = "用户状态不能为空")
    private String status;
}
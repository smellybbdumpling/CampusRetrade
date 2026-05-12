package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGoodsStatusRequest {

    @NotBlank(message = "商品状态不能为空")
    private String status;
}
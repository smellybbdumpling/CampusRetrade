package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoodsMessageSaveRequest {

    private Long parentId;

    @NotBlank(message = "留言内容不能为空")
    private String content;
}
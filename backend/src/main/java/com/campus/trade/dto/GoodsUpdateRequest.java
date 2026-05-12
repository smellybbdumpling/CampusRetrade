package com.campus.trade.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsUpdateRequest {

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotBlank(message = "商品标题不能为空")
    private String title;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    private BigDecimal price;

    @NotBlank(message = "商品描述不能为空")
    private String description;

    @NotBlank(message = "商品封面不能为空")
    private String coverImage;

    private List<String> imageUrls;

    @NotNull(message = "商品库存不能为空")
    @Min(value = 1, message = "商品库存至少为1")
    private Integer stock;
}
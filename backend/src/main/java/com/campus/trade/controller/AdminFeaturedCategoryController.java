package com.campus.trade.controller;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminFeaturedCategoryQueryDTO;
import com.campus.trade.dto.AdminFeaturedCategorySaveRequest;
import com.campus.trade.entity.FeaturedCategory;
import com.campus.trade.service.GoodsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/featured-categories")
@RequiredArgsConstructor
public class AdminFeaturedCategoryController {

    private final GoodsService goodsService;

    @GetMapping("/page")
    public Result<PageResult<FeaturedCategory>> page(AdminFeaturedCategoryQueryDTO queryDTO) {
        UserContext.requireAdmin();
        return Result.success(goodsService.adminPageFeaturedCategories(UserContext.getRequiredUserId(), queryDTO));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody AdminFeaturedCategorySaveRequest request) {
        UserContext.requireAdmin();
        goodsService.adminCreateFeaturedCategory(UserContext.getRequiredUserId(), request);
        return Result.success("特色分类创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody AdminFeaturedCategorySaveRequest request) {
        UserContext.requireAdmin();
        goodsService.adminUpdateFeaturedCategory(UserContext.getRequiredUserId(), id, request);
        return Result.success("特色分类更新成功", null);
    }
}
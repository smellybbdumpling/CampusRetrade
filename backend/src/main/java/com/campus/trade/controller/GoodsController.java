package com.campus.trade.controller;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminAuditGoodsRequest;
import com.campus.trade.dto.AdminGoodsQueryDTO;
import com.campus.trade.dto.FeaturedSectionVO;
import com.campus.trade.dto.GoodsQueryDTO;
import com.campus.trade.dto.GoodsPublishRequest;
import com.campus.trade.dto.GoodsUpdateRequest;
import com.campus.trade.dto.GoodsVO;
import com.campus.trade.dto.UpdateGoodsStatusRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping("/page")
    public Result<PageResult<GoodsVO>> page(GoodsQueryDTO queryDTO) {
        return Result.success(goodsService.pageGoods(queryDTO));
    }

    @GetMapping("/featured/sections")
    public Result<List<FeaturedSectionVO>> featuredSections() {
        return Result.success(goodsService.listFeaturedSections());
    }

    @GetMapping("/{goodsId}")
    public Result<GoodsVO> detail(@PathVariable Long goodsId) {
        Long currentUserId = UserContext.getCurrentUser() == null ? null : UserContext.getRequiredUserId();
        return Result.success(goodsService.getGoodsDetail(goodsId, currentUserId));
    }

    @PostMapping
    public Result<Long> publish(@Valid @RequestBody GoodsPublishRequest request) {
        Long userId = UserContext.getRequiredUserId();
        return Result.success("发布成功，等待管理员审核", goodsService.publishGoods(userId, request));
    }

    @GetMapping("/my")
    public Result<List<GoodsVO>> myGoods() {
        return Result.success(goodsService.listMyGoods(UserContext.getRequiredUserId()));
    }

    @GetMapping("/favorites")
    public Result<List<GoodsVO>> myFavorites() {
        return Result.success(goodsService.listMyFavoriteGoods(UserContext.getRequiredUserId()));
    }

    @PostMapping("/{goodsId}/favorite")
    public Result<Void> favorite(@PathVariable Long goodsId) {
        goodsService.favoriteGoods(UserContext.getRequiredUserId(), goodsId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/{goodsId}/favorite")
    public Result<Void> unfavorite(@PathVariable Long goodsId) {
        goodsService.unfavoriteGoods(UserContext.getRequiredUserId(), goodsId);
        return Result.success("已取消收藏", null);
    }

    @PutMapping("/{goodsId}")
    public Result<Void> update(@PathVariable Long goodsId,
                               @Valid @RequestBody GoodsUpdateRequest request) {
        goodsService.updateMyGoods(UserContext.getRequiredUserId(), goodsId, request);
        return Result.success("商品修改成功，等待重新审核", null);
    }

    @PutMapping("/{goodsId}/status")
    public Result<Void> updateStatus(@PathVariable Long goodsId,
                                     @Valid @RequestBody UpdateGoodsStatusRequest request) {
        goodsService.updateMyGoodsStatus(UserContext.getRequiredUserId(), goodsId, request);
        return Result.success("商品状态更新成功", null);
    }

    @GetMapping("/admin/page")
    public Result<PageResult<GoodsVO>> adminPage(AdminGoodsQueryDTO queryDTO) {
        UserContext.requireAdmin();
        return Result.success(goodsService.adminPageGoods(queryDTO));
    }

    @PutMapping("/admin/{goodsId}/audit")
    public Result<Void> audit(@PathVariable Long goodsId,
                              @Valid @RequestBody AdminAuditGoodsRequest request) {
        Long adminUserId = UserContext.getRequiredUserId();
        goodsService.adminAuditGoods(adminUserId, goodsId, request);
        return Result.success("审核完成", null);
    }

    @PutMapping("/admin/{goodsId}")
    public Result<Void> adminUpdate(@PathVariable Long goodsId,
                                    @Valid @RequestBody GoodsUpdateRequest request) {
        goodsService.adminUpdateGoods(UserContext.getRequiredUserId(), goodsId, request);
        return Result.success("商品信息更新成功", null);
    }

    @PutMapping("/admin/{goodsId}/status")
    public Result<Void> adminUpdateStatus(@PathVariable Long goodsId,
                                          @Valid @RequestBody UpdateGoodsStatusRequest request) {
        goodsService.adminUpdateGoodsStatus(UserContext.getRequiredUserId(), goodsId, request);
        return Result.success("商品状态更新成功", null);
    }

    @DeleteMapping("/admin/{goodsId}")
    public Result<Void> adminDelete(@PathVariable Long goodsId) {
        goodsService.adminDeleteGoods(UserContext.getRequiredUserId(), goodsId);
        return Result.success("商品删除成功", null);
    }

    @PutMapping("/admin/{goodsId}/featured-categories")
    public Result<Void> assignFeaturedCategories(@PathVariable Long goodsId,
                                                 @RequestBody List<Long> featuredCategoryIds) {
        goodsService.adminAssignFeaturedCategories(UserContext.getRequiredUserId(), goodsId, featuredCategoryIds);
        return Result.success("特色分类分配成功", null);
    }
}
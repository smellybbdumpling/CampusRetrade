package com.campus.trade.service;

import com.campus.trade.common.PageResult;
import com.campus.trade.dto.AdminAuditGoodsRequest;
import com.campus.trade.dto.AdminFeaturedCategoryQueryDTO;
import com.campus.trade.dto.AdminFeaturedCategorySaveRequest;
import com.campus.trade.dto.AdminGoodsQueryDTO;
import com.campus.trade.dto.FeaturedSectionVO;
import com.campus.trade.dto.GoodsQueryDTO;
import com.campus.trade.dto.GoodsPublishRequest;
import com.campus.trade.dto.GoodsUpdateRequest;
import com.campus.trade.dto.GoodsVO;
import com.campus.trade.dto.UpdateGoodsStatusRequest;
import com.campus.trade.entity.FeaturedCategory;

import java.util.List;

public interface GoodsService {

    PageResult<GoodsVO> pageGoods(GoodsQueryDTO queryDTO);

    GoodsVO getGoodsDetail(Long goodsId, Long currentUserId);

    Long publishGoods(Long userId, GoodsPublishRequest request);

    List<GoodsVO> listMyGoods(Long userId);

    List<GoodsVO> listMyFavoriteGoods(Long userId);

    void favoriteGoods(Long userId, Long goodsId);

    void unfavoriteGoods(Long userId, Long goodsId);

    void updateMyGoods(Long userId, Long goodsId, GoodsUpdateRequest request);

    void updateMyGoodsStatus(Long userId, Long goodsId, UpdateGoodsStatusRequest request);

    PageResult<GoodsVO> adminPageGoods(AdminGoodsQueryDTO queryDTO);

    void adminAuditGoods(Long adminUserId, Long goodsId, AdminAuditGoodsRequest request);

    void adminUpdateGoods(Long adminUserId, Long goodsId, GoodsUpdateRequest request);

    void adminUpdateGoodsStatus(Long adminUserId, Long goodsId, UpdateGoodsStatusRequest request);

    void adminDeleteGoods(Long adminUserId, Long goodsId);

    void adminAssignFeaturedCategories(Long adminUserId, Long goodsId, List<Long> featuredCategoryIds);

    List<FeaturedSectionVO> listFeaturedSections();

    PageResult<FeaturedCategory> adminPageFeaturedCategories(Long adminUserId, AdminFeaturedCategoryQueryDTO queryDTO);

    void adminCreateFeaturedCategory(Long adminUserId, AdminFeaturedCategorySaveRequest request);

    void adminUpdateFeaturedCategory(Long adminUserId, Long featuredCategoryId, AdminFeaturedCategorySaveRequest request);
}
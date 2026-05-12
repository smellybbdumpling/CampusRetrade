package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.common.PageResult;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminAuditGoodsRequest;
import com.campus.trade.dto.AdminFeaturedCategoryQueryDTO;
import com.campus.trade.dto.AdminFeaturedCategorySaveRequest;
import com.campus.trade.dto.AdminGoodsQueryDTO;
import com.campus.trade.dto.FeaturedCategoryVO;
import com.campus.trade.dto.FeaturedSectionVO;
import com.campus.trade.dto.GoodsAuditLogVO;
import com.campus.trade.dto.GoodsImageVO;
import com.campus.trade.dto.GoodsMessageVO;
import com.campus.trade.dto.GoodsQueryDTO;
import com.campus.trade.dto.GoodsPublishRequest;
import com.campus.trade.dto.GoodsUpdateRequest;
import com.campus.trade.dto.GoodsVO;
import com.campus.trade.dto.UpdateGoodsStatusRequest;
import com.campus.trade.entity.Category;
import com.campus.trade.entity.CartItem;
import com.campus.trade.entity.FavoriteGoods;
import com.campus.trade.entity.FeaturedCategory;
import com.campus.trade.entity.GoodsAuditLog;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.GoodsFeaturedCategory;
import com.campus.trade.entity.GoodsImage;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.CartItemMapper;
import com.campus.trade.mapper.FavoriteGoodsMapper;
import com.campus.trade.mapper.FeaturedCategoryMapper;
import com.campus.trade.mapper.GoodsAuditLogMapper;
import com.campus.trade.mapper.GoodsFeaturedCategoryMapper;
import com.campus.trade.mapper.GoodsImageMapper;
import com.campus.trade.mapper.GoodsMessageMapper;
import com.campus.trade.mapper.CategoryMapper;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final GoodsAuditLogMapper goodsAuditLogMapper;
    private final FeaturedCategoryMapper featuredCategoryMapper;
    private final GoodsFeaturedCategoryMapper goodsFeaturedCategoryMapper;
    private final CartItemMapper cartItemMapper;
    private final FavoriteGoodsMapper favoriteGoodsMapper;
    private final GoodsMessageMapper goodsMessageMapper;

    @Override
    public PageResult<GoodsVO> pageGoods(GoodsQueryDTO queryDTO) {
        Page<Goods> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, "ON_SALE")
                .orderByDesc(Goods::getCreateTime);
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(Goods::getTitle, queryDTO.getKeyword());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Goods::getCategoryId, queryDTO.getCategoryId());
        }
        Page<Goods> goodsPage = goodsMapper.selectPage(page, wrapper);
        List<GoodsVO> records = goodsPage.getRecords().stream().map(goods -> {
            GoodsVO vo = new GoodsVO();
            vo.setId(goods.getId());
            vo.setCategoryId(goods.getCategoryId());
            Category category = categoryMapper.selectById(goods.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
            vo.setSellerId(goods.getSellerId());
            User seller = userMapper.selectById(goods.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
            }
            vo.setTitle(goods.getTitle());
            vo.setPrice(goods.getPrice());
            vo.setDescription(goods.getDescription());
            vo.setCoverImage(goods.getCoverImage());
            vo.setStatus(goods.getStatus());
            vo.setStock(goods.getStock());
            vo.setFavorited(false);
            return vo;
        }).toList();
        return new PageResult<>(goodsPage.getTotal(), records);
    }

    @Override
    public GoodsVO getGoodsDetail(Long goodsId, Long currentUserId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if (!"ON_SALE".equals(goods.getStatus())) {
            boolean owner = currentUserId != null && currentUserId.equals(goods.getSellerId());
            boolean admin = false;
            if (currentUserId != null) {
                User currentUser = userMapper.selectById(currentUserId);
                admin = currentUser != null && "ADMIN".equals(currentUser.getRole());
            }
            if (!owner && !admin) {
                throw new BusinessException("商品暂不可查看");
            }
        }
        return toGoodsVO(goods, currentUserId);
    }

    @Override
    @Transactional
    public Long publishGoods(Long userId, GoodsPublishRequest request) {
        User seller = userMapper.selectById(userId);
        if (seller == null || !"NORMAL".equals(seller.getStatus())) {
            throw new BusinessException("当前用户不可发布商品");
        }
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null || !"ENABLED".equals(category.getStatus())) {
            throw new BusinessException("商品分类不存在或已停用");
        }
        Goods goods = new Goods();
        goods.setSellerId(userId);
        goods.setCategoryId(request.getCategoryId());
        goods.setTitle(request.getTitle());
        goods.setPrice(request.getPrice());
        goods.setDescription(request.getDescription());
        goods.setCoverImage(request.getCoverImage());
        goods.setStatus("PENDING");
        goods.setStock(request.getStock());
        goodsMapper.insert(goods);
        saveGoodsImages(goods.getId(), request.getCoverImage(), request.getImageUrls());
        return goods.getId();
    }

    @Override
    public List<GoodsVO> listMyGoods(Long userId) {
        return goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getSellerId, userId)
                        .orderByDesc(Goods::getCreateTime))
                .stream()
                .map(goods -> toGoodsVO(goods, userId))
                .toList();
    }

    @Override
    public List<GoodsVO> listMyFavoriteGoods(Long userId) {
        return favoriteGoodsMapper.selectList(new LambdaQueryWrapper<FavoriteGoods>()
                        .eq(FavoriteGoods::getUserId, userId)
                        .orderByDesc(FavoriteGoods::getCreateTime, FavoriteGoods::getId))
                .stream()
                .map(FavoriteGoods::getGoodsId)
                .map(goodsMapper::selectById)
                .filter(goods -> goods != null)
                .map(goods -> toGoodsVO(goods, userId))
                .toList();
    }

    @Override
    public void favoriteGoods(Long userId, Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if (userId.equals(goods.getSellerId())) {
            throw new BusinessException("不能收藏自己发布的商品");
        }
        Long count = favoriteGoodsMapper.selectCount(new LambdaQueryWrapper<FavoriteGoods>()
                .eq(FavoriteGoods::getUserId, userId)
                .eq(FavoriteGoods::getGoodsId, goodsId));
        if (count != null && count > 0) {
            return;
        }
        FavoriteGoods favoriteGoods = new FavoriteGoods();
        favoriteGoods.setUserId(userId);
        favoriteGoods.setGoodsId(goodsId);
        favoriteGoodsMapper.insert(favoriteGoods);
    }

    @Override
    public void unfavoriteGoods(Long userId, Long goodsId) {
        favoriteGoodsMapper.delete(new LambdaQueryWrapper<FavoriteGoods>()
                .eq(FavoriteGoods::getUserId, userId)
                .eq(FavoriteGoods::getGoodsId, goodsId));
    }

    @Override
    @Transactional
    public void updateMyGoods(Long userId, Long goodsId, GoodsUpdateRequest request) {
        Goods goods = getOwnedGoods(userId, goodsId);
        if ("SOLD".equals(goods.getStatus())) {
            throw new BusinessException("已售出商品不允许编辑");
        }
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null || !"ENABLED".equals(category.getStatus())) {
            throw new BusinessException("商品分类不存在或已停用");
        }
        goods.setCategoryId(request.getCategoryId());
        goods.setTitle(request.getTitle());
        goods.setPrice(request.getPrice());
        goods.setDescription(request.getDescription());
        goods.setCoverImage(request.getCoverImage());
        goods.setStock(request.getStock());
        goods.setStatus("PENDING");
        goodsMapper.updateById(goods);
        replaceGoodsImages(goods.getId(), request.getCoverImage(), request.getImageUrls());
    }

    @Override
    public void updateMyGoodsStatus(Long userId, Long goodsId, UpdateGoodsStatusRequest request) {
        Goods goods = getOwnedGoods(userId, goodsId);
        String targetStatus = request.getStatus();
        if (!"OFF_SHELF".equals(targetStatus) && !"ON_SALE".equals(targetStatus)) {
            throw new BusinessException("仅支持上架或下架操作");
        }
        if ("SOLD".equals(goods.getStatus())) {
            throw new BusinessException("已售出商品不允许变更状态");
        }
        if ("ON_SALE".equals(targetStatus) && !"OFF_SHELF".equals(goods.getStatus())) {
            throw new BusinessException("只有已下架商品才能重新上架");
        }
        goods.setStatus(targetStatus);
        goodsMapper.updateById(goods);
    }

    @Override
    public PageResult<GoodsVO> adminPageGoods(AdminGoodsQueryDTO queryDTO) {
        Page<Goods> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .orderByDesc(Goods::getCreateTime);
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(Goods::getTitle, queryDTO.getKeyword());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Goods::getCategoryId, queryDTO.getCategoryId());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(Goods::getStatus, queryDTO.getStatus());
        }
        Page<Goods> goodsPage = goodsMapper.selectPage(page, wrapper);
        List<GoodsVO> records = goodsPage.getRecords().stream().map(goods -> toGoodsVO(goods, null)).toList();
        return new PageResult<>(goodsPage.getTotal(), records);
    }

    @Override
    @Transactional
    public void adminAuditGoods(Long adminUserId, Long goodsId, AdminAuditGoodsRequest request) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if (!"PENDING".equals(goods.getStatus())) {
            throw new BusinessException("当前商品不是待审核状态");
        }
        if (Boolean.TRUE.equals(request.getApproved())) {
            goods.setStatus("ON_SALE");
        } else {
            goods.setStatus("REJECTED");
        }
        goodsMapper.updateById(goods);

        GoodsAuditLog auditLog = new GoodsAuditLog();
        auditLog.setGoodsId(goodsId);
        auditLog.setAdminId(adminUserId);
        auditLog.setAuditStatus(goods.getStatus());
        auditLog.setAuditRemark(request.getAuditRemark());
        auditLog.setCreateTime(LocalDateTime.now());
        auditLog.setUpdateTime(LocalDateTime.now());
        goodsAuditLogMapper.insert(auditLog);

        goodsFeaturedCategoryMapper.delete(new LambdaQueryWrapper<GoodsFeaturedCategory>()
                .eq(GoodsFeaturedCategory::getGoodsId, goodsId));
        if (request.getFeaturedCategoryIds() != null) {
            for (Long featuredCategoryId : request.getFeaturedCategoryIds()) {
                FeaturedCategory featuredCategory = featuredCategoryMapper.selectById(featuredCategoryId);
                if (featuredCategory == null || !"ENABLED".equals(featuredCategory.getStatus())) {
                    continue;
                }
                GoodsFeaturedCategory relation = new GoodsFeaturedCategory();
                relation.setGoodsId(goodsId);
                relation.setFeaturedCategoryId(featuredCategoryId);
                relation.setCreateTime(LocalDateTime.now());
                relation.setUpdateTime(LocalDateTime.now());
                goodsFeaturedCategoryMapper.insert(relation);
            }
        }
    }

    @Override
    @Transactional
    public void adminUpdateGoods(Long adminUserId, Long goodsId, GoodsUpdateRequest request) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if ("SOLD".equals(goods.getStatus()) && request.getStock() > 0) {
            throw new BusinessException("已售出商品不允许直接改回有库存状态");
        }
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null || !"ENABLED".equals(category.getStatus())) {
            throw new BusinessException("商品分类不存在或已停用");
        }
        goods.setCategoryId(request.getCategoryId());
        goods.setTitle(request.getTitle());
        goods.setPrice(request.getPrice());
        goods.setDescription(request.getDescription());
        goods.setCoverImage(request.getCoverImage());
        goods.setStock(request.getStock());
        goodsMapper.updateById(goods);
        replaceGoodsImages(goods.getId(), request.getCoverImage(), request.getImageUrls());
    }

    @Override
    public void adminUpdateGoodsStatus(Long adminUserId, Long goodsId, UpdateGoodsStatusRequest request) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        String targetStatus = request.getStatus();
        if (!"ON_SALE".equals(targetStatus) && !"OFF_SHELF".equals(targetStatus)) {
            throw new BusinessException("仅支持上架或下架操作");
        }
        if ("SOLD".equals(goods.getStatus())) {
            throw new BusinessException("已售出商品不允许上架或下架");
        }
        if ("ON_SALE".equals(targetStatus) && goods.getStock() <= 0) {
            throw new BusinessException("库存不足，无法上架");
        }
        goods.setStatus(targetStatus);
        goodsMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void adminDeleteGoods(Long adminUserId, Long goodsId) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if ("SOLD".equals(goods.getStatus())) {
            throw new BusinessException("已售出商品不允许删除");
        }
        goodsFeaturedCategoryMapper.delete(new LambdaQueryWrapper<GoodsFeaturedCategory>()
                .eq(GoodsFeaturedCategory::getGoodsId, goodsId));
        goodsAuditLogMapper.delete(new LambdaQueryWrapper<GoodsAuditLog>()
                .eq(GoodsAuditLog::getGoodsId, goodsId));
        goodsImageMapper.delete(new LambdaQueryWrapper<GoodsImage>()
                .eq(GoodsImage::getGoodsId, goodsId));
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getGoodsId, goodsId));
        favoriteGoodsMapper.delete(new LambdaQueryWrapper<FavoriteGoods>()
            .eq(FavoriteGoods::getGoodsId, goodsId));
        goodsMapper.deleteById(goodsId);
    }

    @Override
    @Transactional
    public void adminAssignFeaturedCategories(Long adminUserId, Long goodsId, List<Long> featuredCategoryIds) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if (!"ON_SALE".equals(goods.getStatus()) && !"PENDING".equals(goods.getStatus())) {
            throw new BusinessException("当前商品状态不允许分配特色分类");
        }
        goodsFeaturedCategoryMapper.delete(new LambdaQueryWrapper<GoodsFeaturedCategory>()
                .eq(GoodsFeaturedCategory::getGoodsId, goodsId));
        if (featuredCategoryIds == null) {
            return;
        }
        for (Long featuredCategoryId : featuredCategoryIds) {
            FeaturedCategory featuredCategory = featuredCategoryMapper.selectById(featuredCategoryId);
            if (featuredCategory == null || !"ENABLED".equals(featuredCategory.getStatus())) {
                continue;
            }
            GoodsFeaturedCategory relation = new GoodsFeaturedCategory();
            relation.setGoodsId(goodsId);
            relation.setFeaturedCategoryId(featuredCategoryId);
            relation.setCreateTime(LocalDateTime.now());
            relation.setUpdateTime(LocalDateTime.now());
            goodsFeaturedCategoryMapper.insert(relation);
        }
    }

    @Override
    public List<FeaturedSectionVO> listFeaturedSections() {
        List<FeaturedCategory> featuredCategories = featuredCategoryMapper.selectList(new LambdaQueryWrapper<FeaturedCategory>()
                .eq(FeaturedCategory::getStatus, "ENABLED")
                .orderByAsc(FeaturedCategory::getSortOrder, FeaturedCategory::getId));
        return featuredCategories.stream().map(category -> {
            FeaturedSectionVO section = new FeaturedSectionVO();
            section.setId(category.getId());
            section.setName(category.getName());

            List<Long> goodsIds = goodsFeaturedCategoryMapper.selectList(new LambdaQueryWrapper<GoodsFeaturedCategory>()
                            .eq(GoodsFeaturedCategory::getFeaturedCategoryId, category.getId())
                            .orderByDesc(GoodsFeaturedCategory::getId))
                    .stream()
                    .map(GoodsFeaturedCategory::getGoodsId)
                    .toList();

            List<GoodsVO> goodsList = goodsIds.stream()
                    .map(goodsMapper::selectById)
                    .filter(goods -> goods != null && "ON_SALE".equals(goods.getStatus()))
                    .limit(6)
                    .map(goods -> toGoodsVO(goods, null))
                    .toList();
            section.setGoodsList(goodsList);
            return section;
        }).toList();
    }

    @Override
    public PageResult<FeaturedCategory> adminPageFeaturedCategories(Long adminUserId, AdminFeaturedCategoryQueryDTO queryDTO) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Page<FeaturedCategory> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<FeaturedCategory> wrapper = new LambdaQueryWrapper<FeaturedCategory>()
                .orderByAsc(FeaturedCategory::getSortOrder, FeaturedCategory::getId);
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(FeaturedCategory::getName, queryDTO.getName());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(FeaturedCategory::getStatus, queryDTO.getStatus());
        }
        Page<FeaturedCategory> result = featuredCategoryMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public void adminCreateFeaturedCategory(Long adminUserId, AdminFeaturedCategorySaveRequest request) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        FeaturedCategory category = new FeaturedCategory();
        category.setName(request.getName());
        category.setStatus(request.getStatus());
        category.setSortOrder(request.getSortOrder());
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        featuredCategoryMapper.insert(category);
    }

    @Override
    public void adminUpdateFeaturedCategory(Long adminUserId, Long featuredCategoryId, AdminFeaturedCategorySaveRequest request) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        FeaturedCategory category = featuredCategoryMapper.selectById(featuredCategoryId);
        if (category == null) {
            throw new BusinessException("特色分类不存在");
        }
        category.setName(request.getName());
        category.setStatus(request.getStatus());
        category.setSortOrder(request.getSortOrder());
        featuredCategoryMapper.updateById(category);
    }

    private GoodsVO toGoodsVO(Goods goods, Long currentUserId) {
        GoodsVO vo = new GoodsVO();
        vo.setId(goods.getId());
        vo.setCategoryId(goods.getCategoryId());
        Category category = categoryMapper.selectById(goods.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        vo.setSellerId(goods.getSellerId());
        User seller = userMapper.selectById(goods.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
        }
        vo.setTitle(goods.getTitle());
        vo.setPrice(goods.getPrice());
        vo.setDescription(goods.getDescription());
        vo.setCoverImage(goods.getCoverImage());
        vo.setImages(listGoodsImages(goods.getId()));
        vo.setStatus(goods.getStatus());
        vo.setStock(goods.getStock());
        vo.setFavorited(isFavorited(goods.getId(), currentUserId));
        vo.setMessages(listGoodsMessages(goods.getId()));
        List<GoodsAuditLogVO> auditLogs = listGoodsAuditLogs(goods.getId());
        vo.setAuditLogs(auditLogs);
        if (!auditLogs.isEmpty()) {
            vo.setLatestAuditRemark(auditLogs.get(0).getAuditRemark());
        }
        vo.setFeaturedCategories(listFeaturedCategoriesByGoodsId(goods.getId()));
        return vo;
    }

    private boolean isFavorited(Long goodsId, Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }
        Long count = favoriteGoodsMapper.selectCount(new LambdaQueryWrapper<FavoriteGoods>()
                .eq(FavoriteGoods::getUserId, currentUserId)
                .eq(FavoriteGoods::getGoodsId, goodsId));
        return count != null && count > 0;
    }

    private List<GoodsMessageVO> listGoodsMessages(Long goodsId) {
        List<GoodsMessageVO> allMessages = goodsMessageMapper.selectList(new LambdaQueryWrapper<com.campus.trade.entity.GoodsMessage>()
                        .eq(com.campus.trade.entity.GoodsMessage::getGoodsId, goodsId)
                        .orderByAsc(com.campus.trade.entity.GoodsMessage::getCreateTime, com.campus.trade.entity.GoodsMessage::getId))
                .stream()
                .map(message -> {
                    GoodsMessageVO vo = new GoodsMessageVO();
                    vo.setId(message.getId());
                    vo.setGoodsId(message.getGoodsId());
                    vo.setSenderId(message.getSenderId());
                    User sender = userMapper.selectById(message.getSenderId());
                    vo.setSenderName(sender == null ? "未知用户" : sender.getNickname());
                    vo.setParentId(message.getParentId());
                    vo.setContent(message.getContent());
                    vo.setCreateTime(message.getCreateTime());
                    vo.setReplies(new ArrayList<>());
                    return vo;
                })
                .toList();

        java.util.Map<Long, GoodsMessageVO> messageMap = new java.util.LinkedHashMap<>();
        for (GoodsMessageVO message : allMessages) {
            messageMap.put(message.getId(), message);
        }
        List<GoodsMessageVO> roots = new ArrayList<>();
        for (GoodsMessageVO message : allMessages) {
            if (message.getParentId() == null) {
                roots.add(message);
            } else {
                GoodsMessageVO parent = messageMap.get(message.getParentId());
                if (parent != null) {
                    parent.getReplies().add(message);
                }
            }
        }
        return roots;
    }

    private Goods getOwnedGoods(Long userId, Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if (!userId.equals(goods.getSellerId())) {
            throw new BusinessException("只能操作自己发布的商品");
        }
        return goods;
    }

    private void saveGoodsImages(Long goodsId, String coverImage, List<String> imageUrls) {
        List<String> normalizedUrls = normalizeImageUrls(coverImage, imageUrls);
        for (int index = 0; index < normalizedUrls.size(); index++) {
            GoodsImage goodsImage = new GoodsImage();
            goodsImage.setGoodsId(goodsId);
            goodsImage.setImageUrl(normalizedUrls.get(index));
            goodsImage.setSortOrder(index);
            goodsImage.setCreateTime(LocalDateTime.now());
            goodsImage.setUpdateTime(LocalDateTime.now());
            goodsImageMapper.insert(goodsImage);
        }
    }

    private void replaceGoodsImages(Long goodsId, String coverImage, List<String> imageUrls) {
        goodsImageMapper.delete(new LambdaQueryWrapper<GoodsImage>().eq(GoodsImage::getGoodsId, goodsId));
        saveGoodsImages(goodsId, coverImage, imageUrls);
    }

    private List<String> normalizeImageUrls(String coverImage, List<String> imageUrls) {
        List<String> result = new ArrayList<>();
        result.add(coverImage);
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                if (StringUtils.hasText(imageUrl) && !result.contains(imageUrl)) {
                    result.add(imageUrl);
                }
            }
        }
        return result;
    }

    private List<GoodsImageVO> listGoodsImages(Long goodsId) {
        return goodsImageMapper.selectList(new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, goodsId)
                        .orderByAsc(GoodsImage::getSortOrder, GoodsImage::getId))
                .stream()
                .map(image -> {
                    GoodsImageVO vo = new GoodsImageVO();
                    vo.setId(image.getId());
                    vo.setImageUrl(image.getImageUrl());
                    vo.setSortOrder(image.getSortOrder());
                    return vo;
                })
                .toList();
    }

    private List<GoodsAuditLogVO> listGoodsAuditLogs(Long goodsId) {
        return goodsAuditLogMapper.selectList(new LambdaQueryWrapper<GoodsAuditLog>()
                        .eq(GoodsAuditLog::getGoodsId, goodsId)
                        .orderByDesc(GoodsAuditLog::getCreateTime, GoodsAuditLog::getId))
                .stream()
                .map(log -> {
                    GoodsAuditLogVO vo = new GoodsAuditLogVO();
                    vo.setId(log.getId());
                    vo.setAdminId(log.getAdminId());
                    User admin = userMapper.selectById(log.getAdminId());
                    if (admin != null) {
                        vo.setAdminName(admin.getNickname());
                    }
                    vo.setAuditStatus(log.getAuditStatus());
                    vo.setAuditRemark(log.getAuditRemark());
                    vo.setCreateTime(log.getCreateTime());
                    return vo;
                })
                .toList();
    }

    private List<FeaturedCategoryVO> listFeaturedCategoriesByGoodsId(Long goodsId) {
        return goodsFeaturedCategoryMapper.selectList(new LambdaQueryWrapper<GoodsFeaturedCategory>()
                        .eq(GoodsFeaturedCategory::getGoodsId, goodsId)
                        .orderByAsc(GoodsFeaturedCategory::getId))
                .stream()
                .map(item -> featuredCategoryMapper.selectById(item.getFeaturedCategoryId()))
                .filter(category -> category != null)
                .map(category -> {
                    FeaturedCategoryVO vo = new FeaturedCategoryVO();
                    vo.setId(category.getId());
                    vo.setName(category.getName());
                    return vo;
                })
                .toList();
    }
}
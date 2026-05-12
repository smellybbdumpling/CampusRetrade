package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.AddCartItemRequest;
import com.campus.trade.dto.CartItemVO;
import com.campus.trade.entity.CartItem;
import com.campus.trade.entity.Goods;
import com.campus.trade.mapper.CartItemMapper;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final GoodsMapper goodsMapper;

    @Override
    public void addToCart(Long userId, AddCartItemRequest request) {
        Goods goods = goodsMapper.selectById(request.getGoodsId());
        if (goods == null || !"ON_SALE".equals(goods.getStatus())) {
            throw new BusinessException("商品不存在或不可购买");
        }
        CartItem anyItem = cartItemMapper.selectAnyByUserIdAndGoodsId(userId, request.getGoodsId());
        if (anyItem != null && Integer.valueOf(1).equals(anyItem.getDeleted())) {
            cartItemMapper.physicalDeleteById(anyItem.getId());
        }
        CartItem existing = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getGoodsId, request.getGoodsId())
                .last("limit 1"));
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemMapper.updateById(existing);
            return;
        }
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setGoodsId(request.getGoodsId());
        cartItem.setQuantity(request.getQuantity());
        cartItemMapper.insert(cartItem);
    }

    @Override
    public List<CartItemVO> listCartItems(Long userId) {
        return cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .orderByDesc(CartItem::getCreateTime))
                .stream()
                .map(item -> {
                    Goods goods = goodsMapper.selectById(item.getGoodsId());
                    if (goods == null) {
                        return null;
                    }
                    CartItemVO vo = new CartItemVO();
                    vo.setId(item.getId());
                    vo.setGoodsId(goods.getId());
                    vo.setTitle(goods.getTitle());
                    vo.setCoverImage(goods.getCoverImage());
                    vo.setPrice(goods.getPrice());
                    vo.setQuantity(item.getQuantity());
                    vo.setSubtotal(goods.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return vo;
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemMapper.selectById(cartItemId);
        if (cartItem == null || !userId.equals(cartItem.getUserId())) {
            throw new BusinessException("购物车记录不存在");
        }
        cartItemMapper.physicalDeleteById(cartItemId);
    }
}
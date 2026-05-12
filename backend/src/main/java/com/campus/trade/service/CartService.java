package com.campus.trade.service;

import com.campus.trade.dto.AddCartItemRequest;
import com.campus.trade.dto.CartItemVO;

import java.util.List;

public interface CartService {

    void addToCart(Long userId, AddCartItemRequest request);

    List<CartItemVO> listCartItems(Long userId);

    void removeCartItem(Long userId, Long cartItemId);
}
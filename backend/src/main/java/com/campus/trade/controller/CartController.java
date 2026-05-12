package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AddCartItemRequest;
import com.campus.trade.dto.CartItemVO;
import com.campus.trade.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(@Valid @RequestBody AddCartItemRequest request) {
        Long userId = UserContext.getRequiredUserId();
        cartService.addToCart(userId, request);
        return Result.success("加入购物车成功", null);
    }

    @GetMapping("/list")
    public Result<List<CartItemVO>> list() {
        Long userId = UserContext.getRequiredUserId();
        return Result.success(cartService.listCartItems(userId));
    }

    @DeleteMapping("/{cartItemId}")
    public Result<Void> remove(@PathVariable Long cartItemId) {
        Long userId = UserContext.getRequiredUserId();
        cartService.removeCartItem(userId, cartItemId);
        return Result.success("删除成功", null);
    }
}
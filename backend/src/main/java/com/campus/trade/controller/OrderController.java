package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.OrderVO;
import com.campus.trade.dto.SubmitOrderRequest;
import com.campus.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderVO> submit(@RequestBody(required = false) SubmitOrderRequest request) {
        Long userId = UserContext.getRequiredUserId();
        return Result.success(orderService.submitOrder(userId, request));
    }

    @GetMapping("/my")
    public Result<List<OrderVO>> myOrders() {
        Long userId = UserContext.getRequiredUserId();
        return Result.success(orderService.listMyOrders(userId));
    }

    @GetMapping("/my/sell")
    public Result<List<OrderVO>> mySellOrders() {
        return Result.success(orderService.listSellOrders(UserContext.getRequiredUserId()));
    }

    @GetMapping("/{orderId}")
    public Result<OrderVO> detail(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderDetail(UserContext.getRequiredUserId(), orderId));
    }

    @GetMapping("/sell/{orderId}")
    public Result<OrderVO> sellDetail(@PathVariable Long orderId) {
        return Result.success(orderService.getSellOrderDetail(UserContext.getRequiredUserId(), orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(UserContext.getRequiredUserId(), orderId);
        return Result.success("订单取消成功", null);
    }

    @PutMapping("/{orderId}/finish")
    public Result<Void> finish(@PathVariable Long orderId) {
        orderService.finishOrder(UserContext.getRequiredUserId(), orderId);
        return Result.success("确认收货成功", null);
    }

    @PutMapping("/{orderId}/ship")
    public Result<Void> ship(@PathVariable Long orderId) {
        orderService.shipOrder(UserContext.getRequiredUserId(), orderId);
        return Result.success("订单发货成功", null);
    }
}
package com.campus.trade.service;

import com.campus.trade.dto.OrderVO;
import com.campus.trade.dto.SubmitOrderRequest;

import java.util.List;

public interface OrderService {

    OrderVO submitOrder(Long userId, SubmitOrderRequest request);

    List<OrderVO> listMyOrders(Long userId);

    List<OrderVO> listSellOrders(Long sellerId);

    OrderVO getOrderDetail(Long userId, Long orderId);

    OrderVO getSellOrderDetail(Long sellerId, Long orderId);

    void cancelOrder(Long userId, Long orderId);

    void finishOrder(Long userId, Long orderId);

    void shipOrder(Long sellerId, Long orderId);
}
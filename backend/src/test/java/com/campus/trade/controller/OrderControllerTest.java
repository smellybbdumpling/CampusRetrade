package com.campus.trade.controller;

import com.campus.trade.entity.OrderItem;
import com.campus.trade.entity.Orders;
import com.campus.trade.mapper.OrderItemMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Test
    void myOrdersShouldRequireLogin() throws Exception {
        mockMvc.perform(get("/api/orders/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("未登录或登录已过期"));
    }

    @Test
    void myOrdersShouldReturnOrderListForLoggedInBuyer() throws Exception {
        Orders order = new Orders();
        order.setOrderNo("ORDTEST001");
        order.setBuyerId(buyerUser.getId());
        order.setSellerId(sellerUser.getId());
        order.setTotalAmount(new BigDecimal("66.00"));
        order.setStatus("CREATED");
        order.setRemark("测试订单");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setDeleted(0);
        ordersMapper.insert(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setGoodsId(onSaleGoods.getId());
        orderItem.setGoodsTitle(onSaleGoods.getTitle());
        orderItem.setGoodsPrice(onSaleGoods.getPrice());
        orderItem.setQuantity(1);
        orderItem.setSubtotalAmount(onSaleGoods.getPrice());
        orderItem.setCreateTime(LocalDateTime.now());
        orderItem.setUpdateTime(LocalDateTime.now());
        orderItemMapper.insert(orderItem);

        mockMvc.perform(get("/api/orders/my")
                        .header("Authorization", bearerTokenForBuyer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].orderNo").value("ORDTEST001"))
                .andExpect(jsonPath("$.data[0].buyerId").value(buyerUser.getId()));
    }

    @Test
    void cancelShouldReturnBusinessErrorWhenOrderNotOwned() throws Exception {
        Orders order = new Orders();
        order.setOrderNo("ORDTEST002");
        order.setBuyerId(sellerUser.getId());
        order.setSellerId(buyerUser.getId());
        order.setTotalAmount(new BigDecimal("10.00"));
        order.setStatus("CREATED");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setDeleted(0);
        ordersMapper.insert(order);

        mockMvc.perform(put("/api/orders/{orderId}/cancel", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearerTokenForBuyer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("只能操作自己的订单"));
    }
}
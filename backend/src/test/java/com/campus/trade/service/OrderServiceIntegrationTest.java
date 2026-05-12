package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.IntegrationTestSupport;
import com.campus.trade.dto.OrderVO;
import com.campus.trade.entity.CartItem;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.Orders;
import com.campus.trade.mapper.CartItemMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartItemMapper localCartItemMapper;

    @Test
    void orderLifecycleShouldSubmitShipAndFinishSuccessfully() {
        createCartItem(buyerUser.getId(), onSaleGoods.getId(), 1);

        OrderVO createdOrder = orderService.submitOrder(buyerUser.getId(), null);
        orderService.shipOrder(sellerUser.getId(), createdOrder.getId());
        orderService.finishOrder(buyerUser.getId(), createdOrder.getId());

        Orders persistedOrder = ordersMapper.selectById(createdOrder.getId());
        Goods updatedGoods = goodsMapper.selectById(onSaleGoods.getId());
        CartItem cartItem = localCartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, buyerUser.getId())
                .eq(CartItem::getGoodsId, onSaleGoods.getId())
                .last("limit 1"));

        assertThat(createdOrder.getStatus()).isEqualTo("CREATED");
        assertThat(persistedOrder.getStatus()).isEqualTo("FINISHED");
        assertThat(updatedGoods.getStock()).isEqualTo(2);
        assertThat(cartItem).isNull();
    }
}
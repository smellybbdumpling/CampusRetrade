package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.OrderItemVO;
import com.campus.trade.dto.OrderVO;
import com.campus.trade.dto.SubmitOrderRequest;
import com.campus.trade.entity.CartItem;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.OrderItem;
import com.campus.trade.entity.Orders;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.CartItemMapper;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.OrderItemMapper;
import com.campus.trade.mapper.OrdersMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final GoodsMapper goodsMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public OrderVO submitOrder(Long userId, SubmitOrderRequest request) {
        List<CartItem> cartItems = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法提交订单");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        Long sellerId = null;
        for (CartItem cartItem : cartItems) {
            Goods goods = goodsMapper.selectById(cartItem.getGoodsId());
            if (goods == null || !"ON_SALE".equals(goods.getStatus())) {
                throw new BusinessException("存在不可下单商品，请刷新购物车后重试");
            }
            if (userId.equals(goods.getSellerId())) {
                throw new BusinessException("不能购买自己发布的商品");
            }
            if (sellerId == null) {
                sellerId = goods.getSellerId();
            } else if (!sellerId.equals(goods.getSellerId())) {
                throw new BusinessException("一次仅支持提交同一卖家的商品，请分开下单");
            }
            if (goods.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("商品库存不足：" + goods.getTitle());
            }
            totalAmount = totalAmount.add(goods.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        Orders order = new Orders();
        order.setOrderNo(generateOrderNo(userId));
        order.setBuyerId(userId);
        order.setSellerId(sellerId);
        order.setTotalAmount(totalAmount);
        order.setStatus("CREATED");
        order.setRemark(request == null ? null : request.getRemark());
        ordersMapper.insert(order);

        for (CartItem cartItem : cartItems) {
            Goods goods = goodsMapper.selectById(cartItem.getGoodsId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setGoodsId(goods.getId());
            orderItem.setGoodsTitle(goods.getTitle());
            orderItem.setGoodsPrice(goods.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotalAmount(goods.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);

            goods.setStock(goods.getStock() - cartItem.getQuantity());
            if (goods.getStock() <= 0) {
                goods.setStatus("SOLD");
            }
            goodsMapper.updateById(goods);
        }

        cartItems.forEach(item -> cartItemMapper.deleteById(item.getId()));
        return buildOrderVO(order);
    }

    @Override
    public List<OrderVO> listMyOrders(Long userId) {
        return ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getBuyerId, userId)
                        .orderByDesc(Orders::getCreateTime))
                .stream()
                .map(this::buildOrderVO)
                .toList();
    }

    @Override
    public List<OrderVO> listSellOrders(Long sellerId) {
        return ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getSellerId, sellerId)
                        .orderByDesc(Orders::getCreateTime))
                .stream()
                .map(this::buildOrderVO)
                .toList();
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Orders order = getOwnedOrder(userId, orderId);
        return buildOrderVO(order);
    }

    @Override
    public OrderVO getSellOrderDetail(Long sellerId, Long orderId) {
        Orders order = getSellerOrder(sellerId, orderId);
        return buildOrderVO(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Orders order = getOwnedOrder(userId, orderId);
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不可取消");
        }
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem orderItem : orderItems) {
            Goods goods = goodsMapper.selectById(orderItem.getGoodsId());
            if (goods != null) {
                goods.setStock(goods.getStock() + orderItem.getQuantity());
                if (!"OFF_SHELF".equals(goods.getStatus())) {
                    goods.setStatus("ON_SALE");
                }
                goodsMapper.updateById(goods);
            }
        }
        order.setStatus("CANCELED");
        ordersMapper.updateById(order);
    }

    @Override
    public void finishOrder(Long userId, Long orderId) {
        Orders order = getOwnedOrder(userId, orderId);
        if (!"SHIPPED".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不可确认收货");
        }
        order.setStatus("FINISHED");
        ordersMapper.updateById(order);
    }

    @Override
    public void shipOrder(Long sellerId, Long orderId) {
        Orders order = getSellerOrder(sellerId, orderId);
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不可发货");
        }
        order.setStatus("SHIPPED");
        ordersMapper.updateById(order);
    }

    private OrderVO buildOrderVO(Orders order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setBuyerId(order.getBuyerId());
        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getNickname());
        }
        vo.setSellerId(order.getSellerId());
        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
        }
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        List<OrderItemVO> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, order.getId()))
                .stream()
                .map(item -> {
                    OrderItemVO itemVO = new OrderItemVO();
                    itemVO.setGoodsId(item.getGoodsId());
                    itemVO.setGoodsTitle(item.getGoodsTitle());
                    itemVO.setGoodsPrice(item.getGoodsPrice());
                    itemVO.setQuantity(item.getQuantity());
                    itemVO.setSubtotalAmount(item.getSubtotalAmount());
                    return itemVO;
                })
                .toList();
        vo.setItems(items);
        return vo;
    }

    private String generateOrderNo(Long userId) {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + userId;
    }

    private Orders getOwnedOrder(Long userId, Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userId.equals(order.getBuyerId())) {
            throw new BusinessException("只能操作自己的订单");
        }
        return order;
    }

    private Orders getSellerOrder(Long sellerId, Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!sellerId.equals(order.getSellerId())) {
            throw new BusinessException("只能操作卖给他人的订单");
        }
        return order;
    }
}
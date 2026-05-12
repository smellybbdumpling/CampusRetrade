package com.campus.trade;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.entity.CartItem;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.GoodsReport;
import com.campus.trade.entity.Orders;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.CartItemMapper;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.GoodsReportMapper;
import com.campus.trade.mapper.OrdersMapper;
import com.campus.trade.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected GoodsMapper goodsMapper;

    @Autowired
    protected CartItemMapper cartItemMapper;

    @Autowired
    protected OrdersMapper ordersMapper;

    @Autowired
    protected GoodsReportMapper goodsReportMapper;

    protected User adminUser;
    protected User sellerUser;
    protected User buyerUser;
    protected Goods pendingGoods;
    protected Goods onSaleGoods;

    @BeforeEach
    void setUpBaseData() {
        adminUser = ensureUser("admin_test", "管理员测试", "ADMIN", "NORMAL");
        sellerUser = ensureUser("seller_test", "卖家测试", "USER", "NORMAL");
        buyerUser = ensureUser("buyer_test", "买家测试", "USER", "NORMAL");
        pendingGoods = ensureGoods("待审核测试商品", sellerUser.getId(), "PENDING", 2, new BigDecimal("88.00"));
        onSaleGoods = ensureGoods("在售测试商品", sellerUser.getId(), "ON_SALE", 3, new BigDecimal("66.00"));
    }

    protected User ensureUser(String username, String nickname, String role, String status) {
        User existing = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("limit 1"));
        if (existing != null) {
            return existing;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword("$2a$10$0w0sD0jEOJdD0a8mN8LJAeTq0zD2R4aW9S8J1Q4M8XH0vGxZt2Y1e");
        user.setNickname(nickname);
        user.setPhone("13900000000");
        user.setRole(role);
        user.setStatus(status);
        user.setDeleted(0);
        userMapper.insert(user);
        return user;
    }

    protected Goods ensureGoods(String title, Long sellerId, String status, int stock, BigDecimal price) {
        Goods existing = goodsMapper.selectOne(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getTitle, title)
                .last("limit 1"));
        if (existing != null) {
            return existing;
        }
        Goods goods = new Goods();
        goods.setSellerId(sellerId);
        goods.setCategoryId(1L);
        goods.setTitle(title);
        goods.setPrice(price);
        goods.setDescription(title + "描述");
        goods.setCoverImage("/uploads/test.png");
        goods.setStatus(status);
        goods.setStock(stock);
        goods.setDeleted(0);
        goodsMapper.insert(goods);
        return goods;
    }

    protected CartItem createCartItem(Long userId, Long goodsId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setGoodsId(goodsId);
        cartItem.setQuantity(quantity);
        cartItem.setDeleted(0);
        cartItemMapper.insert(cartItem);
        return cartItem;
    }

    protected Orders getLatestBuyerOrder(Long buyerId) {
        return ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getBuyerId, buyerId)
                .orderByDesc(Orders::getId)
                .last("limit 1"));
    }

    protected GoodsReport getLatestReport(Long reporterId) {
        return goodsReportMapper.selectOne(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getReporterId, reporterId)
                .orderByDesc(GoodsReport::getId)
                .last("limit 1"));
    }
}
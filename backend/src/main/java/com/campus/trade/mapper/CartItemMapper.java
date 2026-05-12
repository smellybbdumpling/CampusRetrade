package com.campus.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trade.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

public interface CartItemMapper extends BaseMapper<CartItem> {

	@Delete("DELETE FROM cart_item WHERE id = #{id}")
	int physicalDeleteById(@Param("id") Long id);

	@Select("SELECT id, user_id, goods_id, quantity, create_time, update_time, deleted FROM cart_item WHERE user_id = #{userId} AND goods_id = #{goodsId} LIMIT 1")
	CartItem selectAnyByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
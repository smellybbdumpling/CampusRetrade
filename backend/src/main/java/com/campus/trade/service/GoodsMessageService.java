package com.campus.trade.service;

import com.campus.trade.common.PageResult;
import com.campus.trade.dto.AdminGoodsMessageQueryDTO;
import com.campus.trade.dto.GoodsMessageSaveRequest;
import com.campus.trade.dto.GoodsMessageVO;

import java.util.List;

public interface GoodsMessageService {

    List<GoodsMessageVO> listGoodsMessages(Long goodsId, Long currentUserId);

    void saveGoodsMessage(Long userId, Long goodsId, GoodsMessageSaveRequest request);

    PageResult<GoodsMessageVO> adminPageMessages(Long adminUserId, AdminGoodsMessageQueryDTO queryDTO);

    void adminDeleteMessage(Long adminUserId, Long messageId);

    void adminDeleteAllMessages(Long adminUserId);
}
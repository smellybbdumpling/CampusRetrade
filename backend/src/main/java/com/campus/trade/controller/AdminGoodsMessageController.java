package com.campus.trade.controller;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminGoodsMessageQueryDTO;
import com.campus.trade.dto.GoodsMessageVO;
import com.campus.trade.service.GoodsMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/goods-messages")
@RequiredArgsConstructor
public class AdminGoodsMessageController {

    private final GoodsMessageService goodsMessageService;

    @GetMapping("/page")
    public Result<PageResult<GoodsMessageVO>> page(AdminGoodsMessageQueryDTO queryDTO) {
        UserContext.requireAdmin();
        return Result.success(goodsMessageService.adminPageMessages(UserContext.getRequiredUserId(), queryDTO));
    }

    @DeleteMapping("/{messageId}")
    public Result<Void> delete(@PathVariable Long messageId) {
        UserContext.requireAdmin();
        goodsMessageService.adminDeleteMessage(UserContext.getRequiredUserId(), messageId);
        return Result.success("留言删除成功", null);
    }

    @DeleteMapping
    public Result<Void> deleteAll() {
        UserContext.requireAdmin();
        goodsMessageService.adminDeleteAllMessages(UserContext.getRequiredUserId());
        return Result.success("全部留言已删除", null);
    }
}
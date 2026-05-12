package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.GoodsMessageSaveRequest;
import com.campus.trade.dto.GoodsMessageVO;
import com.campus.trade.service.GoodsMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goods/{goodsId}/messages")
@RequiredArgsConstructor
public class GoodsMessageController {

    private final GoodsMessageService goodsMessageService;

    @GetMapping
    public Result<List<GoodsMessageVO>> list(@PathVariable Long goodsId) {
        Long currentUserId = UserContext.getCurrentUser() == null ? null : UserContext.getRequiredUserId();
        return Result.success(goodsMessageService.listGoodsMessages(goodsId, currentUserId));
    }

    @PostMapping
    public Result<Void> save(@PathVariable Long goodsId, @Valid @RequestBody GoodsMessageSaveRequest request) {
        goodsMessageService.saveGoodsMessage(UserContext.getRequiredUserId(), goodsId, request);
        return Result.success("留言发送成功", null);
    }
}
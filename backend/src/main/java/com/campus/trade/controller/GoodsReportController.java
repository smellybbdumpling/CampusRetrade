package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.GoodsReportSaveRequest;
import com.campus.trade.dto.GoodsReportVO;
import com.campus.trade.service.GoodsReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GoodsReportController {

    private final GoodsReportService goodsReportService;

    @PostMapping("/goods/{goodsId}/report")
    public Result<Void> submit(@PathVariable Long goodsId, @Valid @RequestBody GoodsReportSaveRequest request) {
        goodsReportService.submitReport(UserContext.getRequiredUserId(), goodsId, request);
        return Result.success("举报提交成功", null);
    }

    @GetMapping("/goods-reports/{reportId}")
    public Result<GoodsReportVO> detail(@PathVariable Long reportId) {
        return Result.success(goodsReportService.getReportDetail(UserContext.getRequiredUserId(), reportId));
    }
}
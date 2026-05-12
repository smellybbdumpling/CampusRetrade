package com.campus.trade.controller;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminGoodsReportQueryDTO;
import com.campus.trade.dto.AdminHandleGoodsReportRequest;
import com.campus.trade.dto.GoodsReportVO;
import com.campus.trade.service.GoodsReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/goods-reports")
@RequiredArgsConstructor
public class AdminGoodsReportController {

    private final GoodsReportService goodsReportService;

    @GetMapping("/page")
    public Result<PageResult<GoodsReportVO>> page(AdminGoodsReportQueryDTO queryDTO) {
        UserContext.requireAdmin();
        return Result.success(goodsReportService.adminPageReports(UserContext.getRequiredUserId(), queryDTO));
    }

    @PutMapping("/{reportId}/handle")
    public Result<Void> handle(@PathVariable Long reportId, @Valid @RequestBody AdminHandleGoodsReportRequest request) {
        UserContext.requireAdmin();
        goodsReportService.handleReport(UserContext.getRequiredUserId(), reportId, request);
        return Result.success("举报处理成功", null);
    }
}
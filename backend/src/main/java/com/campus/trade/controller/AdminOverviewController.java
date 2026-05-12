package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminOverviewVO;
import com.campus.trade.dto.AdminTrendVO;
import com.campus.trade.dto.CategoryDistributionVO;
import com.campus.trade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminOverviewController {

    private final UserService userService;

    @GetMapping("/overview")
    public Result<AdminOverviewVO> overview() {
        UserContext.requireAdmin();
        return Result.success(userService.adminOverview(UserContext.getRequiredUserId()));
    }

    @GetMapping("/trend")
    public Result<AdminTrendVO> trend() {
        UserContext.requireAdmin();
        return Result.success(userService.adminTrend(UserContext.getRequiredUserId()));
    }

    @GetMapping("/category")
    public Result<List<CategoryDistributionVO>> categoryDistribution() {
        UserContext.requireAdmin();
        return Result.success(userService.adminCategoryDistribution(UserContext.getRequiredUserId()));
    }
}
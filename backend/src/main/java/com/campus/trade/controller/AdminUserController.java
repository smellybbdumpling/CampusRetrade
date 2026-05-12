package com.campus.trade.controller;

import com.campus.trade.common.PageResult;
import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminUserQueryDTO;
import com.campus.trade.dto.AdminUserStatusUpdateRequest;
import com.campus.trade.dto.UserVO;
import com.campus.trade.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(AdminUserQueryDTO queryDTO) {
        UserContext.requireAdmin();
        return Result.success(userService.adminPageUsers(UserContext.getRequiredUserId(), queryDTO));
    }

    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable Long userId,
                                     @Valid @RequestBody AdminUserStatusUpdateRequest request) {
        UserContext.requireAdmin();
        userService.adminUpdateUserStatus(UserContext.getRequiredUserId(), userId, request);
        return Result.success("用户状态更新成功", null);
    }
}
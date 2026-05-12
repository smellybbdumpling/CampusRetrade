package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.ChangePasswordRequest;
import com.campus.trade.dto.UserProfileUpdateRequest;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserVO> profile() {
        return Result.success(userService.getCurrentUserProfile(UserContext.getRequiredUserId()));
    }

    @GetMapping("/public/{userId}")
    public Result<UserVO> publicProfile(@PathVariable Long userId) {
        return Result.success(userService.getPublicUserProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        userService.updateCurrentUserProfile(UserContext.getRequiredUserId(), request);
        return Result.success("个人信息修改成功", null);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(UserContext.getRequiredUserId(), request);
        return Result.success("密码修改成功", null);
    }
}
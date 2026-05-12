package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.UserNotificationVO;
import com.campus.trade.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    @GetMapping
    public Result<List<UserNotificationVO>> list() {
        return Result.success(userNotificationService.listMyNotifications(UserContext.getRequiredUserId()));
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        return Result.success(userNotificationService.countUnreadNotifications(UserContext.getRequiredUserId()));
    }

    @PutMapping("/{notificationId}/read")
    public Result<Void> markRead(@PathVariable Long notificationId) {
        userNotificationService.markNotificationRead(UserContext.getRequiredUserId(), notificationId);
        return Result.success("通知已读", null);
    }

    @DeleteMapping("/{notificationId}")
    public Result<Void> delete(@PathVariable Long notificationId) {
        userNotificationService.deleteNotification(UserContext.getRequiredUserId(), notificationId);
        return Result.success("通知删除成功", null);
    }

    @PutMapping("/read-all")
    public Result<Void> readAll() {
        userNotificationService.markAllNotificationsRead(UserContext.getRequiredUserId());
        return Result.success("全部通知已读", null);
    }

    @DeleteMapping
    public Result<Void> deleteAll() {
        userNotificationService.deleteAllNotifications(UserContext.getRequiredUserId());
        return Result.success("全部通知已删除", null);
    }
}
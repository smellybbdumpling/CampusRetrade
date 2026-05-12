package com.campus.trade.service;

import com.campus.trade.dto.UserNotificationVO;

import java.util.List;

public interface UserNotificationService {

    List<UserNotificationVO> listMyNotifications(Long userId);

    Long countUnreadNotifications(Long userId);

    void markNotificationRead(Long userId, Long notificationId);

    void deleteNotification(Long userId, Long notificationId);

    void markAllNotificationsRead(Long userId);

    void deleteAllNotifications(Long userId);

    void createNotification(Long userId, String type, String title, String content, String actionUrl);
}
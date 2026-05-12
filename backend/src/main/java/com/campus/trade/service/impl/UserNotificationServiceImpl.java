package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.UserNotificationVO;
import com.campus.trade.entity.UserNotification;
import com.campus.trade.mapper.UserNotificationMapper;
import com.campus.trade.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationMapper userNotificationMapper;

    @Override
    public List<UserNotificationVO> listMyNotifications(Long userId) {
        return userNotificationMapper.selectList(new LambdaQueryWrapper<UserNotification>()
                        .eq(UserNotification::getUserId, userId)
                        .orderByDesc(UserNotification::getCreateTime, UserNotification::getId))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        return userNotificationMapper.selectCount(new LambdaQueryWrapper<UserNotification>()
                .eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getReadFlag, false));
    }

    @Override
    public void markNotificationRead(Long userId, Long notificationId) {
        UserNotification notification = userNotificationMapper.selectById(notificationId);
        if (notification == null || !userId.equals(notification.getUserId())) {
            throw new BusinessException("通知不存在");
        }
        notification.setReadFlag(true);
        userNotificationMapper.updateById(notification);
    }

    @Override
    public void deleteNotification(Long userId, Long notificationId) {
        UserNotification notification = userNotificationMapper.selectById(notificationId);
        if (notification == null || !userId.equals(notification.getUserId())) {
            throw new BusinessException("通知不存在");
        }
        userNotificationMapper.deleteById(notificationId);
    }

    @Override
    @Transactional
    public void markAllNotificationsRead(Long userId) {
        List<UserNotification> notifications = userNotificationMapper.selectList(new LambdaQueryWrapper<UserNotification>()
                .eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getReadFlag, false));
        for (UserNotification notification : notifications) {
            notification.setReadFlag(true);
            userNotificationMapper.updateById(notification);
        }
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        userNotificationMapper.delete(new LambdaQueryWrapper<UserNotification>()
                .eq(UserNotification::getUserId, userId));
    }

    @Override
    public void createNotification(Long userId, String type, String title, String content, String actionUrl) {
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setActionUrl(actionUrl);
        notification.setReadFlag(false);
        userNotificationMapper.insert(notification);
    }

    private UserNotificationVO toVO(UserNotification notification) {
        UserNotificationVO vo = new UserNotificationVO();
        vo.setId(notification.getId());
        vo.setType(notification.getType());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setActionUrl(notification.getActionUrl());
        vo.setReadFlag(notification.getReadFlag());
        vo.setCreateTime(notification.getCreateTime());
        return vo;
    }
}
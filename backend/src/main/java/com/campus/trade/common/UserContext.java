package com.campus.trade.common;

import com.campus.trade.entity.User;

public final class UserContext {

    private static final ThreadLocal<CurrentUser> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setCurrentUser(CurrentUser currentUser) {
        CURRENT_USER.set(currentUser);
    }

    public static CurrentUser getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static Long getRequiredUserId() {
        CurrentUser currentUser = CURRENT_USER.get();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        return currentUser.getUserId();
    }

    public static void requireAdmin() {
        CurrentUser currentUser = CURRENT_USER.get();
        if (currentUser == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new BusinessException("当前用户无管理员权限");
        }
    }

    public static void requireAdmin(User user) {
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException("当前用户无管理员权限");
        }
        if (!"NORMAL".equals(user.getStatus())) {
            throw new BusinessException("当前管理员账号不可用");
        }
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
package com.campus.trade.service;

import com.campus.trade.common.PageResult;
import com.campus.trade.dto.AdminTrendVO;
import com.campus.trade.dto.AdminOverviewVO;
import com.campus.trade.dto.AdminUserQueryDTO;
import com.campus.trade.dto.AdminUserStatusUpdateRequest;
import com.campus.trade.dto.CategoryDistributionVO;
import com.campus.trade.dto.ChangePasswordRequest;
import com.campus.trade.dto.LoginRequest;
import com.campus.trade.dto.RegisterRequest;
import com.campus.trade.dto.UserProfileUpdateRequest;
import com.campus.trade.dto.UserVO;
import com.campus.trade.dto.UserLoginVO;
import com.campus.trade.entity.User;

import java.util.List;

public interface UserService {

    void register(RegisterRequest request);

    UserLoginVO login(LoginRequest request);

    Long getCurrentUserIdByUsername(String username);

    UserVO getCurrentUserProfile(Long userId);

    UserVO getPublicUserProfile(Long userId);

    UserVO buildUserProfileWithCredit(User user);

    void updateCurrentUserProfile(Long userId, UserProfileUpdateRequest request);

    void updateAvatar(Long userId, String avatar);

    void changePassword(Long userId, ChangePasswordRequest request);

    PageResult<UserVO> adminPageUsers(Long adminUserId, AdminUserQueryDTO queryDTO);

    void adminUpdateUserStatus(Long adminUserId, Long targetUserId, AdminUserStatusUpdateRequest request);

    AdminOverviewVO adminOverview(Long adminUserId);

    AdminTrendVO adminTrend(Long adminUserId);

    List<CategoryDistributionVO> adminCategoryDistribution(Long adminUserId);
}
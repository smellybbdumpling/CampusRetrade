package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.common.JwtUtil;
import com.campus.trade.common.PageResult;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminOverviewVO;
import com.campus.trade.dto.AdminTrendVO;
import com.campus.trade.dto.AdminUserQueryDTO;
import com.campus.trade.dto.AdminUserStatusUpdateRequest;
import com.campus.trade.dto.CategoryDistributionVO;
import com.campus.trade.dto.ChangePasswordRequest;
import com.campus.trade.dto.LoginRequest;
import com.campus.trade.dto.RegisterRequest;
import com.campus.trade.dto.TrendPointVO;
import com.campus.trade.dto.UserProfileUpdateRequest;
import com.campus.trade.dto.UserVO;
import com.campus.trade.dto.UserLoginVO;
import com.campus.trade.entity.Category;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.GoodsReport;
import com.campus.trade.entity.Orders;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.CategoryMapper;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.GoodsReportMapper;
import com.campus.trade.mapper.OrdersMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final GoodsMapper goodsMapper;
    private final OrdersMapper ordersMapper;
    private final CategoryMapper categoryMapper;
    private final GoodsReportMapper goodsReportMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        user.setStatus("NORMAL");
        userMapper.insert(user);
    }

    @Override
    public UserLoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .last("limit 1"));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!"NORMAL".equals(user.getStatus())) {
            throw new BusinessException("当前账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new UserLoginVO(user.getId(), user.getUsername(), user.getNickname(), user.getRole(), token);
    }

    @Override
    public Long getCurrentUserIdByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getId();
    }

    @Override
    public UserVO getCurrentUserProfile(Long userId) {
        User user = getRequiredUser(userId);
        return buildUserProfileWithCredit(user);
    }

    @Override
    public UserVO getPublicUserProfile(Long userId) {
        User user = getRequiredUser(userId);
        UserVO vo = buildUserProfileWithCredit(user);
        if (!Boolean.TRUE.equals(user.getPhonePublic())) {
            vo.setPhone(null);
        }
        if (!Boolean.TRUE.equals(user.getWechatPublic())) {
            vo.setWechat(null);
        }
        vo.setRole(null);
        vo.setStatus(null);
        return vo;
    }

    @Override
    public void updateCurrentUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = getRequiredUser(userId);
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setBio(request.getBio());
        user.setSchool(request.getSchool());
        user.setCollege(request.getCollege());
        user.setMajor(request.getMajor());
        user.setGrade(request.getGrade());
        user.setWechat(request.getWechat());
        user.setQq(request.getQq());
        user.setEmail(request.getEmail());
        user.setTradeLocation(request.getTradeLocation());
        user.setTradeMethods(request.getTradeMethods());
        user.setAcceptBargain(Boolean.TRUE.equals(request.getAcceptBargain()));
        user.setOnlineTime(request.getOnlineTime());
        user.setPhonePublic(Boolean.TRUE.equals(request.getPhonePublic()));
        user.setWechatPublic(Boolean.TRUE.equals(request.getWechatPublic()));
        userMapper.updateById(user);
    }

    @Override
    public void updateAvatar(Long userId, String avatar) {
        User user = getRequiredUser(userId);
        user.setAvatar(avatar);
        userMapper.updateById(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getRequiredUser(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public PageResult<UserVO> adminPageUsers(Long adminUserId, AdminUserQueryDTO queryDTO) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);

        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreateTime);
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(User::getUsername, queryDTO.getUsername());
        }
        if (StringUtils.hasText(queryDTO.getRole())) {
            wrapper.eq(User::getRole, queryDTO.getRole());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        Page<User> userPage = userMapper.selectPage(page, wrapper);
        List<UserVO> records = userPage.getRecords().stream().map(this::buildUserProfileWithCredit).toList();
        return new PageResult<>(userPage.getTotal(), records);
    }

    @Override
    public UserVO buildUserProfileWithCredit(User user) {
        UserVO vo = toUserVO(user);
        Long publishedGoodsCount = goodsMapper.selectCount(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getSellerId, user.getId()));
        Long approvedGoodsCount = goodsMapper.selectCount(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getSellerId, user.getId())
                .eq(Goods::getStatus, "ON_SALE"));
        Long finishedBuyOrderCount = ordersMapper.selectCount(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getBuyerId, user.getId())
                .eq(Orders::getStatus, "FINISHED"));
        Long finishedSellOrderCount = ordersMapper.selectCount(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getSellerId, user.getId())
                .eq(Orders::getStatus, "FINISHED"));
        Long totalReportCount = goodsReportMapper.selectCount(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getReportedUserId, user.getId()));
        Long effectiveReportCount = goodsReportMapper.selectCount(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getReportedUserId, user.getId())
                .eq(GoodsReport::getStatus, "RESOLVED")
                .ne(GoodsReport::getHandleResult, "IGNORE"));
        Long penaltyCount = goodsReportMapper.selectCount(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getReportedUserId, user.getId())
                .eq(GoodsReport::getStatus, "RESOLVED")
                .in(GoodsReport::getHandleResult, "OFF_SHELF_GOODS", "DELETE_GOODS", "DISABLE_USER"));

        int creditScore = (int) Math.max(0, Math.min(200,
                100
                        + 5 * finishedSellOrderCount
                        + 3 * finishedBuyOrderCount
                        + approvedGoodsCount
                        - 10 * effectiveReportCount
                        - 20 * penaltyCount));

        vo.setPublishedGoodsCount(publishedGoodsCount);
        vo.setApprovedGoodsCount(approvedGoodsCount);
        vo.setFinishedBuyOrderCount(finishedBuyOrderCount);
        vo.setFinishedSellOrderCount(finishedSellOrderCount);
        vo.setTotalReportCount(totalReportCount);
        vo.setEffectiveReportCount(effectiveReportCount);
        vo.setPenaltyCount(penaltyCount);
        vo.setCreditScore(creditScore);
        vo.setCreditLevel(resolveCreditLevel(creditScore));
        return vo;
    }

    @Override
    public void adminUpdateUserStatus(Long adminUserId, Long targetUserId, AdminUserStatusUpdateRequest request) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);

        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BusinessException("目标用户不存在");
        }
        if (!"NORMAL".equals(request.getStatus()) && !"DISABLED".equals(request.getStatus())) {
            throw new BusinessException("用户状态仅支持 NORMAL 或 DISABLED");
        }
        if (adminUserId.equals(targetUserId) && "DISABLED".equals(request.getStatus())) {
            throw new BusinessException("不能禁用当前登录管理员账号");
        }
        targetUser.setStatus(request.getStatus());
        userMapper.updateById(targetUser);
    }

    @Override
    public AdminOverviewVO adminOverview(Long adminUserId) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>());
        Long goodsCount = goodsMapper.selectCount(new LambdaQueryWrapper<Goods>());
        Long orderCount = ordersMapper.selectCount(new LambdaQueryWrapper<Orders>());
        Long pendingGoodsCount = goodsMapper.selectCount(new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, "PENDING"));
        return new AdminOverviewVO(userCount, goodsCount, orderCount, pendingGoodsCount);
    }

    @Override
    public AdminTrendVO adminTrend(Long adminUserId) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        List<Goods> goodsList = goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                .ge(Goods::getCreateTime, startDate.atStartOfDay())
                .orderByAsc(Goods::getCreateTime));
        List<Orders> orderList = ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                .ge(Orders::getCreateTime, startDate.atStartOfDay())
                .orderByAsc(Orders::getCreateTime));

        return new AdminTrendVO(
                buildTrendPoints(startDate, today, goodsList.stream().map(item -> item.getCreateTime().toLocalDate()).toList()),
                buildTrendPoints(startDate, today, orderList.stream().map(item -> item.getCreateTime().toLocalDate()).toList())
        );
    }

    @Override
    public List<CategoryDistributionVO> adminCategoryDistribution(Long adminUserId) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);

        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSortOrder, Category::getId));
        List<Goods> goodsList = goodsMapper.selectList(new LambdaQueryWrapper<Goods>());
        Map<Long, Long> categoryCountMap = new LinkedHashMap<>();
        for (Goods goods : goodsList) {
            categoryCountMap.merge(goods.getCategoryId(), 1L, Long::sum);
        }

        List<CategoryDistributionVO> result = new ArrayList<>();
        for (Category category : categories) {
            result.add(new CategoryDistributionVO(
                    category.getId(),
                    category.getName(),
                    categoryCountMap.getOrDefault(category.getId(), 0L)
            ));
        }
        return result;
    }

    private User getRequiredUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setGender(user.getGender());
        vo.setBio(user.getBio());
        vo.setSchool(user.getSchool());
        vo.setCollege(user.getCollege());
        vo.setMajor(user.getMajor());
        vo.setGrade(user.getGrade());
        vo.setWechat(user.getWechat());
        vo.setQq(user.getQq());
        vo.setEmail(user.getEmail());
        vo.setTradeLocation(user.getTradeLocation());
        vo.setTradeMethods(user.getTradeMethods());
        vo.setAcceptBargain(Boolean.TRUE.equals(user.getAcceptBargain()));
        vo.setOnlineTime(user.getOnlineTime());
        vo.setPhonePublic(Boolean.TRUE.equals(user.getPhonePublic()));
        vo.setWechatPublic(Boolean.TRUE.equals(user.getWechatPublic()));
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    private String resolveCreditLevel(Integer creditScore) {
        if (creditScore >= 180) {
            return "优秀";
        }
        if (creditScore >= 140) {
            return "良好";
        }
        if (creditScore >= 100) {
            return "正常";
        }
        if (creditScore >= 60) {
            return "需关注";
        }
        return "风险较高";
    }

    private List<TrendPointVO> buildTrendPoints(LocalDate startDate, LocalDate endDate, List<LocalDate> dates) {
        Map<LocalDate, Long> countMap = new LinkedHashMap<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            countMap.put(cursor, 0L);
            cursor = cursor.plusDays(1);
        }
        for (LocalDate date : dates) {
            if (countMap.containsKey(date)) {
                countMap.put(date, countMap.get(date) + 1);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        return countMap.entrySet().stream()
                .map(entry -> new TrendPointVO(entry.getKey().format(formatter), entry.getValue()))
                .toList();
    }
}

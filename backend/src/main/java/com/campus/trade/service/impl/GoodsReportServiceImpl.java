package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.common.PageResult;
import com.campus.trade.common.UserContext;
import com.campus.trade.dto.AdminGoodsReportQueryDTO;
import com.campus.trade.dto.AdminHandleGoodsReportRequest;
import com.campus.trade.dto.GoodsReportSaveRequest;
import com.campus.trade.dto.GoodsReportVO;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.GoodsReport;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.GoodsReportMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.GoodsReportService;
import com.campus.trade.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsReportServiceImpl implements GoodsReportService {

    private final GoodsReportMapper goodsReportMapper;
    private final GoodsMapper goodsMapper;
    private final UserMapper userMapper;
    private final UserNotificationService userNotificationService;

    @Override
    @Transactional
    public void submitReport(Long userId, Long goodsId, GoodsReportSaveRequest request) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        if (userId.equals(goods.getSellerId())) {
            throw new BusinessException("不能举报自己发布的商品");
        }
        Long count = goodsReportMapper.selectCount(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getGoodsId, goodsId)
                .eq(GoodsReport::getReporterId, userId)
                .eq(GoodsReport::getStatus, "PENDING"));
        if (count != null && count > 0) {
            throw new BusinessException("你已提交过该商品的待处理举报");
        }
        GoodsReport report = new GoodsReport();
        report.setGoodsId(goodsId);
        report.setReporterId(userId);
        report.setReportedUserId(goods.getSellerId());
        report.setReportType(request.getReportType());
        report.setReportReason(request.getReportReason());
        report.setReportDescription(request.getReportDescription());
        report.setStatus("PENDING");
        goodsReportMapper.insert(report);

        userNotificationService.createNotification(
            goods.getSellerId(),
            "GOODS_REPORT",
            "商品被举报",
            "商品《" + goods.getTitle() + "》收到了一条新的举报，请注意平台后续处理结果。",
            "/report-detail/" + report.getId()
        );
    }

    @Override
    public PageResult<GoodsReportVO> adminPageReports(Long adminUserId, AdminGoodsReportQueryDTO queryDTO) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        Page<GoodsReport> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<GoodsReport> wrapper = new LambdaQueryWrapper<GoodsReport>()
                .orderByDesc(GoodsReport::getCreateTime, GoodsReport::getId);
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(GoodsReport::getStatus, queryDTO.getStatus());
        }
        Page<GoodsReport> result = goodsReportMapper.selectPage(page, wrapper);
        List<GoodsReportVO> records = result.getRecords().stream()
                .map(this::toVO)
                .filter(item -> !StringUtils.hasText(queryDTO.getKeyword())
                        || (item.getGoodsTitle() != null && item.getGoodsTitle().contains(queryDTO.getKeyword()))
                        || (item.getReportReason() != null && item.getReportReason().contains(queryDTO.getKeyword()))
                        || (item.getReportDescription() != null && item.getReportDescription().contains(queryDTO.getKeyword())))
                .toList();
        return new PageResult<>(result.getTotal(), records);
    }

    @Override
    public GoodsReportVO getReportDetail(Long userId, Long reportId) {
        GoodsReport report = goodsReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报不存在");
        }
        if (!userId.equals(report.getReporterId()) && !userId.equals(report.getReportedUserId())) {
            throw new BusinessException("无权查看该举报详情");
        }
        GoodsReportVO vo = toVO(report);
        vo.setReporterId(null);
        vo.setReporterName(null);
        return vo;
    }

    @Override
    @Transactional
    public void handleReport(Long adminUserId, Long reportId, AdminHandleGoodsReportRequest request) {
        User admin = userMapper.selectById(adminUserId);
        UserContext.requireAdmin(admin);
        GoodsReport report = goodsReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报不存在");
        }
        if (!"PENDING".equals(report.getStatus())) {
            throw new BusinessException("该举报已处理");
        }
        Goods goods = goodsMapper.selectById(report.getGoodsId());
        report.setStatus("RESOLVED");
        report.setHandleResult(request.getHandleResult());
        report.setHandleRemark(request.getHandleRemark());
        report.setHandlerId(adminUserId);
        report.setHandleTime(LocalDateTime.now());

        switch (request.getHandleResult()) {
            case "IGNORE" -> report.setStatus("REJECTED");
            case "OFF_SHELF_GOODS" -> {
                if (goods != null) {
                    goods.setStatus("OFF_SHELF");
                    goodsMapper.updateById(goods);
                }
            }
            case "DELETE_GOODS" -> {
                if (goods != null) {
                    goodsMapper.deleteById(goods.getId());
                }
            }
            case "DISABLE_USER" -> {
                User reportedUser = userMapper.selectById(report.getReportedUserId());
                if (reportedUser != null) {
                    reportedUser.setStatus("DISABLED");
                    userMapper.updateById(reportedUser);
                }
            }
            default -> throw new BusinessException("不支持的处理结果");
        }
        goodsReportMapper.updateById(report);

            if ("REJECTED".equals(report.getStatus())) {
                userNotificationService.createNotification(
                    report.getReportedUserId(),
                    "GOODS_REPORT_RESULT",
                    "商品举报已驳回",
                    "商品《" + (goods == null ? ("商品" + report.getGoodsId()) : goods.getTitle()) + "》的举报已被驳回。",
                    "/report-detail/" + report.getId()
                );
            } else {
                userNotificationService.createNotification(
                    report.getReportedUserId(),
                    "GOODS_REPORT_RESULT",
                    "商品举报处理完成",
                    "平台已对商品《" + (goods == null ? ("商品" + report.getGoodsId()) : goods.getTitle()) + "》执行处理结果：" + request.getHandleResult(),
                    "/report-detail/" + report.getId()
                );
                userNotificationService.createNotification(
                    report.getReporterId(),
                    "GOODS_REPORT_RESULT",
                    "举报处理完成",
                    "商品《" + (goods == null ? ("商品" + report.getGoodsId()) : goods.getTitle()) + "》的举报已处理，结果：" + request.getHandleResult(),
                    "/report-detail/" + report.getId()
                );
            }
    }

    @Override
    public Long countTotalReportsByUserId(Long userId) {
        return goodsReportMapper.selectCount(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getReportedUserId, userId));
    }

    @Override
    public Long countEffectiveReportsByUserId(Long userId) {
        return goodsReportMapper.selectCount(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getReportedUserId, userId)
                .ne(GoodsReport::getHandleResult, "IGNORE")
                .eq(GoodsReport::getStatus, "RESOLVED"));
    }

    @Override
    public Long countPenaltyByUserId(Long userId) {
        return goodsReportMapper.selectCount(new LambdaQueryWrapper<GoodsReport>()
                .eq(GoodsReport::getReportedUserId, userId)
                .in(GoodsReport::getHandleResult, "OFF_SHELF_GOODS", "DELETE_GOODS", "DISABLE_USER")
                .eq(GoodsReport::getStatus, "RESOLVED"));
    }

    private GoodsReportVO toVO(GoodsReport report) {
        GoodsReportVO vo = new GoodsReportVO();
        vo.setId(report.getId());
        vo.setGoodsId(report.getGoodsId());
        Goods goods = goodsMapper.selectById(report.getGoodsId());
        if (goods != null) {
            vo.setGoodsTitle(goods.getTitle());
        }
        vo.setReporterId(report.getReporterId());
        User reporter = userMapper.selectById(report.getReporterId());
        if (reporter != null) {
            vo.setReporterName(reporter.getNickname());
        }
        vo.setReportedUserId(report.getReportedUserId());
        User reportedUser = userMapper.selectById(report.getReportedUserId());
        if (reportedUser != null) {
            vo.setReportedUserName(reportedUser.getNickname());
        }
        vo.setReportType(report.getReportType());
        vo.setReportReason(report.getReportReason());
        vo.setReportDescription(report.getReportDescription());
        vo.setStatus(report.getStatus());
        vo.setHandleResult(report.getHandleResult());
        vo.setHandleRemark(report.getHandleRemark());
        vo.setHandlerId(report.getHandlerId());
        if (report.getHandlerId() != null) {
            User handler = userMapper.selectById(report.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getNickname());
            }
        }
        vo.setHandleTime(report.getHandleTime());
        vo.setCreateTime(report.getCreateTime());
        return vo;
    }
}

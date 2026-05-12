package com.campus.trade.service;

import com.campus.trade.common.PageResult;
import com.campus.trade.dto.AdminGoodsReportQueryDTO;
import com.campus.trade.dto.AdminHandleGoodsReportRequest;
import com.campus.trade.dto.GoodsReportSaveRequest;
import com.campus.trade.dto.GoodsReportVO;

public interface GoodsReportService {

    void submitReport(Long userId, Long goodsId, GoodsReportSaveRequest request);

    PageResult<GoodsReportVO> adminPageReports(Long adminUserId, AdminGoodsReportQueryDTO queryDTO);

    GoodsReportVO getReportDetail(Long userId, Long reportId);

    void handleReport(Long adminUserId, Long reportId, AdminHandleGoodsReportRequest request);

    Long countTotalReportsByUserId(Long userId);

    Long countEffectiveReportsByUserId(Long userId);

    Long countPenaltyByUserId(Long userId);
}
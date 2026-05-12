package com.campus.trade.service;

import com.campus.trade.IntegrationTestSupport;
import com.campus.trade.dto.AdminHandleGoodsReportRequest;
import com.campus.trade.dto.GoodsReportSaveRequest;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.GoodsReport;
import com.campus.trade.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class GoodsReportServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private GoodsReportService goodsReportService;

    @Test
    void handleReportShouldOffShelfGoodsAndResolveReport() {
        GoodsReportSaveRequest saveRequest = new GoodsReportSaveRequest();
        saveRequest.setReportType("FAKE_GOODS");
        saveRequest.setReportReason("虚假描述");
        saveRequest.setReportDescription("描述与实际不符");
        goodsReportService.submitReport(buyerUser.getId(), onSaleGoods.getId(), saveRequest);

        GoodsReport report = getLatestReport(buyerUser.getId());

        AdminHandleGoodsReportRequest handleRequest = new AdminHandleGoodsReportRequest();
        handleRequest.setHandleResult("OFF_SHELF_GOODS");
        handleRequest.setHandleRemark("举报属实");
        goodsReportService.handleReport(adminUser.getId(), report.getId(), handleRequest);

        GoodsReport updatedReport = goodsReportMapper.selectById(report.getId());
        Goods updatedGoods = goodsMapper.selectById(onSaleGoods.getId());
        User updatedSeller = userMapper.selectById(sellerUser.getId());

        assertThat(updatedReport.getStatus()).isEqualTo("RESOLVED");
        assertThat(updatedReport.getHandleResult()).isEqualTo("OFF_SHELF_GOODS");
        assertThat(updatedGoods.getStatus()).isEqualTo("OFF_SHELF");
        assertThat(updatedSeller.getStatus()).isEqualTo("NORMAL");
    }
}
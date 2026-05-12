package com.campus.trade.controller;

import com.campus.trade.entity.GoodsReport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminGoodsReportControllerTest extends ControllerTestSupport {

    @Test
    void adminReportsPageShouldRejectNonAdminUser() throws Exception {
        mockMvc.perform(get("/api/admin/goods-reports/page")
                        .header("Authorization", bearerTokenForBuyer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("当前用户无管理员权限"));
    }

    @Test
    void handleReportShouldFailWhenHandleResultMissing() throws Exception {
        GoodsReport report = new GoodsReport();
        report.setGoodsId(onSaleGoods.getId());
        report.setReporterId(buyerUser.getId());
        report.setReportedUserId(sellerUser.getId());
        report.setReportType("FAKE_GOODS");
        report.setReportReason("原因");
        report.setStatus("PENDING");
        report.setDeleted(0);
        goodsReportMapper.insert(report);

        mockMvc.perform(put("/api/admin/goods-reports/{reportId}/handle", report.getId())
                        .header("Authorization", bearerTokenForAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"handleResult\": \"\",
                                  \"handleRemark\": \"备注\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("处理结果不能为空"));
    }
}
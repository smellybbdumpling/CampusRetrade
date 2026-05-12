package com.campus.trade.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GoodsReportControllerTest extends ControllerTestSupport {

    @Test
    void submitReportShouldRequireLogin() throws Exception {
        mockMvc.perform(post("/api/goods/{goodsId}/report", onSaleGoods.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"reportType\": \"FAKE_GOODS\",
                                  \"reportReason\": \"原因\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("未登录或登录已过期"));
    }

    @Test
    void submitReportShouldFailWhenReportTypeMissing() throws Exception {
        mockMvc.perform(post("/api/goods/{goodsId}/report", onSaleGoods.getId())
                        .header("Authorization", bearerTokenForBuyer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"reportType\": \"\",
                                  \"reportReason\": \"原因\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("举报类型不能为空"));
    }
}
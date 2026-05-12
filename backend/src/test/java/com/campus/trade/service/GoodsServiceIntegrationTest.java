package com.campus.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.IntegrationTestSupport;
import com.campus.trade.dto.AdminAuditGoodsRequest;
import com.campus.trade.entity.Goods;
import com.campus.trade.entity.GoodsAuditLog;
import com.campus.trade.mapper.GoodsAuditLogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class GoodsServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsAuditLogMapper goodsAuditLogMapper;

    @Test
    void adminAuditShouldApprovePendingGoodsAndCreateAuditLog() {
        AdminAuditGoodsRequest request = new AdminAuditGoodsRequest();
        request.setApproved(true);
        request.setAuditRemark("审核通过");

        goodsService.adminAuditGoods(adminUser.getId(), pendingGoods.getId(), request);

        Goods updatedGoods = goodsMapper.selectById(pendingGoods.getId());
        GoodsAuditLog auditLog = goodsAuditLogMapper.selectOne(new LambdaQueryWrapper<GoodsAuditLog>()
                .eq(GoodsAuditLog::getGoodsId, pendingGoods.getId())
                .orderByDesc(GoodsAuditLog::getId)
                .last("limit 1"));

        assertThat(updatedGoods.getStatus()).isEqualTo("ON_SALE");
        assertThat(auditLog).isNotNull();
        assertThat(auditLog.getAuditStatus()).isEqualTo("ON_SALE");
        assertThat(auditLog.getAuditRemark()).isEqualTo("审核通过");
    }
}
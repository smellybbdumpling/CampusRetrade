<template>
  <MainLayout>
    <section class="container page-block" v-if="detail.id">
      <div class="card detail-card">
        <h2 class="section-title">举报详情</h2>
        <div class="detail-grid">
          <div><strong>被举报商品：</strong>{{ detail.goodsTitle || `商品${detail.goodsId}` }}</div>
          <div><strong>举报类型：</strong>{{ formatReportType(detail.reportType) }}</div>
          <div><strong>举报原因：</strong>{{ detail.reportReason }}</div>
          <div><strong>举报状态：</strong>{{ formatStatus(detail.status) }}</div>
          <div><strong>处理结果：</strong>{{ formatHandleResult(detail.handleResult) }}</div>
          <div><strong>处理备注：</strong>{{ detail.handleRemark || '无' }}</div>
        </div>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import { getReportDetail } from '../api/goods'

const route = useRoute()
const detail = ref({})

function formatReportType(type) {
  const map = {
    FALSE_INFO: '虚假描述',
    PRICE_ISSUE: '价格异常',
    PROHIBITED_GOODS: '违禁商品',
    SPAM: '重复发布/引流',
    ABUSE: '不文明内容',
    OTHER: '其他'
  }
  return map[type] || type || '-'
}

function formatStatus(status) {
  const map = {
    PENDING: '待处理',
    RESOLVED: '已处理',
    REJECTED: '已驳回'
  }
  return map[status] || status || '-'
}

function formatHandleResult(result) {
  const map = {
    IGNORE: '忽略举报',
    OFF_SHELF_GOODS: '下架商品',
    DELETE_GOODS: '删除商品',
    DISABLE_USER: '禁用用户'
  }
  if (!result) {
    return '待处理'
  }
  return map[result] || result
}

async function fetchDetail() {
  detail.value = await getReportDetail(route.params.id)
}

onMounted(fetchDetail)
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.detail-card {
  padding: 24px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 18px;
  color: var(--muted);
}

@media (max-width: 760px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
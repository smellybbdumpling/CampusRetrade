<template>
  <MainLayout>
    <section class="container page-block">
      <div class="card panel-card">
        <div class="page-head">
          <div>
            <h2 class="section-title">举报管理</h2>
            <p class="section-desc">查看用户提交的商品举报，并进行处理。</p>
          </div>
        </div>

        <div class="filter-bar">
          <input v-model="query.keyword" class="input" placeholder="按商品标题、举报原因搜索" />
          <select v-model="query.status" class="select">
            <option value="">全部状态</option>
            <option value="PENDING">待处理</option>
            <option value="RESOLVED">已处理</option>
            <option value="REJECTED">已驳回</option>
          </select>
          <button class="btn btn-accent" @click="applyFilter">筛选</button>
        </div>

        <div class="table-shell" v-if="reportPage.records?.length">
          <table class="data-table">
            <thead>
              <tr>
                <th>商品</th>
                <th>举报人</th>
                <th>卖家</th>
                <th>举报类型</th>
                <th>举报原因</th>
                <th>状态</th>
                <th>处理时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in reportPage.records" :key="item.id">
                <td>{{ item.goodsTitle || `商品${item.goodsId}` }}</td>
                <td>{{ item.reporterName }}</td>
                <td>{{ item.reportedUserName }}</td>
                <td>{{ formatReportType(item.reportType) }}</td>
                <td>{{ item.reportReason }}</td>
                <td>{{ formatStatus(item.status) }}</td>
                <td>
                  <div class="handle-time">{{ formatDateTime(item.handleTime).date }}</div>
                  <div class="handle-time">{{ formatDateTime(item.handleTime).time }}</div>
                </td>
                <td>
                  <button class="btn" @click="openHandle(item)">处理</button>
                </td>
              </tr>
            </tbody>
          </table>
          <PaginationBar :total="reportPage.total || 0" :page-num="page.pageNum" :page-size="page.pageSize" @change="changePage" />
        </div>

        <EmptyState v-else icon="⚑" title="暂无举报记录" description="当前筛选条件下没有举报数据。" />
      </div>

      <BaseModal v-model="handleVisible" title="处理举报">
        <div class="form-grid" v-if="activeReport">
          <div class="report-box">
            <strong>商品：</strong>{{ activeReport.goodsTitle || `商品${activeReport.goodsId}` }}
          </div>
          <div class="report-box">
            <strong>举报原因：</strong>{{ activeReport.reportReason }}
          </div>
          <div class="report-box">
            <strong>举报类型：</strong>{{ formatReportType(activeReport.reportType) }}
          </div>
          <div class="report-box">
            <strong>当前状态：</strong>{{ formatStatus(activeReport.status) }}
          </div>
          <div class="report-box">
            <strong>补充说明：</strong>{{ activeReport.reportDescription || '无' }}
          </div>
          <select v-model="handleForm.handleResult" class="select">
            <option value="IGNORE">忽略举报</option>
            <option value="OFF_SHELF_GOODS">下架商品</option>
            <option value="DELETE_GOODS">删除商品</option>
            <option value="DISABLE_USER">禁用用户</option>
          </select>
          <textarea v-model="handleForm.handleRemark" class="textarea" rows="3" placeholder="处理备注"></textarea>
          <button class="btn btn-accent" @click="submitHandle">提交处理</button>
        </div>
      </BaseModal>
    </section>
  </MainLayout>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import BaseModal from '../components/BaseModal.vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { getGoodsReportPage, handleGoodsReport } from '../api/admin'
import { useUiStore } from '../stores/ui'

const query = reactive({ keyword: '', status: '' })
const page = reactive({ pageNum: 1, pageSize: 10 })
const reportPage = ref({ total: 0, records: [] })
const handleVisible = ref(false)
const activeReport = ref(null)
const handleForm = reactive({ handleResult: 'IGNORE', handleRemark: '' })
const uiStore = useUiStore()

function formatStatus(status) {
  const map = {
    PENDING: '待处理',
    RESOLVED: '已处理',
    REJECTED: '已驳回'
  }
  return map[status] || status || '-'
}

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

function formatDateTime(value) {
  if (!value) {
    return { date: '-', time: '-' }
  }
  const normalized = String(value).replace('T', ' ')
  const [date = '-', time = '-'] = normalized.split(' ')
  return { date, time }
}

async function fetchReports() {
  reportPage.value = await getGoodsReportPage({
    pageNum: page.pageNum,
    pageSize: page.pageSize,
    keyword: query.keyword || undefined,
    status: query.status || undefined
  })
}

function applyFilter() {
  page.pageNum = 1
  fetchReports()
}

function changePage(nextPage) {
  page.pageNum = nextPage
  fetchReports()
}

function openHandle(report) {
  activeReport.value = report
  handleVisible.value = true
  handleForm.handleResult = 'IGNORE'
  handleForm.handleRemark = ''
}

async function submitHandle() {
  await handleGoodsReport(activeReport.value.id, {
    handleResult: handleForm.handleResult,
    handleRemark: handleForm.handleRemark
  })
  handleVisible.value = false
  uiStore.showToast('举报处理成功', 'success')
  await fetchReports()
}

onMounted(fetchReports)
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.panel-card {
  padding: 20px;
}

.page-head {
  margin-bottom: 16px;
}

.filter-bar {
  display: grid;
  grid-template-columns: 1fr 200px auto;
  gap: 10px;
  margin-bottom: 18px;
}

.table-shell {
  overflow: auto;
}

.data-table {
  width: 100%;
  min-width: 840px;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 10px;
  border-bottom: 1px solid rgba(55, 35, 20, 0.08);
  text-align: left;
  vertical-align: top;
}

.data-table th {
  color: var(--muted);
  font-weight: 600;
}

.form-grid {
  display: grid;
  gap: 12px;
}

.report-box {
  color: var(--muted);
}

.handle-time {
  color: var(--muted);
  line-height: 1.4;
}

@media (max-width: 760px) {
  .filter-bar {
    grid-template-columns: 1fr;
  }
}
</style>
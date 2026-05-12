<template>
  <MainLayout>
    <section class="container page-block">
      <div class="stats-grid">
        <article class="card stat-card" v-for="item in statCards" :key="item.label">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </article>
      </div>

      <div class="admin-grid">
        <div class="card chart-card">
          <h3>近7天趋势</h3>
          <div ref="trendRef" class="chart-box"></div>
        </div>
        <div class="card chart-card">
          <h3>分类分布</h3>
          <div ref="categoryRef" class="chart-box"></div>
        </div>
      </div>

      <div class="management-stack">
        <router-link to="/admin/audit" class="card management-card">
          <div>
            <span class="status-tag">商品审核</span>
            <h3>处理商品上架审核</h3>
            <p>查看待审核商品，执行通过或驳回，并为通过商品配置特色分类。</p>
          </div>
          <strong>进入审核</strong>
        </router-link>

        <router-link to="/admin/goods" class="card management-card">
          <div>
            <span class="status-tag">商品管理</span>
            <h3>管理平台商品</h3>
            <p>查看商品列表、浏览详情、编辑信息，并执行上架、下架或删除操作。</p>
          </div>
          <strong>进入管理</strong>
        </router-link>

        <router-link to="/admin/users" class="card management-card">
          <div>
            <span class="status-tag">用户管理</span>
            <h3>维护用户账号状态</h3>
            <p>按用户名或状态筛选用户，并对异常账号执行禁用或启用操作。</p>
          </div>
          <strong>进入管理</strong>
        </router-link>

        <router-link to="/admin/messages" class="card management-card">
          <div>
            <span class="status-tag">留言管理</span>
            <h3>管理商品留言沟通</h3>
            <p>查看买卖家留言与回复内容，对不良、恶意言语执行删除处理。</p>
          </div>
          <strong>进入管理</strong>
        </router-link>

        <router-link to="/admin/reports" class="card management-card">
          <div>
            <span class="status-tag">举报管理</span>
            <h3>处理用户举报商品</h3>
            <p>查看举报记录，核实问题，并对商品或用户执行治理动作。</p>
          </div>
          <strong>进入管理</strong>
        </router-link>

        <router-link to="/admin/featured-categories" class="card management-card">
          <div>
            <span class="status-tag">特色分类管理</span>
            <h3>维护首页特色专区</h3>
            <p>新增、编辑和启停特色分类，用于组织首页推荐专区和专题展示。</p>
          </div>
          <strong>进入管理</strong>
        </router-link>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import MainLayout from '../layouts/MainLayout.vue'
import { getCategoryDistribution, getOverview, getTrend } from '../api/admin'

const overview = ref({ userCount: 0, goodsCount: 0, orderCount: 0, pendingGoodsCount: 0 })
const trend = ref({ goodsTrend: [], orderTrend: [] })
const categories = ref([])
const trendRef = ref(null)
const categoryRef = ref(null)
let trendChart = null
let categoryChart = null

const statCards = computed(() => [
  { label: '用户总数', value: overview.value.userCount },
  { label: '商品总数', value: overview.value.goodsCount },
  { label: '订单总数', value: overview.value.orderCount },
  { label: '待审核商品', value: overview.value.pendingGoodsCount }
])

async function fetchDashboard() {
  overview.value = await getOverview()
  trend.value = await getTrend()
  categories.value = await getCategoryDistribution()
  await nextTick()
  renderCharts()
}

function renderCharts() {
  if (trendRef.value) {
    if (!trendChart) {
      trendChart = echarts.init(trendRef.value)
    }
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['商品新增', '订单新增'] },
      xAxis: { type: 'category', data: trend.value.goodsTrend.map((item) => item.date) },
      yAxis: { type: 'value' },
      series: [
        { name: '商品新增', type: 'line', smooth: true, data: trend.value.goodsTrend.map((item) => item.count) },
        { name: '订单新增', type: 'line', smooth: true, data: trend.value.orderTrend.map((item) => item.count) }
      ]
    })
  }

  if (categoryRef.value) {
    if (!categoryChart) {
      categoryChart = echarts.init(categoryRef.value)
    }
    categoryChart.setOption({
      tooltip: { trigger: 'item' },
      series: [
        {
          type: 'pie',
          radius: ['40%', '72%'],
          data: categories.value.map((item) => ({ name: item.categoryName, value: item.goodsCount }))
        }
      ]
    })
  }
}

onMounted(fetchDashboard)

onBeforeUnmount(() => {
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
  if (categoryChart) {
    categoryChart.dispose()
    categoryChart = null
  }
})
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  padding: 20px;
}

.stat-card span {
  color: var(--muted);
}

.stat-card strong {
  display: block;
  margin-top: 10px;
  font-size: 34px;
}

.admin-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-bottom: 18px;
}

.chart-card {
  padding: 20px;
}

.chart-box {
  height: 320px;
}

.management-stack {
  display: grid;
  gap: 14px;
  margin-top: 18px;
}

.management-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 20px;
}

.management-card h3 {
  margin: 12px 0 8px;
}

.management-card p {
  margin: 0;
  color: var(--muted);
}

.management-card > strong {
  white-space: nowrap;
  color: var(--accent-deep);
}

@media (max-width: 960px) {
  .stats-grid,
  .admin-grid {
    grid-template-columns: 1fr;
  }

  .management-card {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
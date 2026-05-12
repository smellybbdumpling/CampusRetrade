<template>
  <MainLayout>
    <section class="container page-block">
      <div class="card panel-card">
        <div class="page-head">
          <div>
            <h2 class="section-title">商品审核</h2>
            <p class="section-desc">审核待上架商品，并为通过审核的商品分配特色分类。</p>
          </div>
        </div>

        <div class="filter-bar">
          <input v-model="goodsFilter.keyword" class="input" placeholder="按商品标题搜索" />
          <select v-model="goodsFilter.status" class="select">
            <option value="PENDING">待审核</option>
            <option value="REJECTED">已驳回</option>
            <option value="ON_SALE">在售</option>
          </select>
          <button class="btn" @click="applyGoodsFilter">筛选商品</button>
        </div>

        <div class="table-shell" v-if="goodsList.records?.length">
          <table class="data-table">
            <thead>
              <tr>
                <th>商品ID</th>
                <th>标题</th>
                <th>分类</th>
                <th>状态</th>
                <th>审核备注</th>
                <th>特色分类</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in goodsList.records" :key="item.id">
                <td>{{ item.id }}</td>
                <td>{{ item.title }}</td>
                <td>{{ item.categoryName }}</td>
                  <td>{{ formatStatus(item.status) }}</td>
                <td>{{ item.latestAuditRemark || '-' }}</td>
                <td>
                  <div class="tag-stack">
                    <label v-for="option in featuredOptions" :key="`${item.id}-${option.id}`" class="tag-option">
                      <input v-model="selectedFeaturedMap[item.id]" type="checkbox" :value="option.id" />
                      <span>{{ option.name }}</span>
                    </label>
                  </div>
                </td>
                <td>
                  <div class="action-stack">
                    <button class="btn btn-accent" @click="audit(item.id, true)">通过</button>
                    <button class="btn" @click="audit(item.id, false)">驳回</button>
                    <button v-if="item.status === 'ON_SALE'" class="btn" @click="saveFeatured(item.id)">保存特色</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          <PaginationBar :total="goodsList.total || 0" :page-num="goodsPage.pageNum" :page-size="goodsPage.pageSize" @change="changeGoodsPage" />
        </div>
        <EmptyState v-if="!goodsList.records?.length" icon="△" title="暂无待审核商品" description="当前筛选条件下没有待审核商品。" />
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { assignFeaturedCategories, auditGoods, getAdminGoodsPage, getFeaturedSections } from '../api/goods'
import { useUiStore } from '../stores/ui'

const goodsList = ref({ records: [] })
const featuredOptions = ref([])
const selectedFeaturedMap = ref({})
const goodsFilter = ref({ keyword: '', status: 'PENDING' })
const goodsPage = ref({ pageNum: 1, pageSize: 8 })
const uiStore = useUiStore()

function formatStatus(status) {
  const map = {
    PENDING: '待审核',
    REJECTED: '已驳回',
    ON_SALE: '在售',
    OFF_SHELF: '已下架',
    SOLD: '已售出'
  }
  return map[status] || status || '-'
}

async function fetchGoodsAudit() {
  featuredOptions.value = (await getFeaturedSections()).map((item) => ({ id: item.id, name: item.name }))
  goodsList.value = await getAdminGoodsPage({
    pageNum: goodsPage.value.pageNum,
    pageSize: goodsPage.value.pageSize,
    status: goodsFilter.value.status || undefined,
    keyword: goodsFilter.value.keyword || undefined
  })
  const selectionMap = {}
  for (const item of goodsList.value.records || []) {
    selectionMap[item.id] = (item.featuredCategories || []).map((featured) => featured.id)
  }
  selectedFeaturedMap.value = selectionMap
}

async function audit(id, approved) {
  await auditGoods(id, {
    approved,
    auditRemark: approved ? '前端审核通过' : '前端审核驳回',
    featuredCategoryIds: selectedFeaturedMap.value[id] || []
  })
  uiStore.showToast(`商品已${approved ? '通过' : '驳回'}审核`, 'success')
  await fetchGoodsAudit()
}

async function saveFeatured(id) {
  await assignFeaturedCategories(id, selectedFeaturedMap.value[id] || [])
  uiStore.showToast('在售商品特色分类已更新', 'success')
  await fetchGoodsAudit()
}

function applyGoodsFilter() {
  goodsPage.value.pageNum = 1
  fetchGoodsAudit()
}

function changeGoodsPage(page) {
  goodsPage.value.pageNum = page
  fetchGoodsAudit()
}

onMounted(fetchGoodsAudit)
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.panel-card {
  padding: 20px;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.filter-bar {
  display: grid;
  grid-template-columns: 1fr 180px auto;
  gap: 10px;
  margin: 16px 0;
}

.table-shell {
  overflow: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 760px;
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

.action-stack {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-stack {
  display: grid;
  gap: 6px;
  min-width: 150px;
}

.tag-option {
  display: flex;
  gap: 6px;
  align-items: center;
  font-size: 13px;
}

@media (max-width: 960px) {
  .filter-bar {
    grid-template-columns: 1fr;
  }
}
</style>

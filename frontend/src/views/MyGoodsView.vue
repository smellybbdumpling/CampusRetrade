<template>
  <MainLayout>
    <section class="container page-block">
      <div class="page-head">
        <h2 class="section-title">我的商品</h2>
        <p class="section-desc">你发布的商品、审核状态和最近审核备注都会在这里展示。</p>
      </div>
      <div class="toolbar card">
        <input v-model="filters.keyword" class="input" placeholder="按商品标题搜索" />
        <select v-model="filters.status" class="select">
          <option value="">全部状态</option>
          <option value="PENDING">待审核</option>
          <option value="ON_SALE">在售</option>
          <option value="OFF_SHELF">已下架</option>
          <option value="REJECTED">已驳回</option>
          <option value="SOLD">已售出</option>
        </select>
        <button class="btn btn-accent" @click="applyFilters">筛选</button>
        <button class="btn" @click="resetFilters">重置</button>
      </div>
      <div class="list-grid">
        <article v-for="item in pagedList" :key="item.id" class="card list-card">
          <img :src="buildImageUrl(item.coverImage, item.title || 'Goods')" :alt="item.title" @error="(event) => handleImageError(event, item.title || 'Goods')" />
          <div class="content">
            <div class="title-row">
              <h3>{{ item.title }}</h3>
              <span class="status-tag">{{ formatGoodsStatus(item.status) }}</span>
            </div>
            <p>{{ item.description }}</p>
            <div class="meta">￥{{ item.price }} / 库存 {{ item.stock }}</div>
            <div v-if="item.latestAuditRemark" class="remark">最近审核备注：{{ item.latestAuditRemark }}</div>
            <div class="actions">
              <button class="btn btn-accent" @click="openEdit(item)">编辑</button>
              <button class="btn" @click="toggleStatus(item)">{{ item.status === 'OFF_SHELF' ? '重新上架' : '下架' }}</button>
            </div>
          </div>
        </article>
      </div>
      <EmptyState v-if="!list.length" icon="▣" title="还没有发布商品" description="先去发布一个商品，再回来管理审核和上下架状态。" />
      <PaginationBar
        v-if="filteredList.length"
        :total="filteredList.length"
        :page-num="pageNum"
        :page-size="pageSize"
        @change="pageNum = $event"
      />
    </section>

    <BaseModal v-model="editingVisible" title="编辑商品">
      <div class="form-grid">
        <select v-model="editForm.categoryId" class="select">
          <option value="">选择分类</option>
          <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
        </select>
        <input v-model="editForm.title" class="input" placeholder="商品标题" />
        <input v-model="editForm.price" class="input" type="number" min="0.01" step="0.01" placeholder="价格" />
        <input v-model="editForm.stock" class="input" type="number" min="1" step="1" placeholder="库存" />
        <textarea v-model="editForm.description" class="textarea" rows="4" placeholder="商品描述"></textarea>
        <input type="file" multiple @change="handleUpload" />
        <div class="preview-grid">
          <div v-for="(image, index) in editImages" :key="`${image}-${index}`" class="preview-item">
            <img :src="buildImageUrl(image, 'Preview')" alt="preview" @error="(event) => handleImageError(event, 'Preview')" />
            <div class="preview-actions">
              <button class="mini-btn" :disabled="index === 0" @click="moveImage(index, -1)">↑</button>
              <button class="mini-btn" :disabled="index === editImages.length - 1" @click="moveImage(index, 1)">↓</button>
              <button class="mini-btn danger" @click="removeImage(index)">删</button>
            </div>
          </div>
        </div>
        <button class="btn btn-accent" @click="saveEdit">保存修改</button>
      </div>
    </BaseModal>
  </MainLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import BaseModal from '../components/BaseModal.vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { getCategories, getMyGoods, updateGoods, updateGoodsStatus, uploadGoodsImage } from '../api/goods'
import { useUiStore } from '../stores/ui'
import { buildImageUrl, handleImageError } from '../utils/image'
import { positiveNumber, required } from '../utils/validators'

const list = ref([])
const categories = ref([])
const editingVisible = ref(false)
const currentGoodsId = ref(null)
const editImages = ref([])
const uiStore = useUiStore()
const pageNum = ref(1)
const pageSize = 4
const filters = reactive({ keyword: '', status: '' })
const editForm = reactive({ categoryId: '', title: '', price: '', stock: 1, description: '' })

function formatGoodsStatus(status) {
  const map = {
    PENDING: '待审核',
    ON_SALE: '在售',
    OFF_SHELF: '已下架',
    REJECTED: '已驳回',
    SOLD: '已售出'
  }
  return map[status] || status || '-'
}

const filteredList = computed(() => {
  return list.value.filter((item) => {
    const matchKeyword = !filters.keyword || item.title?.includes(filters.keyword)
    const matchStatus = !filters.status || item.status === filters.status
    return matchKeyword && matchStatus
  })
})

const pagedList = computed(() => {
  const start = (pageNum.value - 1) * pageSize
  return filteredList.value.slice(start, start + pageSize)
})

async function fetchList() {
  list.value = await getMyGoods()
}

async function fetchCategories() {
  categories.value = await getCategories()
}

async function toggleStatus(item) {
  const status = item.status === 'OFF_SHELF' ? 'ON_SALE' : 'OFF_SHELF'
  await updateGoodsStatus(item.id, { status })
  uiStore.showToast('商品状态更新成功', 'success')
  await fetchList()
}

function openEdit(item) {
  currentGoodsId.value = item.id
  editForm.categoryId = item.categoryId
  editForm.title = item.title
  editForm.price = item.price
  editForm.stock = item.stock
  editForm.description = item.description
  editImages.value = (item.images || []).map((image) => image.imageUrl)
  if (!editImages.value.length && item.coverImage) {
    editImages.value = [item.coverImage]
  }
  editingVisible.value = true
}

function applyFilters() {
  pageNum.value = 1
}

function resetFilters() {
  filters.keyword = ''
  filters.status = ''
  pageNum.value = 1
}

async function handleUpload(event) {
  const files = Array.from(event.target.files || [])
  for (const file of files) {
    const formData = new FormData()
    formData.append('file', file)
    const path = await uploadGoodsImage(formData)
    editImages.value.push(path)
  }
}

async function saveEdit() {
  required(editForm.categoryId, '分类')
  required(editForm.title, '商品标题')
  required(editForm.description, '商品描述')
  positiveNumber(editForm.price, '商品价格')
  positiveNumber(editForm.stock, '商品库存')
  if (!editImages.value.length) {
    throw new Error('请至少保留一张商品图片')
  }
  await updateGoods(currentGoodsId.value, {
    categoryId: Number(editForm.categoryId),
    title: editForm.title,
    price: Number(editForm.price),
    stock: Number(editForm.stock),
    description: editForm.description,
    coverImage: editImages.value[0],
    imageUrls: editImages.value
  })
  uiStore.showToast('商品已提交修改并重新进入审核', 'success')
  editingVisible.value = false
  await fetchList()
}

function removeImage(index) {
  editImages.value.splice(index, 1)
}

function moveImage(index, step) {
  const targetIndex = index + step
  if (targetIndex < 0 || targetIndex >= editImages.value.length) {
    return
  }
  const clone = [...editImages.value]
  const [current] = clone.splice(index, 1)
  clone.splice(targetIndex, 0, current)
  editImages.value = clone
}

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchList()])
})
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.page-head {
  margin-bottom: 18px;
}

.toolbar {
  display: grid;
  grid-template-columns: 1fr 200px auto auto;
  gap: 10px;
  padding: 16px;
  margin-bottom: 18px;
}

.list-grid {
  display: grid;
  gap: 16px;
}

.list-card {
  display: grid;
  grid-template-columns: 220px 1fr;
  overflow: hidden;
}

.list-card img {
  width: 100%;
  height: 100%;
  min-height: 220px;
  object-fit: cover;
}

.content {
  padding: 18px;
}

.title-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.meta,
.remark {
  color: var(--muted);
}

.actions {
  margin-top: 14px;
  display: flex;
  gap: 10px;
}

.form-grid {
  display: grid;
  gap: 12px;
}

.preview-grid {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.preview-item {
  display: grid;
  gap: 6px;
}

.preview-grid img {
  width: 110px;
  height: 110px;
  border-radius: 14px;
  object-fit: cover;
}

.preview-actions {
  display: flex;
  gap: 6px;
}

.mini-btn {
  border: none;
  border-radius: 10px;
  padding: 6px 8px;
  background: rgba(35, 22, 15, 0.08);
}

.mini-btn.danger {
  background: rgba(182, 58, 58, 0.12);
  color: var(--danger);
}

@media (max-width: 760px) {
  .toolbar,
  .list-card {
    grid-template-columns: 1fr;
  }
}
</style>
<template>
  <MainLayout>
    <section class="container page-block">
      <div class="card panel-card">
        <div class="page-head">
          <div>
            <h2 class="section-title">商品管理</h2>
            <p class="section-desc">查看、编辑、上架、下架或删除平台商品。</p>
          </div>
        </div>

        <div class="filter-bar">
          <input v-model="filter.keyword" class="input" placeholder="按商品标题搜索" />
          <select v-model="filter.categoryId" class="select">
            <option value="">全部分类</option>
            <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
          </select>
          <select v-model="filter.status" class="select">
            <option value="">全部状态</option>
            <option value="PENDING">待审核</option>
            <option value="ON_SALE">在售</option>
            <option value="OFF_SHELF">已下架</option>
            <option value="REJECTED">已驳回</option>
            <option value="SOLD">已售出</option>
          </select>
          <button class="btn" @click="applyFilter">筛选</button>
        </div>

        <div class="table-shell" v-if="goodsList.records?.length">
          <table class="data-table">
            <thead>
              <tr>
                <th>商品</th>
                <th>分类</th>
                <th>价格</th>
                <th>库存</th>
                <th>卖家</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in goodsList.records" :key="item.id">
                <td>
                  <div class="goods-cell">
                    <img :src="normalizeImage(item.coverImage)" :alt="item.title" @error="(event) => handleImageError(event, item.title || 'Goods')" />
                    <div>
                      <strong>{{ item.title }}</strong>
                      <span>ID: {{ item.id }}</span>
                    </div>
                  </div>
                </td>
                <td>{{ item.categoryName || '-' }}</td>
                <td>￥{{ item.price }}</td>
                <td>{{ item.stock }}</td>
                <td>{{ item.sellerName || '-' }}</td>
                <td><span class="status-tag">{{ statusText(item.status) }}</span></td>
                <td>
                  <div class="action-stack">
                    <button class="btn" @click="openDetail(item)">详情</button>
                    <button class="btn btn-accent" @click="openEdit(item)">编辑</button>
                    <button v-if="item.status !== 'ON_SALE' && item.status !== 'SOLD'" class="btn" @click="changeStatus(item, 'ON_SALE')">上架</button>
                    <button v-if="item.status === 'ON_SALE'" class="btn" @click="changeStatus(item, 'OFF_SHELF')">下架</button>
                    <button class="btn danger-btn" :disabled="item.status === 'SOLD'" @click="removeGoods(item)">删除</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          <PaginationBar :total="goodsList.total || 0" :page-num="page.pageNum" :page-size="page.pageSize" @change="changePage" />
        </div>
        <EmptyState v-else icon="□" title="暂无商品" description="当前筛选条件下没有商品记录。" />
      </div>
    </section>

    <BaseModal v-model="detailVisible" title="商品详情">
      <div v-if="currentGoods" class="detail-grid">
        <div class="image-panel">
          <img :src="normalizeImage(currentGoods.coverImage)" :alt="currentGoods.title" @error="(event) => handleImageError(event, currentGoods.title || 'Goods')" />
        </div>
        <div class="detail-panel">
          <span class="status-tag">{{ statusText(currentGoods.status) }}</span>
          <h3>{{ currentGoods.title }}</h3>
          <div class="price">￥{{ currentGoods.price }}</div>
          <p>{{ currentGoods.description }}</p>
          <div class="meta-list">
            <span>分类：{{ currentGoods.categoryName || '-' }}</span>
            <span>卖家：{{ currentGoods.sellerName || '-' }}</span>
            <span>库存：{{ currentGoods.stock }}</span>
            <span>审核备注：{{ currentGoods.latestAuditRemark || '-' }}</span>
          </div>
          <div class="thumb-grid" v-if="currentGoods.images?.length">
            <img v-for="image in currentGoods.images" :key="image.id || image.imageUrl" :src="normalizeImage(image.imageUrl)" alt="thumb" @error="(event) => handleImageError(event, 'Goods')" />
          </div>
        </div>
      </div>
    </BaseModal>

    <BaseModal v-model="editVisible" title="编辑商品">
      <div class="form-grid" v-if="editForm.id">
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
          <div v-for="image in imageUrls" :key="image" class="preview-item">
            <img :src="normalizeImage(image)" alt="preview" @error="(event) => handleImageError(event, 'Goods')" />
            <button class="remove-image" @click="removeImage(image)">×</button>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-accent" @click="saveEdit">保存修改</button>
          <button class="btn" @click="editVisible = false">取消</button>
        </div>
      </div>
    </BaseModal>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import BaseModal from '../components/BaseModal.vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import {
  deleteAdminGoods,
  getAdminGoodsPage,
  getCategories,
  updateAdminGoods,
  updateAdminGoodsStatus,
  uploadGoodsImage
} from '../api/goods'
import { useUiStore } from '../stores/ui'
import { buildImageUrl, handleImageError } from '../utils/image'
import { positiveNumber, required } from '../utils/validators'

const MAX_FILE_SIZE = 10 * 1024 * 1024
const ALLOWED_FILE_TYPES = ['image/jpeg', 'image/png', 'image/webp']
const categories = ref([])
const goodsList = ref({ total: 0, records: [] })
const filter = reactive({ keyword: '', categoryId: '', status: '' })
const page = reactive({ pageNum: 1, pageSize: 8 })
const detailVisible = ref(false)
const editVisible = ref(false)
const currentGoods = ref(null)
const imageUrls = ref([])
const editForm = reactive({ id: null, categoryId: '', title: '', price: '', stock: 1, description: '' })
const uiStore = useUiStore()

function normalizeImage(path) {
  return buildImageUrl(path, 'Goods')
}

function statusText(status) {
  const map = {
    PENDING: '待审核',
    ON_SALE: '在售',
    OFF_SHELF: '已下架',
    REJECTED: '已驳回',
    SOLD: '已售出'
  }
  return map[status] || status || '-'
}

async function fetchCategories() {
  categories.value = await getCategories()
}

async function fetchGoods() {
  goodsList.value = await getAdminGoodsPage({
    pageNum: page.pageNum,
    pageSize: page.pageSize,
    keyword: filter.keyword || undefined,
    categoryId: filter.categoryId || undefined,
    status: filter.status || undefined
  })
}

function applyFilter() {
  page.pageNum = 1
  fetchGoods()
}

function changePage(nextPage) {
  page.pageNum = nextPage
  fetchGoods()
}

function openDetail(item) {
  currentGoods.value = item
  detailVisible.value = true
}

function openEdit(item) {
  currentGoods.value = item
  editForm.id = item.id
  editForm.categoryId = item.categoryId
  editForm.title = item.title
  editForm.price = item.price
  editForm.stock = item.stock
  editForm.description = item.description
  imageUrls.value = (item.images?.length ? item.images.map((image) => image.imageUrl) : [item.coverImage]).filter(Boolean)
  editVisible.value = true
}

async function handleUpload(event) {
  const files = Array.from(event.target.files || [])
  for (const file of files) {
    if (!ALLOWED_FILE_TYPES.includes(file.type)) {
      uiStore.showToast('上传失败：仅支持 jpg、jpeg、png、webp 格式图片', 'error')
      continue
    }
    if (file.size > MAX_FILE_SIZE) {
      uiStore.showToast('上传失败：单张图片不能超过 10MB', 'error')
      continue
    }
    const formData = new FormData()
    formData.append('file', file)
    const path = await uploadGoodsImage(formData)
    imageUrls.value.push(path)
  }
  event.target.value = ''
}

function removeImage(imageUrl) {
  imageUrls.value = imageUrls.value.filter((item) => item !== imageUrl)
}

async function saveEdit() {
  required(editForm.categoryId, '分类')
  required(editForm.title, '商品标题')
  required(editForm.description, '商品描述')
  positiveNumber(editForm.price, '商品价格')
  positiveNumber(editForm.stock, '商品库存')
  if (!imageUrls.value.length) {
    throw new Error('请至少保留一张商品图片')
  }
  await updateAdminGoods(editForm.id, {
    categoryId: Number(editForm.categoryId),
    title: editForm.title,
    price: Number(editForm.price),
    stock: Number(editForm.stock),
    description: editForm.description,
    coverImage: imageUrls.value[0],
    imageUrls: imageUrls.value
  })
  uiStore.showToast('商品信息已更新', 'success')
  editVisible.value = false
  await fetchGoods()
}

async function changeStatus(item, status) {
  await updateAdminGoodsStatus(item.id, { status })
  uiStore.showToast(`商品已${status === 'ON_SALE' ? '上架' : '下架'}`, 'success')
  await fetchGoods()
}

async function removeGoods(item) {
  if (!window.confirm(`确定删除商品“${item.title}”吗？`)) {
    return
  }
  await deleteAdminGoods(item.id)
  uiStore.showToast('商品已删除', 'success')
  await fetchGoods()
}

onMounted(async () => {
  await fetchCategories()
  await fetchGoods()
})
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
  grid-template-columns: 1fr 180px 160px auto;
  gap: 10px;
  margin: 18px 0;
}

.table-shell {
  overflow: auto;
}

.data-table {
  width: 100%;
  min-width: 920px;
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

.goods-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 230px;
}

.goods-cell img {
  width: 58px;
  height: 58px;
  border-radius: 14px;
  object-fit: cover;
}

.goods-cell span {
  display: block;
  margin-top: 4px;
  color: var(--muted);
  font-size: 13px;
}

.action-stack,
.form-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.danger-btn {
  background: var(--danger);
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.45;
  transform: none;
}

.detail-grid {
  display: grid;
  grid-template-columns: 0.9fr 1.1fr;
  gap: 18px;
}

.image-panel img {
  width: 100%;
  height: 320px;
  object-fit: cover;
  border-radius: 18px;
}

.detail-panel h3 {
  margin: 12px 0 8px;
}

.price {
  font-size: 30px;
  font-weight: 700;
  color: var(--accent-deep);
}

.meta-list {
  display: grid;
  gap: 8px;
  color: var(--muted);
  margin-top: 14px;
}

.thumb-grid,
.preview-grid {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 14px;
}

.thumb-grid img {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  object-fit: cover;
}

.form-grid {
  display: grid;
  gap: 14px;
}

.preview-item {
  position: relative;
}

.preview-item img {
  width: 96px;
  height: 96px;
  border-radius: 14px;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 50%;
  background: var(--danger);
  color: #fff;
  font-size: 18px;
}

@media (max-width: 960px) {
  .filter-bar,
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>

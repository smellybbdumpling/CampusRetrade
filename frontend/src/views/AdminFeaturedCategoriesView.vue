<template>
  <MainLayout>
    <section class="container page-block">
      <div class="page-head">
        <div>
          <h2 class="section-title">特色分类管理</h2>
          <p class="section-desc">管理员可以维护毕业甩卖、新生必备等特色专区，并决定它们在首页的展示顺序。</p>
        </div>
        <button class="btn btn-accent" @click="openCreate">新增特色分类</button>
      </div>

      <div class="toolbar card">
        <input v-model="filters.name" class="input" placeholder="按特色分类名称搜索" />
        <select v-model="filters.status" class="select">
          <option value="">全部状态</option>
          <option value="ENABLED">启用</option>
          <option value="DISABLED">停用</option>
        </select>
        <button class="btn" @click="applyFilters">筛选</button>
      </div>

      <div class="table-shell card">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>状态</th>
              <th>排序</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list.records || []" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.name }}</td>
              <td>{{ formatFeaturedStatus(item.status) }}</td>
              <td>{{ item.sortOrder }}</td>
              <td><button class="btn" @click="openEdit(item)">编辑</button></td>
            </tr>
          </tbody>
        </table>
        <PaginationBar :total="list.total || 0" :page-num="pageNum" :page-size="pageSize" @change="changePage" />
      </div>
    </section>

    <BaseModal v-model="visible" :title="editingId ? '编辑特色分类' : '新增特色分类'">
      <div class="form-grid">
        <input v-model="form.name" class="input" placeholder="特色分类名称" />
        <select v-model="form.status" class="select">
          <option value="ENABLED">启用</option>
          <option value="DISABLED">停用</option>
        </select>
        <input v-model="form.sortOrder" class="input" type="number" min="1" step="1" placeholder="排序值" />
        <button class="btn btn-accent" @click="submit">保存</button>
      </div>
    </BaseModal>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import BaseModal from '../components/BaseModal.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { createFeaturedCategory, getFeaturedCategoryPage, updateFeaturedCategory } from '../api/admin'
import { useUiStore } from '../stores/ui'
import { required } from '../utils/validators'

const uiStore = useUiStore()
const list = ref({ total: 0, records: [] })
const pageNum = ref(1)
const pageSize = 8
const visible = ref(false)
const editingId = ref(null)
const filters = reactive({ name: '', status: '' })
const form = reactive({ name: '', status: 'ENABLED', sortOrder: 1 })

function formatFeaturedStatus(status) {
  const map = {
    ENABLED: '启用',
    DISABLED: '停用'
  }
  return map[status] || status || '-'
}

async function fetchList() {
  list.value = await getFeaturedCategoryPage({
    pageNum: pageNum.value,
    pageSize,
    name: filters.name || undefined,
    status: filters.status || undefined
  })
}

function applyFilters() {
  pageNum.value = 1
  fetchList()
}

function changePage(page) {
  pageNum.value = page
  fetchList()
}

function openCreate() {
  editingId.value = null
  form.name = ''
  form.status = 'ENABLED'
  form.sortOrder = 1
  visible.value = true
}

function openEdit(item) {
  editingId.value = item.id
  form.name = item.name
  form.status = item.status
  form.sortOrder = item.sortOrder
  visible.value = true
}

async function submit() {
  required(form.name, '特色分类名称')
  if (editingId.value) {
    await updateFeaturedCategory(editingId.value, { ...form, sortOrder: Number(form.sortOrder) })
    uiStore.showToast('特色分类更新成功', 'success')
  } else {
    await createFeaturedCategory({ ...form, sortOrder: Number(form.sortOrder) })
    uiStore.showToast('特色分类创建成功', 'success')
  }
  visible.value = false
  await fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 18px;
}

.toolbar {
  display: grid;
  grid-template-columns: 1fr 180px auto;
  gap: 10px;
  padding: 16px;
  margin-bottom: 18px;
}

.table-shell {
  padding: 16px;
  overflow: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 560px;
}

.data-table th,
.data-table td {
  padding: 12px 10px;
  border-bottom: 1px solid rgba(55, 35, 20, 0.08);
  text-align: left;
}

.form-grid {
  display: grid;
  gap: 12px;
}

@media (max-width: 900px) {
  .page-head,
  .toolbar {
    grid-template-columns: 1fr;
    display: grid;
  }
}
</style>
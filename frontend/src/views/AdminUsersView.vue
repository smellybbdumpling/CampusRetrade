<template>
  <MainLayout>
    <section class="container page-block">
      <div class="card panel-card">
        <div class="page-head">
          <div>
            <h2 class="section-title">用户管理</h2>
            <p class="section-desc">查看用户列表，按状态筛选并禁用或启用用户账号。</p>
          </div>
        </div>

        <div class="filter-bar">
          <input v-model="userFilter.username" class="input" placeholder="按用户名搜索" />
          <select v-model="userFilter.status" class="select">
            <option value="">全部状态</option>
            <option value="NORMAL">正常</option>
            <option value="DISABLED">已禁用</option>
          </select>
          <button class="btn" @click="applyUserFilter">筛选用户</button>
        </div>

        <div class="table-shell" v-if="userList.records?.length">
          <table class="data-table">
            <thead>
              <tr>
                <th>用户ID</th>
                <th>用户名</th>
                <th>昵称</th>
                <th>信用分</th>
                <th>信用等级</th>
                <th>有效举报</th>
                <th>角色</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in userList.records" :key="user.id">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.nickname }}</td>
                <td>{{ user.creditScore ?? 100 }}</td>
                <td>{{ user.creditLevel || '正常' }}</td>
                <td>{{ user.effectiveReportCount ?? 0 }}</td>
                <td>{{ user.role }}</td>
                <td>{{ formatUserStatus(user.status) }}</td>
                <td><button class="btn" @click="toggleUser(user)">{{ user.status === 'NORMAL' ? '禁用' : '启用' }}</button></td>
              </tr>
            </tbody>
          </table>
          <PaginationBar :total="userList.total || 0" :page-num="userPage.pageNum" :page-size="userPage.pageSize" @change="changeUserPage" />
        </div>
        <EmptyState v-if="!userList.records?.length" icon="◇" title="暂无用户数据" description="当前筛选条件下没有用户记录。" />
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { getUserPage, updateUserStatus } from '../api/admin'
import { useUiStore } from '../stores/ui'

const userList = ref({ records: [] })
const userFilter = ref({ username: '', status: '' })
const userPage = ref({ pageNum: 1, pageSize: 8 })
const uiStore = useUiStore()

function formatUserStatus(status) {
  const map = {
    NORMAL: '正常',
    DISABLED: '已禁用'
  }
  return map[status] || status || '-'
}

async function fetchUsers() {
  userList.value = await getUserPage({
    pageNum: userPage.value.pageNum,
    pageSize: userPage.value.pageSize,
    username: userFilter.value.username || undefined,
    status: userFilter.value.status || undefined
  })
}

async function toggleUser(user) {
  await updateUserStatus(user.id, { status: user.status === 'NORMAL' ? 'DISABLED' : 'NORMAL' })
  uiStore.showToast('用户状态更新成功', 'success')
  await fetchUsers()
}

function applyUserFilter() {
  userPage.value.pageNum = 1
  fetchUsers()
}

function changeUserPage(page) {
  userPage.value.pageNum = page
  fetchUsers()
}

onMounted(fetchUsers)
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
  min-width: 680px;
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

@media (max-width: 960px) {
  .filter-bar {
    grid-template-columns: 1fr;
  }
}
</style>

<template>
  <MainLayout>
    <section class="container page-block">
      <div class="card panel-card">
        <div class="page-head">
          <div>
            <h2 class="section-title">留言管理</h2>
            <p class="section-desc">管理买卖家在商品详情页的留言与回复，对不良言论可直接删除。</p>
          </div>
          <button class="btn danger-btn" @click="removeAllMessages">全部删除</button>
        </div>

        <div class="filter-bar">
          <input v-model="query.keyword" class="input" placeholder="按留言内容搜索" />
          <input v-model="query.senderUsername" class="input" placeholder="按发送者用户名搜索" />
          <button class="btn btn-accent" @click="applyFilter">筛选</button>
        </div>

        <div class="table-shell" v-if="messagePage.records?.length">
          <table class="data-table">
            <thead>
              <tr>
                <th>发送者</th>
                <th>内容</th>
                <th>发送时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in messagePage.records" :key="item.id">
                <td>
                  <router-link class="user-link" :to="`/users/${item.senderId}`">{{ item.senderName }}</router-link>
                </td>
                <td>{{ item.content }}</td>
                <td>
                  <div class="message-time">{{ formatMessageTime(item.createTime).date }}</div>
                  <div class="message-time">{{ formatMessageTime(item.createTime).time }}</div>
                </td>
                <td>
                  <button class="btn danger-btn" @click="removeMessage(item.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
          <PaginationBar :total="messagePage.total || 0" :page-num="page.pageNum" :page-size="page.pageSize" @change="changePage" />
        </div>

        <EmptyState v-else icon="!" title="暂无沟通记录" description="当前筛选条件下没有买卖家留言记录。" />
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { deleteAllGoodsMessages, deleteGoodsMessage, getGoodsMessagePage } from '../api/admin'
import { useUiStore } from '../stores/ui'

const query = reactive({ keyword: '', senderUsername: '' })
const page = reactive({ pageNum: 1, pageSize: 10 })
const messagePage = ref({ total: 0, records: [] })
const uiStore = useUiStore()

function formatMessageTime(value) {
  if (!value) {
    return { date: '-', time: '-' }
  }
  const normalized = String(value).replace('T', ' ')
  const [date = '-', time = '-'] = normalized.split(' ')
  return { date, time }
}

async function fetchMessages() {
  messagePage.value = await getGoodsMessagePage({
    pageNum: page.pageNum,
    pageSize: page.pageSize,
    keyword: query.keyword || undefined,
    senderUsername: query.senderUsername || undefined
  })
}

function applyFilter() {
  page.pageNum = 1
  fetchMessages()
}

function changePage(nextPage) {
  page.pageNum = nextPage
  fetchMessages()
}

async function removeMessage(id) {
  await deleteGoodsMessage(id)
  uiStore.showToast('留言已删除', 'success')
  await fetchMessages()
}

async function removeAllMessages() {
  await deleteAllGoodsMessages()
  uiStore.showToast('全部留言已删除', 'success')
  await fetchMessages()
}

onMounted(fetchMessages)
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
  margin-bottom: 16px;
}

.filter-bar {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 10px;
  margin-bottom: 18px;
}

.table-shell {
  overflow: auto;
}

.data-table {
  width: 100%;
  min-width: 760px;
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

.user-link {
  color: var(--accent-deep);
  font-weight: 700;
}

.message-time {
  color: var(--muted);
  line-height: 1.4;
}

.danger-btn {
  background: var(--danger);
}

@media (max-width: 760px) {
  .page-head,
  .filter-bar {
    grid-template-columns: 1fr;
  }
}
</style>
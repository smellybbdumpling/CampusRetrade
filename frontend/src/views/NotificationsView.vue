<template>
  <MainLayout>
    <section class="container page-block">
      <div class="page-head">
        <div>
          <h2 class="section-title">消息中心</h2>
          <p class="section-desc">查看买卖双方留言、回复等站内通知。</p>
        </div>
        <div class="head-actions">
          <button class="btn" @click="readAll">全部标为已读</button>
          <button class="btn danger-btn" @click="removeAll">全部删除</button>
        </div>
      </div>

      <div class="list-grid" v-if="notifications.length">
        <article v-for="item in notifications" :key="item.id" class="card notice-card" :class="{ unread: !item.readFlag }">
          <div class="notice-top">
            <div class="notice-title-row">
              <strong>{{ item.title }}</strong>
              <span :class="['status-chip', item.readFlag ? 'read' : 'unread-chip']">{{ item.readFlag ? '已读' : '未读' }}</span>
            </div>
            <div class="notice-side">
              <div class="notice-time">{{ formatMessageTime(item.createTime).date }}</div>
              <div class="notice-time">{{ formatMessageTime(item.createTime).time }}</div>
            </div>
          </div>
          <p class="notice-content">{{ item.content }}</p>
          <div class="actions">
            <router-link class="btn btn-accent" :to="item.actionUrl" @click="markRead(item.id)">查看详情</router-link>
            <button class="mini-link danger-text delete-outline" @click="removeOne(item.id)">删除</button>
          </div>
        </article>
      </div>

      <EmptyState v-else icon="✦" title="暂无消息" description="有新的留言或回复时，会在这里提醒你。" />
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import { deleteAllNotifications, deleteNotification, getNotifications, markAllNotificationsRead, markNotificationRead } from '../api/auth'
import { useUiStore } from '../stores/ui'

const notifications = ref([])
const uiStore = useUiStore()

function formatMessageTime(value) {
  if (!value) {
    return { date: '-', time: '-' }
  }
  const normalized = String(value).replace('T', ' ')
  const [date = '-', time = '-'] = normalized.split(' ')
  return { date, time }
}

async function fetchNotifications() {
  notifications.value = await getNotifications()
}

async function markRead(id) {
  await markNotificationRead(id)
}

async function removeOne(id) {
  await deleteNotification(id)
  uiStore.showToast('通知已删除', 'success')
  await fetchNotifications()
}

async function readAll() {
  await markAllNotificationsRead()
  uiStore.showToast('消息已全部标为已读', 'success')
  await fetchNotifications()
}

async function removeAll() {
  await deleteAllNotifications()
  uiStore.showToast('消息已全部删除', 'success')
  await fetchNotifications()
}

onMounted(fetchNotifications)
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.head-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.list-grid {
  display: grid;
  gap: 12px;
}

.notice-card {
  padding: 16px 18px;
}

.notice-card.unread {
  border-color: rgba(212, 106, 46, 0.35);
}

.notice-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: flex-start;
  color: var(--muted);
}

.notice-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.notice-side {
  display: grid;
  justify-items: end;
  gap: 2px;
  flex-shrink: 0;
}

.status-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 52px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.status-chip.read {
  background: rgba(47, 125, 87, 0.12);
  color: var(--ok);
}

.status-chip.unread-chip {
  background: rgba(212, 106, 46, 0.12);
  color: var(--accent-deep);
}

.notice-time {
  color: var(--muted);
  line-height: 1.3;
  text-align: right;
}

.mini-link {
  padding: 8px 14px;
  font: inherit;
  border-radius: 999px;
  border: 1px solid rgba(182, 58, 58, 0.28);
  background: rgba(255, 255, 255, 0.72);
}

.danger-text,
.danger-btn {
  color: var(--danger);
}

.delete-outline {
  margin-left: auto;
}

.danger-btn {
  background: rgba(182, 58, 58, 0.12);
}

.actions {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.notice-content {
  margin: 10px 0 0;
}

@media (max-width: 760px) {
  .page-head,
  .notice-top {
    flex-direction: column;
  }

  .notice-side {
    justify-items: start;
  }

  .notice-time {
    text-align: left;
  }

  .delete-outline {
    margin-left: 0;
  }
}
</style>

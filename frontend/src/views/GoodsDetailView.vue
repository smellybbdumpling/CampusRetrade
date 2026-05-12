<template>
  <MainLayout>
    <section class="container detail-grid" v-if="detail.id">
      <div class="gallery card">
        <img class="cover" :src="currentImage" :alt="detail.title" @error="(event) => handleImageError(event, detail.title || 'Goods')" />
        <div class="thumbs">
          <button
            v-for="item in detail.images || []"
            :key="item.id || item.imageUrl"
            class="thumb"
            @click="currentImage = normalizeImage(item.imageUrl)"
          >
            <img :src="normalizeImage(item.imageUrl)" alt="thumb" @error="(event) => handleImageError(event, 'Thumb')" />
          </button>
        </div>
      </div>

      <div class="card detail-card">
        <span class="status-tag">{{ detail.categoryName }}</span>
        <h1>{{ detail.title }}</h1>
        <div class="price">￥{{ detail.price }}</div>
        <p>{{ detail.description }}</p>
        <div class="meta-list">
          <span>
            卖家：
            <router-link class="seller-link" :to="`/users/${detail.sellerId}`">{{ detail.sellerName || '查看卖家资料' }}</router-link>
          </span>
          <span>库存：{{ detail.stock }}</span>
          <span>状态：{{ formatGoodsStatus(detail.status) }}</span>
        </div>
        <div class="action-row">
          <button class="btn" @click="toggleFavorite">{{ detail.favorited ? '取消收藏' : '收藏商品' }}</button>
          <button class="btn" @click="openReport">举报商品</button>
          <button class="btn btn-accent" @click="handleAddToCart">加入购物车</button>
        </div>
      </div>
    </section>

    <section class="container message-section" v-if="detail.id">
      <div class="card message-card">
        <div class="message-head">
          <div>
            <h2 class="section-title">留言 / 咨询</h2>
            <p class="section-desc">每条留言和回复都支持继续回复，模拟真实交易沟通。</p>
          </div>
        </div>

        <div class="message-form" v-if="canPostTopLevel">
          <textarea v-model="messageForm.content" class="textarea" rows="3" placeholder="例如：请问今晚能在图书馆门口交易吗？"></textarea>
          <button class="btn btn-accent" @click="submitMessage()">发送留言</button>
        </div>
        <div v-else-if="authStore.isLoggedIn && authStore.user?.id === detail.sellerId" class="seller-tip">
          你是卖家，可在下方任意留言或回复右侧点击“回复”进行沟通。
        </div>

        <div class="message-list" v-if="detail.messages?.length">
          <GoodsMessageThread
            v-for="message in detail.messages"
            :key="message.id"
            :message="message"
            :depth="0"
            :active-reply-id="activeReplyId"
            :reply-drafts="replyDrafts"
            :can-reply="canReply"
            :toggle-reply-box="toggleReplyBox"
            :submit-reply="submitMessage"
            :format-message-time="formatMessageTime"
          />
        </div>

        <EmptyState v-else icon="✉" title="还没有留言" description="如果你对这件商品感兴趣，可以先发一条咨询消息。" />
      </div>
    </section>

    <BaseModal v-model="reportVisible" title="举报商品">
      <div class="report-form">
        <select v-model="reportForm.reportType" class="select">
          <option value="FALSE_INFO">虚假描述</option>
          <option value="PRICE_ISSUE">价格异常</option>
          <option value="PROHIBITED_GOODS">违禁商品</option>
          <option value="SPAM">重复发布/引流</option>
          <option value="ABUSE">不文明内容</option>
          <option value="OTHER">其他</option>
        </select>
        <input v-model="reportForm.reportReason" class="input" placeholder="举报原因" />
        <textarea v-model="reportForm.reportDescription" class="textarea" rows="4" placeholder="补充说明，方便管理员核实"></textarea>
        <button class="btn btn-accent" @click="submitReport">提交举报</button>
      </div>
    </BaseModal>
  </MainLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BaseModal from '../components/BaseModal.vue'
import EmptyState from '../components/EmptyState.vue'
import GoodsMessageThread from '../components/GoodsMessageThread.vue'
import MainLayout from '../layouts/MainLayout.vue'
import { favoriteGoods, getGoodsDetail, reportGoods, unfavoriteGoods } from '../api/goods'
import { addToCart } from '../api/order'
import { useAuthStore } from '../stores/auth'
import { useUiStore } from '../stores/ui'
import { buildImageUrl, handleImageError } from '../utils/image'
import request from '../utils/request'
import { required } from '../utils/validators'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const uiStore = useUiStore()
const detail = ref({})
const currentImage = ref('')
const reportVisible = ref(false)
const messageForm = reactive({ content: '' })
const replyDrafts = reactive({})
const activeReplyId = ref(null)
const reportForm = reactive({ reportType: 'FALSE_INFO', reportReason: '', reportDescription: '' })

const canPostTopLevel = computed(() => authStore.isLoggedIn && authStore.user?.id !== detail.value.sellerId)

function normalizeImage(path) {
  return buildImageUrl(path, 'Goods')
}

function formatMessageTime(value) {
  if (!value) {
    return { date: '-', time: '-' }
  }
  const normalized = String(value).replace('T', ' ')
  const [date = '-', time = '-'] = normalized.split(' ')
  return { date, time }
}

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

async function fetchDetail() {
  detail.value = await getGoodsDetail(route.params.id)
  currentImage.value = normalizeImage(detail.value.coverImage)
}

async function handleAddToCart() {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  await addToCart({ goodsId: detail.value.id, quantity: 1 })
  uiStore.showToast('已加入购物车', 'success')
}

async function toggleFavorite() {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (detail.value.favorited) {
    await unfavoriteGoods(detail.value.id)
    uiStore.showToast('已取消收藏', 'success')
  } else {
    await favoriteGoods(detail.value.id)
    uiStore.showToast('收藏成功', 'success')
  }
  await fetchDetail()
}

function openReport() {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  reportVisible.value = true
}

function canReply(message) {
  return authStore.isLoggedIn && authStore.user?.id !== message.senderId
}

function toggleReplyBox(messageId) {
  activeReplyId.value = activeReplyId.value === messageId ? null : messageId
}

async function submitMessage(parentId = null) {
  try {
    const content = parentId ? replyDrafts[parentId] : messageForm.content
    required(content, parentId ? '回复内容' : '留言内容')
    await request.post(`/api/goods/${detail.value.id}/messages`, {
      parentId,
      content
    })
    if (parentId) {
      replyDrafts[parentId] = ''
      activeReplyId.value = null
    } else {
      messageForm.content = ''
    }
    uiStore.showToast(parentId ? '回复发送成功' : '留言发送成功', 'success')
    await fetchDetail()
  } catch (error) {
    uiStore.showToast(error.message || '留言发送失败', 'error')
  }
}

async function submitReport() {
  try {
    required(reportForm.reportReason, '举报原因')
    await reportGoods(detail.value.id, {
      reportType: reportForm.reportType,
      reportReason: reportForm.reportReason,
      reportDescription: reportForm.reportDescription
    })
    reportVisible.value = false
    reportForm.reportType = 'FALSE_INFO'
    reportForm.reportReason = ''
    reportForm.reportDescription = ''
    uiStore.showToast('举报提交成功', 'success')
  } catch (error) {
    uiStore.showToast(error.message || '举报提交失败', 'error')
  }
}

onMounted(fetchDetail)
</script>

<style scoped>
.detail-grid {
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  gap: 22px;
  padding: 30px 0 48px;
}

.gallery,
.detail-card {
  padding: 20px;
}

.cover {
  width: 100%;
  height: 440px;
  object-fit: cover;
  border-radius: 20px;
}

.thumbs {
  display: flex;
  gap: 10px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.thumb {
  border: none;
  background: transparent;
  padding: 0;
}

.thumb img {
  width: 88px;
  height: 88px;
  border-radius: 16px;
  object-fit: cover;
}

.detail-card h1 {
  margin: 14px 0 10px;
}

.price {
  font-size: 36px;
  font-weight: 700;
  color: var(--accent-deep);
}

.meta-list {
  display: grid;
  gap: 8px;
  color: var(--muted);
  margin: 16px 0 20px;
}

.action-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.seller-link {
  color: var(--accent-deep);
  font-weight: 700;
}

.message-section {
  padding-bottom: 48px;
}

.message-card {
  padding: 20px;
}

.message-head {
  margin-bottom: 16px;
}

.message-form {
  display: grid;
  gap: 10px;
  margin-bottom: 16px;
}

.seller-tip {
  margin-bottom: 16px;
  color: var(--muted);
}

.message-list {
  display: grid;
  gap: 14px;
}

.report-form {
  display: grid;
  gap: 12px;
}

@media (max-width: 960px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>

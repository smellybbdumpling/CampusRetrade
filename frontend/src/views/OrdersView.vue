<template>
  <MainLayout>
    <section class="container page-block">
      <div class="tab-row">
        <button :class="['tab', { active: tab === 'buy' }]" @click="tab = 'buy'">买家订单</button>
        <button :class="['tab', { active: tab === 'sell' }]" @click="tab = 'sell'">卖家订单</button>
      </div>

      <div v-if="tab === 'buy'" class="card cart-panel">
        <div class="cart-head">
          <div>
            <h2 class="section-title">购物车</h2>
            <p class="section-desc">在这里确认商品、移除不需要的内容，并直接提交订单。</p>
          </div>
        </div>
        <div v-if="cartList.length" class="cart-list">
          <article v-for="item in cartList" :key="item.id" class="cart-item">
            <img :src="item.coverImage" :alt="item.title" />
            <div class="cart-body">
              <strong>{{ item.title }}</strong>
              <p>单价：￥{{ item.price }} / 数量：{{ item.quantity }}</p>
              <span>小计：￥{{ item.subtotal }}</span>
            </div>
            <button class="btn" @click="removeCart(item.id)">移除</button>
          </article>
          <div class="cart-submit">
            <textarea v-model="submitForm.remark" class="textarea" rows="3" placeholder="订单备注，例如校内自提、交易时间等"></textarea>
            <div class="cart-summary">
              <strong>合计：￥{{ cartTotal }}</strong>
              <button class="btn btn-accent" @click="submitCartOrder">提交订单</button>
            </div>
          </div>
        </div>
        <EmptyState v-else icon="◍" title="购物车为空" description="先去商品详情页加入商品，再回来提交订单。" />
      </div>

      <div class="filter-bar card">
        <select v-model="filters.status" class="select">
          <option value="">全部状态</option>
          <option value="CREATED">待发货</option>
          <option value="SHIPPED">已发货</option>
          <option value="FINISHED">已完成</option>
          <option value="CANCELED">已取消</option>
        </select>
        <input v-model="filters.keyword" class="input" placeholder="按订单号搜索" />
        <button class="btn btn-accent" @click="pageNum = 1">筛选</button>
      </div>

      <div class="order-grid">
        <article v-for="item in pagedOrders" :key="item.id" class="card order-card">
          <div class="order-top">
            <div>
              <strong>{{ item.orderNo }}</strong>
              <div class="meta">状态：{{ formatOrderStatus(item.status) }}</div>
            </div>
            <div class="price">￥{{ item.totalAmount }}</div>
          </div>
          <ul>
            <li v-for="goods in item.items || []" :key="goods.goodsId">{{ goods.goodsTitle }} x {{ goods.quantity }}</li>
          </ul>
          <div class="actions">
            <button class="btn" @click="openDetail(item)">查看详情</button>
            <button v-if="tab === 'buy' && item.status === 'CREATED'" class="btn" @click="cancel(item.id)">取消订单</button>
            <button v-if="tab === 'buy' && item.status === 'SHIPPED'" class="btn btn-accent" @click="finish(item.id)">确认收货</button>
            <button v-if="tab === 'sell' && item.status === 'CREATED'" class="btn btn-accent" @click="ship(item.id)">发货</button>
          </div>
        </article>
      </div>
      <EmptyState
        v-if="!currentList.length"
        icon="◌"
        :title="tab === 'buy' ? '暂无买家订单' : '暂无卖家订单'"
        :description="tab === 'buy' ? '你还没有提交订单。' : '还没有人购买你的商品。'"
      />
      <PaginationBar
        v-if="filteredOrders.length"
        :total="filteredOrders.length"
        :page-num="pageNum"
        :page-size="pageSize"
        @change="pageNum = $event"
      />
    </section>

    <BaseModal v-model="detailVisible" title="订单详情">
      <template v-if="activeOrder">
        <div class="detail-grid">
          <div><strong>订单号：</strong>{{ activeOrder.orderNo }}</div>
          <div><strong>状态：</strong>{{ formatOrderStatus(activeOrder.status) }}</div>
          <div>
            <strong>买家：</strong>
            <router-link class="user-link" :to="`/users/${activeOrder.buyerId}`">{{ activeOrder.buyerName || `用户${activeOrder.buyerId}` }}</router-link>
          </div>
          <div>
            <strong>卖家：</strong>
            <router-link class="user-link" :to="`/users/${activeOrder.sellerId}`">{{ activeOrder.sellerName || `用户${activeOrder.sellerId}` }}</router-link>
          </div>
          <div><strong>备注：</strong>{{ activeOrder.remark || '无' }}</div>
          <div><strong>总金额：</strong>￥{{ activeOrder.totalAmount }}</div>
        </div>
        <ul class="detail-list">
          <li v-for="goods in activeOrder.items || []" :key="goods.goodsId">
            {{ goods.goodsTitle }} / 单价 ￥{{ goods.goodsPrice }} / 数量 {{ goods.quantity }} / 小计 ￥{{ goods.subtotalAmount }}
          </li>
        </ul>
      </template>
    </BaseModal>
  </MainLayout>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import BaseModal from '../components/BaseModal.vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { cancelOrder, finishOrder, getBuyOrderDetail, getBuyOrders, getCartList, getSellOrderDetail, getSellOrders, removeCartItem, shipOrder, submitOrder } from '../api/order'
import { useUiStore } from '../stores/ui'

const tab = ref('buy')
const buyOrders = ref([])
const sellOrders = ref([])
const cartList = ref([])
const detailVisible = ref(false)
const activeOrder = ref(null)
const uiStore = useUiStore()
const pageNum = ref(1)
const pageSize = 5
const filters = reactive({ status: '', keyword: '' })
const submitForm = reactive({ remark: '' })

function formatOrderStatus(status) {
  const map = {
    CREATED: '待发货',
    SHIPPED: '已发货',
    FINISHED: '已完成',
    CANCELED: '已取消'
  }
  return map[status] || status || '-'
}

const currentList = computed(() => (tab.value === 'buy' ? buyOrders.value : sellOrders.value))
const cartTotal = computed(() => cartList.value.reduce((sum, item) => sum + Number(item.subtotal || 0), 0))
const filteredOrders = computed(() => {
  return currentList.value.filter((item) => {
    const matchStatus = !filters.status || item.status === filters.status
    const matchKeyword = !filters.keyword || item.orderNo?.includes(filters.keyword)
    return matchStatus && matchKeyword
  })
})
const pagedOrders = computed(() => {
  const start = (pageNum.value - 1) * pageSize
  return filteredOrders.value.slice(start, start + pageSize)
})

async function fetchOrders() {
  buyOrders.value = await getBuyOrders()
  sellOrders.value = await getSellOrders()
}

async function fetchCart() {
  cartList.value = await getCartList()
}

async function cancel(id) {
  await cancelOrder(id)
  uiStore.showToast('订单已取消', 'success')
  await fetchOrders()
}

async function finish(id) {
  await finishOrder(id)
  uiStore.showToast('确认收货成功', 'success')
  await fetchOrders()
}

async function ship(id) {
  await shipOrder(id)
  uiStore.showToast('卖家已发货', 'success')
  await fetchOrders()
}

async function removeCart(id) {
  await removeCartItem(id)
  uiStore.showToast('购物车商品已移除', 'success')
  await fetchCart()
}

async function submitCartOrder() {
  await submitOrder({ remark: submitForm.remark })
  uiStore.showToast('订单提交成功', 'success')
  submitForm.remark = ''
  await Promise.all([fetchCart(), fetchOrders()])
}

async function openDetail(item) {
  activeOrder.value = tab.value === 'buy' ? await getBuyOrderDetail(item.id) : await getSellOrderDetail(item.id)
}

watch(activeOrder, (value) => {
  detailVisible.value = Boolean(value)
})

watch(tab, async () => {
  pageNum.value = 1
  filters.status = ''
  filters.keyword = ''
  await Promise.all([fetchOrders(), fetchCart()])
})

onMounted(async () => {
  await Promise.all([fetchOrders(), fetchCart()])
})
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.tab-row {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
}

.cart-panel {
  padding: 20px;
  margin-bottom: 18px;
}

.cart-head {
  margin-bottom: 16px;
}

.cart-list {
  display: grid;
  gap: 12px;
}

.cart-item {
  display: grid;
  grid-template-columns: 96px 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 14px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.66);
  border: 1px solid var(--line);
}

.cart-item img {
  width: 96px;
  height: 96px;
  border-radius: 14px;
  object-fit: cover;
}

.cart-body p,
.cart-body span {
  color: var(--muted);
}

.cart-submit {
  display: grid;
  gap: 12px;
  margin-top: 8px;
}

.cart-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.filter-bar {
  display: grid;
  grid-template-columns: 200px 1fr auto;
  gap: 10px;
  padding: 16px;
  margin-bottom: 18px;
}

.tab {
  border: 1px solid var(--line);
  border-radius: 999px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.68);
}

.tab.active {
  background: var(--text);
  color: #fff;
}

.order-grid {
  display: grid;
  gap: 16px;
}

.order-card {
  padding: 18px;
}

.order-top {
  display: flex;
  justify-content: space-between;
  gap: 14px;
}

.meta {
  color: var(--muted);
  margin-top: 4px;
}

.price {
  font-size: 24px;
  font-weight: 700;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  color: var(--muted);
}

.detail-list {
  margin: 16px 0 0;
  padding-left: 18px;
  color: var(--muted);
}

.user-link {
  color: var(--accent-deep);
  font-weight: 700;
}

@media (max-width: 760px) {
  .cart-item,
  .cart-summary,
  .filter-bar,
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .cart-summary {
    display: grid;
  }
}
</style>
<template>
  <MainLayout>
    <section class="container page-block">
      <div class="page-head">
        <h2 class="section-title">我的收藏</h2>
        <p class="section-desc">管理你收藏过的商品，随时查看详情或取消收藏。</p>
      </div>

      <div v-if="favorites.length" class="list-grid">
        <article v-for="item in favorites" :key="item.id" class="card list-card">
          <img :src="buildImageUrl(item.coverImage, item.title || 'Goods')" :alt="item.title" @error="(event) => handleImageError(event, item.title || 'Goods')" />
          <div class="content">
            <div class="title-row">
              <h3>{{ item.title }}</h3>
              <span class="status-tag">{{ item.status }}</span>
            </div>
            <p>{{ item.description }}</p>
            <div class="meta">￥{{ item.price }} / 卖家 {{ item.sellerName || '未知' }}</div>
            <div class="actions">
              <router-link class="btn btn-accent" :to="`/goods/${item.id}`">查看商品</router-link>
              <button class="btn" @click="removeFavorite(item.id)">取消收藏</button>
            </div>
          </div>
        </article>
      </div>

      <EmptyState v-else icon="★" title="还没有收藏商品" description="去商品详情页点一下收藏，感兴趣的商品就会出现在这里。" />
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import { getMyFavorites, unfavoriteGoods } from '../api/goods'
import { useUiStore } from '../stores/ui'
import { buildImageUrl, handleImageError } from '../utils/image'

const favorites = ref([])
const uiStore = useUiStore()

async function fetchFavorites() {
  favorites.value = await getMyFavorites()
}

async function removeFavorite(goodsId) {
  await unfavoriteGoods(goodsId)
  uiStore.showToast('已取消收藏', 'success')
  await fetchFavorites()
}

onMounted(fetchFavorites)
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.page-head {
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

.meta {
  color: var(--muted);
}

.actions {
  margin-top: 14px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 760px) {
  .list-card {
    grid-template-columns: 1fr;
  }
}
</style>

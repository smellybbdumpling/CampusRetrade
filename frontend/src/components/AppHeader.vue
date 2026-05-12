<template>
  <header class="header-wrap">
    <div class="container header-inner">
      <router-link to="/" class="brand">
        <img class="brand-mark" :src="brandLogo" alt="Campus Re:Trade logo" />
        <div>
          <div class="brand-title">Campus Re:Trade</div>
          <div class="brand-subtitle">校友有点闲</div>
        </div>
      </router-link>

      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/publish" v-if="authStore.isLoggedIn">发布商品</router-link>
        <router-link to="/my-goods" v-if="authStore.isLoggedIn">我的商品</router-link>
        <router-link to="/favorites" v-if="authStore.isLoggedIn">我的收藏</router-link>
        <router-link to="/notifications" v-if="authStore.isLoggedIn">消息中心<span v-if="unreadCount" class="badge">{{ unreadCount }}</span></router-link>
        <router-link to="/orders" v-if="authStore.isLoggedIn">我的订单</router-link>
        <router-link to="/admin" v-if="authStore.isAdmin">管理后台</router-link>
        <router-link to="/admin/audit" v-if="authStore.isAdmin">商品审核</router-link>
        <router-link to="/admin/goods" v-if="authStore.isAdmin">商品管理</router-link>
        <router-link to="/admin/users" v-if="authStore.isAdmin">用户管理</router-link>
        <router-link to="/admin/messages" v-if="authStore.isAdmin">留言管理</router-link>
        <router-link to="/admin/reports" v-if="authStore.isAdmin">举报管理</router-link>
        <router-link to="/admin/featured-categories" v-if="authStore.isAdmin">特色分类管理</router-link>
      </nav>

      <div class="header-actions">
        <router-link v-if="!authStore.isLoggedIn" to="/login" class="btn btn-accent">登录 / 注册</router-link>
        <router-link v-else to="/profile" class="user-chip">
          <img v-if="authStore.user?.avatar" :src="buildImageUrl(authStore.user.avatar, 'Avatar')" alt="avatar" @error="(event) => handleImageError(event, 'Avatar')" />
          <span v-else>{{ initial }}</span>
          <strong>{{ authStore.user?.nickname || authStore.user?.username }}</strong>
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { getUnreadNotificationCount } from '../api/auth'
import { useAuthStore } from '../stores/auth'
import { buildImageUrl, handleImageError } from '../utils/image'
import brandLogo from '../assets/brand-logo-generated.png'

const authStore = useAuthStore()
const unreadCount = ref(0)
const initial = computed(() => (authStore.user?.nickname || authStore.user?.username || 'U').slice(0, 1).toUpperCase())

async function fetchUnreadCount() {
  if (!authStore.isLoggedIn) {
    unreadCount.value = 0
    return
  }
  unreadCount.value = await getUnreadNotificationCount()
}

watch(() => authStore.isLoggedIn, fetchUnreadCount, { immediate: true })
onMounted(fetchUnreadCount)
</script>

<style scoped>
.header-wrap {
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(18px);
  background: rgba(246, 239, 230, 0.75);
  border-bottom: 1px solid rgba(55, 35, 20, 0.08);
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  min-height: 82px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  display: block;
  object-fit: contain;
  border-radius: 12px;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
}

.brand-subtitle {
  color: var(--muted);
  font-size: 12px;
}

.nav-links {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
}

.nav-links a {
  color: var(--muted);
  font-weight: 500;
}

.nav-links a.router-link-active {
  color: var(--text);
}

.badge {
  display: inline-flex;
  min-width: 18px;
  height: 18px;
  margin-left: 6px;
  padding: 0 5px;
  border-radius: 999px;
  align-items: center;
  justify-content: center;
  background: var(--danger);
  color: #fff;
  font-size: 12px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid var(--line);
}

.user-chip img,
.user-chip span {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  object-fit: cover;
  display: grid;
  place-items: center;
  background: rgba(212, 106, 46, 0.18);
}

@media (max-width: 960px) {
  .header-inner {
    flex-direction: column;
    align-items: flex-start;
    padding: 14px 0;
  }
}
</style>

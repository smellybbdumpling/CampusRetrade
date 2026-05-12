import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/goods/:id',
    name: 'goods-detail',
    component: () => import('../views/GoodsDetailView.vue')
  },
  {
    path: '/profile',
    name: 'profile',
    meta: { requiresAuth: true },
    component: () => import('../views/ProfileView.vue')
  },
  {
    path: '/notifications',
    name: 'notifications',
    meta: { requiresAuth: true },
    component: () => import('../views/NotificationsView.vue')
  },
  {
    path: '/report-detail/:id',
    name: 'report-detail',
    meta: { requiresAuth: true },
    component: () => import('../views/ReportDetailView.vue')
  },
  {
    path: '/users/:id',
    name: 'public-profile',
    component: () => import('../views/PublicProfileView.vue')
  },
  {
    path: '/publish',
    name: 'publish',
    meta: { requiresAuth: true },
    component: () => import('../views/PublishGoodsView.vue')
  },
  {
    path: '/my-goods',
    name: 'my-goods',
    meta: { requiresAuth: true },
    component: () => import('../views/MyGoodsView.vue')
  },
  {
    path: '/favorites',
    name: 'favorites',
    meta: { requiresAuth: true },
    component: () => import('../views/MyFavoritesView.vue')
  },
  {
    path: '/orders',
    name: 'orders',
    meta: { requiresAuth: true },
    component: () => import('../views/OrdersView.vue')
  },
  {
    path: '/admin',
    name: 'admin',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('../views/AdminDashboardView.vue')
  },
  {
    path: '/admin/audit',
    name: 'admin-audit',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('../views/AdminGoodsAuditView.vue')
  },
  {
    path: '/admin/goods',
    name: 'admin-goods',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('../views/AdminGoodsManagementView.vue')
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('../views/AdminUsersView.vue')
  },
  {
    path: '/admin/messages',
    name: 'admin-messages',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('../views/AdminGoodsMessagesView.vue')
  },
  {
    path: '/admin/reports',
    name: 'admin-reports',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('../views/AdminGoodsReportsView.vue')
  },
  {
    path: '/admin/featured-categories',
    name: 'admin-featured-categories',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('../views/AdminFeaturedCategoriesView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    return { name: 'home' }
  }
  return true
})

export default router
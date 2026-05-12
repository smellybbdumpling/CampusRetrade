<template>
  <div class="login-page">
    <div class="login-card card">
      <div>
        <span class="status-tag">JWT Login</span>
        <h1>欢迎来到 校友有点闲</h1>
        <p>{{ introCopy }}</p>
      </div>

      <div class="tabs">
        <button :class="['tab', { active: mode === 'login' }]" @click="mode = 'login'">登录</button>
        <button :class="['tab', { active: mode === 'register' }]" @click="mode = 'register'">注册</button>
      </div>

      <form class="form-grid" @submit.prevent="submit">
        <input v-model="form.username" class="input" placeholder="用户名" />
        <input v-model="form.password" type="password" class="input" placeholder="密码" />
        <input v-if="mode === 'register'" v-model="form.nickname" class="input" placeholder="昵称" />
        <input v-if="mode === 'register'" v-model="form.phone" class="input" placeholder="手机号" />
        <button class="btn btn-accent" type="submit">{{ mode === 'login' ? '立即登录' : '提交注册' }}</button>
      </form>

      <div class="tips">
        <div v-if="message" class="message">{{ message }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login, register } from '../api/auth'
import { useAuthStore } from '../stores/auth'
import { minLength, phoneOptional, required } from '../utils/validators'

const introCopies = [
  '宿舍太挤？来场卖舍离',
  '旧物不旧，换然一新',
  '不是我有点闲，是我有点闲置',
  '低价淘好物，轻松不吃土',
  '花小钱，办大事，淘好物',
  '闲置不出手，灰尘陪你久',
  '学长学姐的旧爱，学弟学妹的新欢',
  '同校交易，走两步就能见面',
  '你负责毕业，我负责帮它再就业'
]

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const mode = ref('login')
const message = ref('')
const introCopy = introCopies[Math.floor(Math.random() * introCopies.length)]
const form = reactive({ username: '', password: '', nickname: '', phone: '' })

async function submit() {
  message.value = ''
  try {
    if (mode.value === 'register') {
      required(form.username, '用户名')
      minLength(form.password, 6, '密码')
      required(form.nickname, '昵称')
      phoneOptional(form.phone)
      await register(form)
      message.value = '注册成功，请继续登录'
      mode.value = 'login'
      return
    }
    required(form.username, '用户名')
    required(form.password, '密码')
    const data = await login(form)
    authStore.setAuth(data)
    router.push(route.query.redirect || (data.role === 'ADMIN' ? '/admin' : '/'))
  } catch (error) {
    message.value = error.message || '操作失败'
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.login-card {
  width: min(520px, 100%);
  padding: 28px;
}

.login-card h1 {
  margin: 14px 0 8px;
}

.login-card p,
.tips {
  color: var(--muted);
}

.tabs {
  display: flex;
  gap: 10px;
  margin: 22px 0 18px;
}

.tab {
  flex: 1;
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.7);
  border-radius: 14px;
  padding: 10px 0;
}

.tab.active {
  background: var(--text);
  color: #fff;
}

.form-grid {
  display: grid;
  gap: 12px;
}

.message {
  color: var(--danger);
  margin-top: 10px;
}
</style>
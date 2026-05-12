import { defineStore } from 'pinia'

const TOKEN_KEY = 'campus_trade_token'
const USER_KEY = 'campus_trade_user'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  }),
  getters: {
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    setAuth(payload) {
      this.token = payload.token
      this.user = payload
      localStorage.setItem(TOKEN_KEY, payload.token)
      localStorage.setItem(USER_KEY, JSON.stringify(payload))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
})
import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import { useUiStore } from '../stores/ui'

const request = axios.create({
  baseURL: '/',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const uiStore = useUiStore()
  uiStore.startLoading()
  const token = localStorage.getItem('campus_trade_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const uiStore = useUiStore()
    uiStore.stopLoading()
    const payload = response.data
    if (payload.code !== 200) {
      uiStore.showToast(payload.message || '请求失败', 'error')
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return payload.data
  },
  (error) => {
    const uiStore = useUiStore()
    const authStore = useAuthStore()
    uiStore.stopLoading()
    let message = error?.response?.data?.message || error.message || '请求失败'
    if (message.includes('Maximum upload size exceeded') || message.includes('max-file-size') || message.includes('SizeLimitExceededException')) {
      message = '上传失败：文件大小超出限制，单个文件最大 10MB，请求总大小最大 20MB'
    }
    if (error?.response?.status === 401 || message.includes('未登录') || message.includes('令牌')) {
      authStore.logout()
    }
    uiStore.showToast(message, 'error')
    return Promise.reject(error)
  }
)

export default request
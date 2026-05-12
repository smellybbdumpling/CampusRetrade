import request from '../utils/request'

export function login(data) {
  return request.post('/api/auth/login', data)
}

export function register(data) {
  return request.post('/api/auth/register', data)
}

export function getProfile() {
  return request.get('/api/user/profile')
}

export function getPublicProfile(id) {
  return request.get(`/api/user/public/${id}`)
}

export function updateProfile(data) {
  return request.put('/api/user/profile', data)
}

export function changePassword(data) {
  return request.put('/api/user/password', data)
}

export function uploadAvatar(formData) {
  return request.post('/api/files/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getNotifications() {
  return request.get('/api/user/notifications')
}

export function getUnreadNotificationCount() {
  return request.get('/api/user/notifications/unread-count')
}

export function markNotificationRead(id) {
  return request.put(`/api/user/notifications/${id}/read`)
}

export function markAllNotificationsRead() {
  return request.put('/api/user/notifications/read-all')
}

export function deleteNotification(id) {
  return request.delete(`/api/user/notifications/${id}`)
}

export function deleteAllNotifications() {
  return request.delete('/api/user/notifications')
}
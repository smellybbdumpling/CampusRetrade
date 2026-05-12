import request from '../utils/request'

export function getUserPage(params) {
  return request.get('/api/admin/users/page', { params })
}

export function updateUserStatus(id, data) {
  return request.put(`/api/admin/users/${id}/status`, data)
}

export function getOverview() {
  return request.get('/api/admin/statistics/overview')
}

export function getTrend() {
  return request.get('/api/admin/statistics/trend')
}

export function getCategoryDistribution() {
  return request.get('/api/admin/statistics/category')
}

export function getFeaturedCategoryPage(params) {
  return request.get('/api/admin/featured-categories/page', { params })
}

export function createFeaturedCategory(data) {
  return request.post('/api/admin/featured-categories', data)
}

export function updateFeaturedCategory(id, data) {
  return request.put(`/api/admin/featured-categories/${id}`, data)
}

export function getGoodsMessagePage(params) {
  return request.get('/api/admin/goods-messages/page', { params })
}

export function deleteGoodsMessage(id) {
  return request.delete(`/api/admin/goods-messages/${id}`)
}

export function deleteAllGoodsMessages() {
  return request.delete('/api/admin/goods-messages')
}

export function getGoodsReportPage(params) {
  return request.get('/api/admin/goods-reports/page', { params })
}

export function handleGoodsReport(id, data) {
  return request.put(`/api/admin/goods-reports/${id}/handle`, data)
}
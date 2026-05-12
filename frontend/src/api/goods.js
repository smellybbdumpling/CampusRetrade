import request from '../utils/request'

export function getCategories() {
  return request.get('/api/categories')
}

export function getGoodsPage(params) {
  return request.get('/api/goods/page', { params })
}

export function getFeaturedSections() {
  return request.get('/api/goods/featured/sections')
}

export function getGoodsDetail(id) {
  return request.get(`/api/goods/${id}`)
}

export function getMyGoods() {
  return request.get('/api/goods/my')
}

export function getMyFavorites() {
  return request.get('/api/goods/favorites')
}

export function favoriteGoods(id) {
  return request.post(`/api/goods/${id}/favorite`)
}

export function unfavoriteGoods(id) {
  return request.delete(`/api/goods/${id}/favorite`)
}

export function publishGoods(data) {
  return request.post('/api/goods', data)
}

export function updateGoods(id, data) {
  return request.put(`/api/goods/${id}`, data)
}

export function updateGoodsStatus(id, data) {
  return request.put(`/api/goods/${id}/status`, data)
}

export function uploadGoodsImage(formData) {
  return request.post('/api/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getAdminGoodsPage(params) {
  return request.get('/api/goods/admin/page', { params })
}

export function auditGoods(id, data) {
  return request.put(`/api/goods/admin/${id}/audit`, data)
}

export function updateAdminGoods(id, data) {
  return request.put(`/api/goods/admin/${id}`, data)
}

export function updateAdminGoodsStatus(id, data) {
  return request.put(`/api/goods/admin/${id}/status`, data)
}

export function deleteAdminGoods(id) {
  return request.delete(`/api/goods/admin/${id}`)
}

export function assignFeaturedCategories(id, featuredCategoryIds) {
  return request.put(`/api/goods/admin/${id}/featured-categories`, featuredCategoryIds)
}

export function reportGoods(id, data) {
  return request.post(`/api/goods/${id}/report`, data)
}

export function getReportDetail(id) {
  return request.get(`/api/goods-reports/${id}`)
}
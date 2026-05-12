import request from '../utils/request'

export function getCartList() {
  return request.get('/api/cart/list')
}

export function addToCart(data) {
  return request.post('/api/cart/add', data)
}

export function removeCartItem(id) {
  return request.delete(`/api/cart/${id}`)
}

export function submitOrder(data) {
  return request.post('/api/orders/submit', data)
}

export function getBuyOrders() {
  return request.get('/api/orders/my')
}

export function getSellOrders() {
  return request.get('/api/orders/my/sell')
}

export function getBuyOrderDetail(id) {
  return request.get(`/api/orders/${id}`)
}

export function getSellOrderDetail(id) {
  return request.get(`/api/orders/sell/${id}`)
}

export function cancelOrder(id) {
  return request.put(`/api/orders/${id}/cancel`)
}

export function finishOrder(id) {
  return request.put(`/api/orders/${id}/finish`)
}

export function shipOrder(id) {
  return request.put(`/api/orders/${id}/ship`)
}
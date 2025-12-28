/**
 * 应用常量定义
 */

// API 状态码
export const API_CODE = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500
}

// 本地存储键名
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo',
  CART_DATA: 'cartData'
}

// 路由名称
export const ROUTE_NAMES = {
  HOME: 'home',
  CATEGORY: 'category',
  CART: 'cart',
  LOGIN: 'login',
  PROFILE: 'profile'
}

// 商品状态
export const GOODS_STATUS = {
  ON_SALE: 1,
  OFF_SALE: 0
}

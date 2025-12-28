import httpInstance from '@/utils/http'

/**
 * 用户相关API
 */
export const userAPI = {
  // 登录 - 密码验证在后端处理
  login: (account, password) => {
    return httpInstance({
      url: '/login',
      method: 'POST',
      data: { account, password }
    })
  },
  
  // 获取喜欢列表
  getLikeList: (limit = 4) => {
    return httpInstance({
      url: '/goods/relevant',
      params: { limit }
    })
  }
}

/**
 * 首页相关API
 */
export const homeAPI = {
  // 获取Banner
  getBanner: (distributionSite = '1') => {
    return httpInstance({
      url: '/home/banner',
      params: { distributionSite }
    })
  },
  
  // 获取新鲜好物
  getNewGoods: () => {
    return httpInstance({
      url: '/home/new'
    })
  },
  
  // 获取人气推荐
  getHotGoods: () => {
    return httpInstance({
      url: '/home/hot'
    })
  },
  
  // 获取所有商品模块
  getGoods: () => {
    return httpInstance({
      url: '/home/goods'
    })
  }
}

/**
 * 分类相关API
 */
export const categoryAPI = {
  // 获取分类数据
  getTopCategory: (id) => {
    return httpInstance({
      url: '/category',
      params: { id }
    })
  },
  
  // 获取二级分类筛选条件
  getCategoryFilter: (id) => {
    return httpInstance({
      url: '/category/sub/filter',
      params: { id }
    })
  },
  
  // 获取子分类商品
  getSubCategory: (data) => {
    return httpInstance({
      url: '/category/goods/temporary',
      method: 'POST',
      data
    })
  }
}

/**
 * 购物车相关API
 */
export const cartAPI = {
  // 加入购物车
  insertCart: (skuId, count) => {
    return httpInstance({
      url: '/member/cart',
      method: 'POST',
      data: { skuId, count }
    })
  },
  
  // 获取购物车列表
  getCartList: () => {
    return httpInstance({
      url: '/member/cart'
    })
  },
  
  // 删除购物车商品
  deleteCart: (ids) => {
    return httpInstance({
      url: '/member/cart',
      method: 'DELETE',
      data: { ids }
    })
  },
  
  // 合并购物车
  mergeCart: (data) => {
    return httpInstance({
      url: '/member/cart/merge',
      method: 'POST',
      data
    })
  }
}

/**
 * 商品相关API
 */
export const goodsAPI = {
  // 获取商品详情
  getDetail: (id) => {
    return httpInstance({
      url: '/goods',
      params: { id }
    })
  },
  
  // 获取热榜商品
  getHotGoods: (id, type, limit = 3) => {
    return httpInstance({
      url: '/goods/hot',
      params: { id, type, limit }
    })
  }
}

/**
 * 订单相关API
 */
export const orderAPI = {
  // 获取用户订单
  getUserOrder: (orderState, page = 1, pageSize = 2) => {
    const params = { page, pageSize }
    if (orderState !== undefined) {
      params.orderState = orderState
    }
    return httpInstance({
      url: '/member/order',
      params
    })
  }
}

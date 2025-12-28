/**
 * Coded by 余卓炜 202330452181
 */
import request from '@/utils/http'
// 加入购物车
export const insertCartAPI = ({ skuId, count }) => {
  return request({
    url: '/member/cart',
    method: 'POST',
    data: {
      skuId,
      count
    }
  })
}

export const findNewCartListAPI = () => {
  return request({
    url: '/member/cart',
  })
}

// 删除购物车
export const delCartAPI = (ids) => {
  return request({
    url: '/member/cart',
    method: 'DELETE',
    data: {
      ids
    }
  })
}

//合并购物车
export const mergeCartAPI=(data)=>{
  return request({
    url: '/member/cart/merge',
    method: 'POST',
    data

  })
}
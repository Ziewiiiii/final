/**
 * Coded by 余卓炜 202330452181
 */
import { defineStore } from "pinia";
import { ref } from 'vue'
import { loginAPI } from '@/apis/user'
import {useCartStore} from './cartStore'
import {mergeCartAPI} from '@/apis/cart'

export const useUserStore = defineStore('user', () => {
  // 1. 定义管理用户数据的state
  const userInfo = ref({})
  const cartStore = useCartStore()
  
  // 2. 定义获取接口数据的action函数
  const getUserInfo = async ({ account, password }) => {
    const res = await loginAPI({ account, password })
    userInfo.value = res.result
    //合并本地和用户购物车逻辑
    try {
      await mergeCartAPI(cartStore.cartList.map(item => {
        return {
          skuId: item.skuId,
          selected: item.selected,
          count: item.count
        }
      }))
      await cartStore.updateNewList()
    } catch (error) {
      console.error('购物车同步失败:', error)
    }
  }
  
  //退出清除用户信息
  const clearUserInfo = () => {
    userInfo.value = {}
    cartStore.clearCart()
  }
  // 3. 以对象的格式把state和action return
  return {
    getUserInfo,
    userInfo,
    clearUserInfo
  }
}, {
  persist: true,
})
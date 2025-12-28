/**
 * Coded by 余卓炜 202330452181
 */
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { useUserStore } from './userStore'
import { insertCartAPI,findNewCartListAPI,delCartAPI } from '@/apis/cart'


export const useCartStore = defineStore('cart', () => {
  const useStore=useUserStore()
  const isLogin = computed(() => useStore.userInfo.token)


  //获取最新购物车列表action
  const updateNewList = async () => {
    // 只有登录状态下才调用接口
    if (!isLogin.value) {
      console.log('用户未登录，跳过购物车同步')
      return
    }
    try {
      const res = await findNewCartListAPI()
      // 确保每个商品都有selected字段
      cartList.value = res.result.map(item => ({
        ...item,
        selected: item.selected !== undefined ? item.selected : true
      }))
    } catch (error) {
      console.error('获取购物车列表失败:', error)
    }
  }




  // 1. 定义state - cartList
  const cartList = ref([])
  // 2. 定义action - addCart
  const addCart = async(goods) => {
    const { skuId, count } = goods
    // 登录
    if (isLogin.value) {
      // 登录之后的加入购车逻辑
      await insertCartAPI({ skuId, count })
      updateNewList()
    } else {
      // 未登录
      const item = cartList.value.find((item) => goods.skuId === item.skuId)
      if (item) {
        // 找到了
        item.count++
      } else {
        // 没找到
        cartList.value.push(goods)
      }
    }
  }
  
  

//定义删除功能
const delCart = async (skuId) => {
     if (isLogin.value) {
      // 登录之后的加入购车逻辑
      await delCartAPI([skuId])
      updateNewList()
    } else{
      const idx = cartList.value.findIndex((item) => skuId === item.skuId)
      cartList.value.splice(idx, 1)
    }
      
  }

  //清空购物车
  const clearCart=()=>{

    cartList.value=[]
  }


//右上角计算属性
  const allCount=computed(()=>cartList.value.reduce((a,c)=>a+c.count,0))
  const allPrice=computed(()=>cartList.value.reduce((a,c)=>a+c.count*c.price,0))
//结算页面计算属性
// 3. 已选择数量
const selectedCount = computed(() => cartList.value.filter(item => item.selected).reduce((a, c) => a + c.count, 0))
// 4. 已选择商品价钱合计
const selectedPrice = computed(() => cartList.value.filter(item => item.selected).reduce((a, c) => a + c.count * c.price, 0))
// 5. 已选择的商品列表
const selectedList = computed(() => cartList.value.filter(item => item.selected))


//单选功能
   const singleCheck = (skuId, selected) => {
// 通过skuId找到要修改的那一项 然后把它的selected修改为传过来的selected
  const item = cartList.value.find((item) => item.skuId === skuId)
  item.selected = selected
}
// 全选功能action
const allCheck = (selected) => {
  // 把cartList中的每一项的selected都设置为当前的全选框状态
  cartList.value.forEach(item => item.selected = selected)
}
const isAll = computed(() => cartList.value.every((item) => item.selected))
 

  return {
    cartList,
    addCart,
    delCart,
    updateNewList,
    allCount,
    allPrice,
    singleCheck,
    allCheck,
    clearCart,
    isAll,
    selectedCount,
    selectedPrice,
    selectedList

  }
}, {
  persist: true,
})
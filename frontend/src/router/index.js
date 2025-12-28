/**
 * Coded by 余卓炜 202330452181
 */
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login/index.vue'
import Layout from '@/views/Layout/index2.vue'
import Home from '@/views/Home/index3.vue'
import Category from '@/views/Category/index4.vue'
import SubCategory from '@/views/SubCategory/index5.vue'
import Detail from '@/views/Detail/index6.vue'
import CartList from '@/views/CartList/index.vue'
import CheckOut from '@/views/CheckOut/index.vue'
import Pay from '@/views/Pay/index.vue'
import WechatPay from '@/views/WechatPay/index.vue'
import Member from '@/views/Member/index.vue'
import UserInfo from '@/views/Member/components/UserInfo.vue'
import UserOrder from '@/views/Member/components/UserOrder.vue'
import PublishProduct from '@/views/Member/components/PublishProduct.vue'
import ProductManage from '@/views/Member/components/ProductManage.vue'





const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path:'/',
      component: Layout,
      children:[
        {  //路由置空设置，当你访问父路由时，如果没有额外的子路径，就会直接渲染这个 path: "" 的子路由。
          path: '',
          component:Home
        },
        {
          path:'category/:id',
          component:Category
        },
        {
          path: 'category/sub/:id',
          component: SubCategory
        },
        {
          path: 'detail/:id',
          component: Detail

        },
        {
          path: 'cartlist',
          component: CartList
          },
        {
          path: 'checkout',
          component: CheckOut
          },
          {
          path: 'pay',
          component: Pay
          },
          {
          path: 'wechatpay',
          component: WechatPay
          },
          {
          path: 'member',
          component: Member,
          children:[
            {
              path:'user',
              component:UserInfo
            },
            {
              path:'order',
              component:UserOrder
            },
            {
              path:'publish',
              component:PublishProduct
            },
            {
              path:'products',
              component:ProductManage
            }
          ]
          },
          
      ]
    },
    {
      path:'/Login',
      component: Login
    }
  ],
  //路由行为配置项
  scrollBehavior(){
    return { top:0 }

  }
})

export default router

/**
 * Coded by 余卓炜 202330452181
 * axios基础封装
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import 'element-plus/theme-chalk/el-message.css'
import router from '@/router'
import { useUserStore } from '@/stores/userStore'
import config from '@/config'


const httpInstance=axios.create({
baseURL: config.baseURL,
timeout: config.timeout || 20000
})
//拦截器设置
// axios请求拦截器
httpInstance.interceptors.request.use(config => {
  // 1. 从pinia获取token数据
  const userStore = useUserStore()
  // 2. 按照后端的要求拼接token数据
  const token = userStore.userInfo.token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, e => Promise.reject(e))

// axios响应式拦截器
httpInstance.interceptors.response.use(res => res.data, e => {
  const useStore=useUserStore()
  
  //401失效错误处理
  //清楚本地用户数据
  //跳转到登录页
  if(e.response?.status===401){
    useStore.clearUserInfo()
    router.push('/login')
    return Promise.reject(e)
  }
  
  // 其他错误才显示消息
  ElMessage({
    type:'warning',
    message:e.response?.data?.message || '请求失败'
  })
  return Promise.reject(e)
})

export default httpInstance
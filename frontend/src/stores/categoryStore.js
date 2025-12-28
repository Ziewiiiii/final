/**
 * Coded by 余卓炜 202330452181
 */
import { ref} from 'vue'
import { defineStore } from 'pinia'
import { getCategoryAPI } from '@/apis/layout';

export const useCategoryStore = defineStore('category', () => {
  //导航列表的数据管理
  const categorylist = ref([])
const getCategory = async () => {
  const res = await getCategoryAPI()
  categorylist.value = res.result
}

return{
categorylist,
getCategory
}
})



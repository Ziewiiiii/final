//封装category轮播图相关代码

import { getTopCategoryAPI } from '@/apis/category';
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';


export function useCategory(){
const categoryData=ref({})
const route=useRoute()
const getCategory=async(id) =>{
// 如何在setup中获取路由参数 useRoute() -> route 等价于this.$route
    const res = await getTopCategoryAPI(id)
    categoryData.value = res.result
}
watch(() => route.params.id, id => getCategory(id), { immediate: true })


return {
    categoryData
}


}
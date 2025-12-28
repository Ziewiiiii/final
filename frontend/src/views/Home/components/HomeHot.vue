<script setup>
import HomePanel from './HomePanel.vue';
import { ref, computed } from 'vue';
import { getHotAPI } from '@/apis/home';
import { onMounted } from 'vue';
import { useCategoryStore } from '@/stores/categoryStore';
import { ElMessage } from 'element-plus';

const categoryStore = useCategoryStore()
const hotList = ref([])

const getHotList = async () => {
  const res = await getHotAPI()
  hotList.value = res.result
}

// 根据商品标题判断跳转的分类ID
const getCategoryIdByTitle = (title) => {
  
  const yanxuanCategory = categoryStore.categorylist.find(category => 
    category.name.includes('严选') || 
    category.name.includes('精选') ||
    category.name.includes('特惠')
  )
  
  
  const zaxiangCategory = categoryStore.categorylist.find(category => 
    category.name.includes('杂项') || 
    category.name.includes('相关') ||
    category.name.includes('综合') ||
    category.name.includes('一次集齐')
  )
  
  // 根据标题内容判断
  if (title.includes('特惠') || title.includes('优惠') || title.includes('折扣')) {
    return yanxuanCategory?.id || '1' // 默认返回第一个分类
  } else if (title.includes('爆款') || title.includes('热销') || title.includes('推荐')) {
    return yanxuanCategory?.id || '1' // 默认返回第一个分类
  } else {
    return zaxiangCategory?.id || '1' // 默认返回第一个分类
  }
}

// 获取跳转链接
const getItemLink = (item) => {
  // 检查是否是领券中心（第四个商品）
  if (item.title && item.title.includes('领券')) {
    return '#' // 返回空链接，用于显示提示
  }
  const categoryId = getCategoryIdByTitle(item.title)
  return `/category/${categoryId}`
}

// 处理领券中心点击事件
const handleCouponClick = (event) => {
  event.preventDefault() // 阻止默认跳转
  ElMessage.warning('功能正在维护中，敬请期待！')
}

onMounted(async () => {
  // 先获取分类数据
  await categoryStore.getCategory()
  // 再获取人气推荐数据
  await getHotList()
})
</script>



<template>
    
  <HomePanel title="人气推荐" sub-title="人气爆款，不容错过">
    <template #main>
   <ul class="goods-list">
      <li v-for="item in hotList" :key="item.id">
        <RouterLink 
          :to="getItemLink(item)"
          @click="item.title && item.title.includes('领券') ? handleCouponClick($event) : null"
        >
          <img v-img-lazy="item.picture" alt="">
          <p class="name">{{ item.title }}</p>
          <p class="desc">{{ item.alt }}</p>
        </RouterLink>
      </li>
    </ul>
    </template>
  </HomePanel>


</template>


<style scoped lang='scss'>
.goods-list {
  display: flex;
  justify-content: space-between;
  height: 426px;

  li {
    width: 306px;
    height: 406px;
    transition: all .5s;

    &:hover {
      transform: translate3d(0, -3px, 0);
      box-shadow: 0 3px 8px rgb(0 0 0 / 20%);
    }

    img {
      width: 306px;
      height: 306px;
    }

    p {
      font-size: 22px;
      padding-top: 12px;
      text-align: center;
    }

    .desc {
      color: #999;
      font-size: 18px;
    }
  }
}
</style>
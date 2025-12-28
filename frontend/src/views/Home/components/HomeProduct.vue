<script setup>
import HomePanel from './HomePanel.vue'
import { getGoodsAPI } from '@/apis/home'
import { onMounted, ref } from 'vue'
import GoodsItem from './GoodsItem.vue'
import { useCategoryStore } from '@/stores/categoryStore'

const categoryStore = useCategoryStore()

// 获取数据列表
const goodsProduct = ref([])
const getGoods = async () => {
  const res = await getGoodsAPI()
  goodsProduct.value = res.result
}

// 获取杂项分类ID
const getZaxiangCategoryId = () => {
  const zaxiangCategory = categoryStore.categorylist.find(category => 
    category.name.includes('杂项') || 
    category.name.includes('相关') ||
    category.name.includes('综合') ||
    category.name.includes('一次集齐')
  )
  return zaxiangCategory?.id || '1' // 默认返回第一个分类
}

// 获取分类链接
const getCategoryLink = (categoryName) => {
  if (categoryName.includes('一站买全')) {
    return `/category/${getZaxiangCategoryId()}`
  }
  // 其他分类可以保持原有逻辑或添加更多判断
  return '/'
}

onMounted(async () => {
  // 先获取分类数据
  await categoryStore.getCategory()
  // 再获取商品数据
  await getGoods()
})
</script>

<template>
  <div class="home-product">
    <HomePanel :title="cate.name" v-for="cate in goodsProduct" :key="cate.id">
      <template #main>
      <div class="box">
        <RouterLink class="cover" :to="getCategoryLink(cate.name)">
          <img :src="cate.picture" />
          <strong class="label">
            <span>{{ cate.name }}馆</span>
            <span>{{ cate.saleInfo }}</span>
          </strong>
        </RouterLink>
        <ul class="goods-list">
          <li v-for="good in cate.goods" :key="good.id">
            <GoodsItem :goods="good"/>
          </li>
        </ul>
      </div>
    </template>
    </HomePanel>
   
  </div>
</template>

<style scoped lang='scss'>
.home-product {
  background: #fff;
  margin-top: 20px;

  .sub {
    margin-bottom: 2px;

    a {
      padding: 2px 12px;
      font-size: 16px;
      border-radius: 4px;

      &:hover {
        background: $xtxColor;
        color: #fff;
      }

      &:last-child {
        margin-right: 80px;
      }
    }
  }

  .box {
    display: flex;

    .cover {
      width: 240px;
      height: 610px;
      margin-right: 10px;
      position: relative;

      img {
        width: 100%;
        height: 100%;
      }

      .label {
        width: 188px;
        height: 66px;
        display: flex;
        font-size: 18px;
        color: #fff;
        line-height: 66px;
        font-weight: normal;
        position: absolute;
        left: 0;
        top: 50%;
        transform: translate3d(0, -50%, 0);

        span {
          text-align: center;

          &:first-child {
            width: 76px;
            background: rgba(0, 0, 0, 0.9);
          }

          &:last-child {
            flex: 1;
            background: rgba(0, 0, 0, 0.7);
          }
        }
      }
    }

    .goods-list {
      width: 990px;
      display: flex;
      flex-wrap: wrap;

      li {
        width: 240px;
        height: 300px;
        margin-right: 10px;
        margin-bottom: 10px;

        &:nth-last-child(-n + 4) {
          margin-bottom: 0;
        }

        &:nth-child(4n) {
          margin-right: 0;
        }
      }
    }


  }
}
</style>
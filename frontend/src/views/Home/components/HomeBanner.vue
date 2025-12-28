<script setup>
import { getBannerAPI } from '@/apis/home'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const bannerList = ref([])

const getBanner = async () => {
  const res = await getBannerAPI()
  bannerList.value = res.result
}

// 处理轮播图点击事件
const handleBannerClick = (item) => {
  // 直接跳转到指定的商品页面
  router.push('/detail/1369155859933827074')
}


onMounted(() => getBanner())

</script>


<template>
  <div class="home-banner">
    <el-carousel height="500px">
      <el-carousel-item v-for="item in bannerList" :key="item.id">
        <div class="banner-item" @click="handleBannerClick(item)">
          <img v-img-lazy="item.imgUrl" alt="">
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>




<style scoped lang='scss'>
.home-banner {
  width: 1240px;
  height: 500px;
  position: absolute;
  left: 0;
  top: 0;
  z-index: 98;

  .banner-item {
    width: 100%;
    height: 100%;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.02);
    }

    img {
      width: 100%;
      height: 500px;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

    &:hover img {
      transform: scale(1.05);
    }
  }
}
</style>
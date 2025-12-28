<!-- Coded by 余卓炜 202330452181 -->
<script setup lang="ts">
import { RouterView } from 'vue-router';
import LayoutNav from './components/LayoutNav.vue';
import LayoutHeader from './components/LayoutHeader.vue';
import LayoutFooter from './components/LayoutFooter.vue';
import LayoutFixed from './components/LayoutFixed.vue';
import { useCategoryStore } from '@/stores/categoryStore';
import { onMounted } from 'vue';

const categoryStore=useCategoryStore()
onMounted(()=>categoryStore.getCategory())
//触发获取导航的列表
</script>

<template>
  <LayoutFixed />
 <LayoutNav />
 <LayoutHeader />
 <RouterView />
 <LayoutFooter />

</template>
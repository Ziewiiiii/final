<template>
  <div class="product-manage">
    <div class="header">
      <h2>商品管理</h2>
      <p>管理您发布的商品</p>
    </div>
    
    <div class="product-list">
      <div v-if="products.length === 0" class="empty-state">
        <el-empty description="暂无发布的商品">
          <el-button type="primary" @click="goToPublish">发布商品</el-button>
        </el-empty>
      </div>
      
      <div v-else class="products-grid">
        <div v-for="product in products" :key="product.id" class="product-card">
          <div class="product-images">
            <img 
              v-if="product.images && product.images.length > 0" 
              :src="product.images[0].url" 
              :alt="product.name"
              @error="handleImageError"
            />
            <div v-else class="no-image">暂无图片</div>
          </div>
          
          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <p class="description">{{ product.description }}</p>
            <p class="price">¥{{ product.price }}</p>
            
            <div class="product-specs">
              <h4>规格：</h4>
              <div v-for="spec in product.specs" :key="spec.name" class="spec-item">
                <span class="spec-name">{{ spec.name }}:</span>
                <span class="spec-value">{{ spec.value }}</span>
              </div>
            </div>
            
            <div v-if="product.sizes && product.sizes.length > 0" class="product-sizes">
              <h4>尺码：</h4>
              <div v-for="size in product.sizes" :key="size.name" class="size-item">
                <span class="size-name">{{ size.name }}:</span>
                <span class="size-value">{{ size.value }}</span>
              </div>
            </div>
            
            <div class="product-actions">
              <el-button type="primary" size="small" @click="editProduct(product)">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="deleteProduct(product.id)">
                删除
              </el-button>
            </div>
            
            <div class="product-meta">
              <span class="created-at">发布时间: {{ formatDate(product.createdAt) }}</span>
              <span class="status" :class="product.status">{{ getStatusText(product.status) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const products = ref([])

// 加载商品数据
const loadProducts = () => {
  const savedProducts = localStorage.getItem('publishedProducts')
  if (savedProducts) {
    products.value = JSON.parse(savedProducts)
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'published': '已发布',
    'draft': '草稿',
    'pending': '待审核'
  }
  return statusMap[status] || '未知'
}

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = '/src/assets/images/none.png'
}

// 编辑商品
const editProduct = (product) => {
  // 跳转到发布页面，并传递商品数据
  router.push({
    path: '/member/publish',
    query: { edit: product.id }
  })
}

// 删除商品
const deleteProduct = async (productId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个商品吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const index = products.value.findIndex(p => p.id === productId)
    if (index > -1) {
      products.value.splice(index, 1)
      localStorage.setItem('publishedProducts', JSON.stringify(products.value))
      ElMessage.success('商品删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

// 跳转到发布页面
const goToPublish = () => {
  router.push('/member/publish')
}

// 组件挂载时加载数据
onMounted(() => {
  loadProducts()
})
</script>

<style scoped lang="scss">
.product-manage {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  .header {
    margin-bottom: 30px;
    
    h2 {
      color: #333;
      margin-bottom: 10px;
    }
    
    p {
      color: #666;
      margin: 0;
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px 0;
  }

  .products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
  }

  .product-card {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    overflow: hidden;
    background: #fff;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }

    .product-images {
      height: 200px;
      overflow: hidden;
      position: relative;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .no-image {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: #909399;
      }
    }

    .product-info {
      padding: 20px;

      h3 {
        margin: 0 0 10px 0;
        color: #333;
        font-size: 18px;
        font-weight: 600;
      }

      .description {
        color: #666;
        font-size: 14px;
        line-height: 1.5;
        margin: 0 0 15px 0;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .price {
        font-size: 20px;
        font-weight: 600;
        color: #f56c6c;
        margin: 0 0 15px 0;
      }

      .product-specs, .product-sizes {
        margin-bottom: 15px;

        h4 {
          margin: 0 0 8px 0;
          font-size: 14px;
          color: #333;
          font-weight: 600;
        }

        .spec-item, .size-item {
          display: flex;
          margin-bottom: 4px;
          font-size: 13px;

          .spec-name, .size-name {
            color: #666;
            margin-right: 8px;
            min-width: 60px;
          }

          .spec-value, .size-value {
            color: #333;
          }
        }
      }

      .product-actions {
        margin-bottom: 15px;
        
        .el-button {
          margin-right: 10px;
        }
      }

      .product-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 12px;
        color: #999;
        border-top: 1px solid #f0f0f0;
        padding-top: 10px;

        .status {
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 11px;

          &.published {
            background: #f0f9ff;
            color: #1890ff;
          }

          &.draft {
            background: #fff7e6;
            color: #fa8c16;
          }

          &.pending {
            background: #f6ffed;
            color: #52c41a;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>

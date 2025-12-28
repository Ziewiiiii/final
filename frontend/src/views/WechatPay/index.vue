<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderAPI } from '@/apis/pay'
import { ElMessage } from 'element-plus'

const route = useRoute()
const payInfo = ref({})
const qrCodeLoaded = ref(false)

const getPayInfo = async () => {
  const res = await getOrderAPI(route.query.id)
  payInfo.value = res.result
}

// 检查支付状态
const checkPaymentStatus = () => {
  // 这里可以调用API检查支付状态
  // 如果支付成功，跳转到成功页面
  // 如果未支付，提示用户
  ElMessage.success('您已经支付完成，我们将为您尽快安排发货，请勿重复付款！')
  // 模拟检查支付状态
  // 实际项目中应该调用后端API
}

// 处理二维码图片加载成功
const handleImageLoad = () => {
  qrCodeLoaded.value = true
}

// 处理二维码图片加载失败
const handleImageError = () => {
  qrCodeLoaded.value = false
}

onMounted(() => getPayInfo())
</script>

<template>
  <div class="wechat-pay-page">
    <div class="container">
      <!-- 支付信息 -->
      <div class="pay-info">
        <span class="icon iconfont icon-queren2"></span>
        <div class="tip">
          <p>请使用微信扫码支付</p>
        </div>
      </div>
      
      <!-- 微信支付二维码区域 -->
      <div class="wechat-pay-area">
        <div class="qr-code-container">
          <h3>请扫码支付</h3>
          <div class="amount-display">
            <span class="amount-label">应付总额：</span>
            <span class="amount-value">¥{{ payInfo.totalMoney?.toFixed(2) }}</span>
          </div>
          <div class="qr-code-box">
            <!-- 这里预留位置放收款码图片 -->
            <div class="qr-code-placeholder">
              <img 
                src="/src/assets/images/wechat-qr-code.png" 
                alt="微信支付二维码"
                class="qr-code-image"
                @load="handleImageLoad"
                @error="handleImageError"
              />
              <div v-if="!qrCodeLoaded" class="qr-code-fallback">
                <i class="iconfont icon-wechat"></i>
              </div>
            </div>
          </div>
        </div>
        
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="btn btn-secondary" @click="$router.back()">
          返回修改订单
        </button>
        <button class="btn btn-primary" @click="checkPaymentStatus">
          支付完成
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.wechat-pay-page {
  margin-top: 20px;
  min-height: 80vh;
}

  .pay-info {
    background: #fff;
    display: flex;
    align-items: center;
    height: 80px;
    padding: 0 40px;
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .icon {
      font-size: 50px;
      color: #1dc779;
      margin-right: 20px;
    }

    .tip {
      flex: 1;

      p {
        margin: 0;
        font-size: 18px;
        color: #333;
      }
    }
  }

.wechat-pay-area {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  margin-bottom: 20px;

  .qr-code-container {
    h3 {
      font-size: 24px;
      color: #333;
      margin-bottom: 20px;
      font-weight: normal;
    }

    .amount-display {
      margin-bottom: 30px;
      padding: 15px 20px;
      background: #f8f9fa;
      border-radius: 8px;
      border: 1px solid #e9ecef;

      .amount-label {
        font-size: 16px;
        color: #666;
        margin-right: 10px;
      }

      .amount-value {
        font-size: 28px;
        color: $priceColor;
        font-weight: bold;
      }
    }

      .qr-code-box {
        display: inline-block;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 12px;
        border: 2px dashed #ddd;

        .qr-code-placeholder {
          position: relative;
          width: 320px;
          height: 320px;
          display: flex;
          align-items: center;
          justify-content: center;

          .qr-code-image {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            z-index: 1;
          }

        .qr-code-fallback {
          position: absolute;
          top: 0;
          left: 0;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          width: 100%;
          height: 100%;
          background: #fff;
          border-radius: 8px;
          border: 1px solid #e0e0e0;
          z-index: 0;

          .iconfont {
            font-size: 80px;
            color: #07c160;
          }
        }
      }

      .qr-tip {
        margin-top: 20px;
        color: #666;
        font-size: 14px;
      }
    }
  }

}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;

  .btn {
    padding: 12px 30px;
    border-radius: 6px;
    font-size: 16px;
    cursor: pointer;
    border: none;
    transition: all 0.3s;

    &.btn-secondary {
      background: #f5f5f5;
      color: #666;
      border: 1px solid #ddd;

      &:hover {
        background: #e8e8e8;
      }
    }

    &.btn-primary {
      background: $xtxColor;
      color: #fff;

      &:hover {
        background: darken($xtxColor, 10%);
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .wechat-pay-area {
    padding: 20px;

    .qr-code-container .qr-code-box .qr-code-placeholder {
      width: 280px;
      height: 280px;
    }

    .action-buttons {
      flex-direction: column;
      align-items: center;

      .btn {
        width: 200px;
      }
    }
  }
}
</style>

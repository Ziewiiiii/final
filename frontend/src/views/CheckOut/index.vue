<!-- Coded by 余卓炜 202330452181 -->
<script setup>
import { getCheckoutInfoAPI } from '@/apis/checkout';
import { onMounted, ref, computed } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useCartStore } from '@/stores/cartStore';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createOrderAPI } from '@/apis/checkout'

// 创建订单
const createOrder = async () => {
  try {
    // 验证地址
    if (!curAddress.value.id) {
      ElMessage.warning('请选择收货地址')
      return
    }
    
    // 验证商品
    if (cartStore.selectedCount === 0) {
      ElMessage.warning('请选择要结算的商品')
      return
    }
    
    // 记录订单总价
    const orderTotalPrice = cartStore.selectedPrice
    
    // 检查选中商品是否有selected字段
    const hasSelectedField = cartStore.selectedList.every(item => item.hasOwnProperty('selected'))
    
   
    // 验证邮箱格式（如果填写了邮箱）
    if (email.value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
      ElMessage.warning('请输入正确的邮箱地址')
      return
    }
    
    const orderData = {
      deliveryTimeType: selectedDeliveryTime.value,
      payType: selectedPayment.value,
      payChannel: selectedPayment.value,
      buyerMessage: '',
      goods: cartStore.selectedList.map(item => {
        return {
          skuId: String(item.skuId),
          count: Number(item.count)
        }
      }),
      addressId: String(curAddress.value.id),
      email: email.value || null
    }
    
    
    const res = await createOrderAPI(orderData)
    
    // 记录要删除的商品，在订单创建成功后再删除
    const selectedItems = [...cartStore.selectedList] // 创建副本避免在遍历时修改原数组
    
    const orderId = res.result.id
    ElMessage.success('订单创建成功')
    
    // 订单创建成功后，删除购物车中已选择的商品
    selectedItems.forEach(item => {
      cartStore.delCart(item.skuId)
    })
    
    // 跳转到支付页面，传递订单ID和总价
    router.push({
      path: '/pay',
      query: {
        id: orderId,
        totalPrice: orderTotalPrice
      }
    })
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败，请重试')
  }
}



const userStore = useUserStore()
const cartStore = useCartStore()
const router = useRouter()
const checkInfo = ref({})  // 订单对象
const curAddress = ref({})
const selectedAddress = ref({}) // 临时选中的地址
const showDialog = ref(false)
const showAddDialog = ref(false) // 添加地址对话框
const addAddressForm = ref({
  receiver: '',
  contact: '',
  address: '',
  isDefault: 0
})

// 配送时间选项
const deliveryTimeOptions = ref([
  { id: 1, name: '不限送货时间：周一至周日' },
  { id: 2, name: '工作日送货：周一至周五' },
  { id: 3, name: '双休日、假日送货：周六至周日' }
])
const selectedDeliveryTime = ref(1) // 默认选择第一个

// 支付方式选项
const paymentOptions = ref([
  { id: 1, name: '在线支付' },
  { id: 2, name: '货到付款' }
])
const selectedPayment = ref(1) // 默认选择在线支付

// 邮箱地址
const email = ref('')

const getCheckInfo = async()=>{
  // 检查是否已登录
  if (!userStore.userInfo.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 检查是否有选中的商品
  if (cartStore.selectedCount === 0) {
    ElMessage.warning('请先在购物车中选择要结算的商品')
    router.push('/cartlist')
    return
  }
  
  try {
    const res = await getCheckoutInfoAPI()
    
    // 直接使用接口返回的数据
    checkInfo.value = res.result
    
    if (checkInfo.value.userAddresses && checkInfo.value.userAddresses.length > 0) {
      const item = checkInfo.value.userAddresses.find(item => item.isDefault === 0)
      curAddress.value = item || checkInfo.value.userAddresses[0]
    } else {
      curAddress.value = {}
    }
  } catch (error) {
    console.error('获取结算信息失败:', error)
    ElMessage.error('获取结算信息失败，请重试')
  }
}

// 计算选中商品的总价
const totalPrice = computed(() => {
  return cartStore.selectedPrice
})

// 计算运费
const postFee = computed(() => {
  return 0
})

// 计算应付总额
const totalPayPrice = computed(() => {
  return totalPrice.value + postFee.value
})

// 选择地址
const selectAddress = (address) => {
  selectedAddress.value = address
}

// 处理地址确认
const handleAddressConfirm = () => {
  if (selectedAddress.value && Object.keys(selectedAddress.value).length > 0) {
    curAddress.value = selectedAddress.value
    showDialog.value = false
    ElMessage.success('地址切换成功')
  } else {
    ElMessage.warning('请先选择一个地址')
  }
}

// 取消选择地址
const handleAddressCancel = () => {
  selectedAddress.value = {}
  showDialog.value = false
}

// 打开添加地址对话框
const openAddDialog = () => {
  addAddressForm.value = {
    receiver: '',
    contact: '',
    address: '',
    isDefault: 0
  }
  showAddDialog.value = true
}

// 添加地址确认
const handleAddAddressConfirm = () => {
  // 简单验证
  if (!addAddressForm.value.receiver) {
    ElMessage.warning('请输入收货人')
    return
  }
  if (!addAddressForm.value.contact) {
    ElMessage.warning('请输入联系方式')
    return
  }
  if (!addAddressForm.value.address) {
    ElMessage.warning('请输入详细地址')
    return
  }
  
  // 生成新地址ID（简单使用时间戳）
  const newAddress = {
    id: Date.now().toString(),
    receiver: addAddressForm.value.receiver,
    contact: addAddressForm.value.contact,
    fullLocation: '北京市 北京市 朝阳区', // 简化处理
    address: addAddressForm.value.address,
    isDefault: addAddressForm.value.isDefault
  }
  
  // 添加到地址列表
  checkInfo.value.userAddresses.push(newAddress)
  
  // 如果设置为默认地址，设为当前地址
  if (addAddressForm.value.isDefault === 1) {
    curAddress.value = newAddress
  }
  
  ElMessage.success('地址添加成功')
  showAddDialog.value = false
}

// 添加地址取消
const handleAddAddressCancel = () => {
  showAddDialog.value = false
}

// 选择配送时间
const selectDeliveryTime = (timeId) => {
  selectedDeliveryTime.value = timeId
}

// 选择支付方式
const selectPayment = (paymentId) => {
  selectedPayment.value = paymentId
}

// 删除地址
const deleteAddress = (addressId) => {
  ElMessageBox.confirm(
    '确定要删除这个地址吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      center: true,
      customClass: 'delete-confirm-dialog',
      appendTo: document.body
    }
  ).then(() => {
    // 从地址列表中删除
    const index = checkInfo.value.userAddresses.findIndex(item => item.id === addressId)
    if (index > -1) {
      checkInfo.value.userAddresses.splice(index, 1)
      ElMessage.success('地址删除成功')
      
      // 如果删除的是当前选中的地址，清空当前地址
      if (curAddress.value.id === addressId) {
        curAddress.value = {}
      }
    }
  }).catch(() => {
    // 用户取消删除
  })
}

onMounted(async () => {
  // 先同步购物车数据到服务器
  await cartStore.updateNewList()
  // 等待一下确保数据同步完成
  await new Promise(resolve => setTimeout(resolve, 500))
  // 再获取结算信息
  await getCheckInfo()
}) 

</script>

<template>
  <div class="xtx-pay-checkout-page">
    <div class="container">
      <div class="wrapper">
        <!-- 收货地址 -->
        <h3 class="box-title">收货地址</h3>
        <div class="box-body">
          <div class="address">
            <div class="text">
              <div class="none" v-if="!curAddress">您需要先添加收货地址才可提交订单。</div>
              <ul v-else>
                <li><span>收<i />货<i />人：</span>{{ curAddress.receiver }}</li>
                <li><span>联系方式：</span>{{ curAddress.contact }}</li>
                <li><span>收货地址：</span>{{ curAddress.fullLocation }} {{ curAddress.address }}</li>
              </ul>
            </div>
             <div class="action">
               <el-button size="large" @click="showDialog = true" >切换地址</el-button>
               <el-button size="large" @click="openAddDialog" >添加地址</el-button>
             </div>
          </div>
        </div>
        <!-- 商品信息 -->
        <h3 class="box-title">商品信息</h3>
        <div class="box-body">
          <table class="goods">
            <thead>
              <tr>
                <th width="520">商品信息</th>
                <th width="170">单价</th>
                <th width="170">数量</th>
                <th width="170">小计</th>
                <th width="170">实付</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="i in cartStore.selectedList" :key="i.skuId">
                <td>
                  <a href="javascript:;" class="info">
                    <img :src="i.picture" alt="">
                    <div class="right">
                      <p>{{ i.name }}</p>
                      <p>{{ i.attrsText }}</p>
                    </div>
                  </a>
                </td>
                <td>&yen;{{ i.price }}</td>
                <td>{{ i.count }}</td>
                <td>&yen;{{ (i.price * i.count).toFixed(2) }}</td>
                <td>&yen;{{ (i.price * i.count).toFixed(2) }}</td>
              </tr>
              <tr v-if="cartStore.selectedList.length === 0">
                <td colspan="5" style="text-align: center; color: #999; padding: 40px;">
                  请先在购物车中选择要结算的商品
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <!-- 配送时间 -->
        <h3 class="box-title">配送时间</h3>
        <div class="box-body">
          <a 
            v-for="option in deliveryTimeOptions" 
            :key="option.id"
            class="my-btn" 
            :class="{ active: selectedDeliveryTime === option.id }"
            href="javascript:;"
            @click="selectDeliveryTime(option.id)"
          >
            {{ option.name }}
          </a>
        </div>
        <!-- 支付方式 -->
        <h3 class="box-title">支付方式</h3>
        <div class="box-body">
          <a 
            v-for="option in paymentOptions" 
            :key="option.id"
            class="my-btn" 
            :class="{ active: selectedPayment === option.id }"
            href="javascript:;"
            @click="selectPayment(option.id)"
          >
            {{ option.name }}
          </a>
          <span style="color:#999" v-if="selectedPayment === 2">货到付款需付5元手续费</span>
        </div>
        <!-- 金额明细 -->
        <h3 class="box-title">金额明细</h3>
        <div class="box-body">
          <div class="total">
            <dl>
              <dt>商品件数：</dt>
              <dd>{{ cartStore.selectedCount }}件</dd>
            </dl>
            <dl>
              <dt>商品总价：</dt>
              <dd>¥{{ totalPrice.toFixed(2) }}</dd>
            </dl>
            <dl>
              <dt>运<i></i>费：</dt>
              <dd>¥{{ postFee.toFixed(2) }}</dd>
            </dl>
            <dl>
              <dt>应付总额：</dt>
              <dd class="price">¥{{ totalPayPrice.toFixed(2) }}</dd>
            </dl>
          </div>
        </div>
        <!-- 邮箱地址 -->
        <h3 class="box-title">订单通知邮箱</h3>
        <div class="box-body">
          <el-input
            v-model="email"
            placeholder="请输入邮箱地址，订单提交成功后将发送确认邮件"
            size="large"
            style="max-width: 500px;"
          />
          <p style="color: #999; font-size: 12px; margin-top: 10px;">
            填写邮箱地址后，订单提交成功将自动发送订单确认邮件
          </p>
        </div>
        <!-- 提交订单 -->
        <div class="submit">
          <el-button  @click="createOrder" type="primary" size="large" >提交订单</el-button>
        </div>
      </div>
    </div>
  </div>
  <!-- 切换地址 -->
<el-dialog 
  v-model="showDialog" 
  title="切换收货地址" 
  width="30%" 
  center
>
   <div class="addressWrapper">
     <div 
       class="text item" 
       v-for="item in checkInfo.userAddresses" 
       :key="item.id"
       :class="{ active: selectedAddress.id === item.id }"
       @click="selectAddress(item)"
     >
       <ul>
         <li><span>收<i />货<i />人：</span>{{ item.receiver }}</li>
         <li><span>联系方式：</span>{{ item.contact }}</li>
         <li><span>收货地址：</span>{{ item.fullLocation + item.address }}</li>
       </ul>
       <div class="delete-btn" @click.stop="deleteAddress(item.id)">
         <i class="el-icon-close"></i>
       </div>
     </div>
     <div v-if="!checkInfo.userAddresses || checkInfo.userAddresses.length === 0" style="text-align: center; color: #999; padding: 20px;">
       暂无收货地址
     </div>
   </div>
  <template #footer>
    <span class="dialog-footer">
      <el-button @click="handleAddressCancel">取消</el-button>
      <el-button type="primary" @click="handleAddressConfirm">确定</el-button>
    </span>
  </template>
</el-dialog>

<!-- 添加地址对话框 -->
<el-dialog 
  v-model="showAddDialog" 
  title="添加收货地址" 
  width="40%" 
  center
>
  <el-form :model="addAddressForm" label-width="80px" class="add-address-form">
    <el-form-item label="收货人" required>
      <el-input v-model="addAddressForm.receiver" placeholder="请输入收货人姓名" />
    </el-form-item>
    <el-form-item label="联系方式" required>
      <el-input v-model="addAddressForm.contact" placeholder="请输入手机号码" />
    </el-form-item>
    <el-form-item label="详细地址" required>
      <el-input 
        v-model="addAddressForm.address" 
        type="textarea" 
        :rows="3"
        placeholder="请输入详细地址" 
      />
    </el-form-item>
    <el-form-item label="设为默认">
      <el-switch 
        v-model="addAddressForm.isDefault" 
        :active-value="1" 
        :inactive-value="0"
      />
    </el-form-item>
  </el-form>
  <template #footer>
    <span class="dialog-footer">
      <el-button @click="handleAddAddressCancel">取消</el-button>
      <el-button type="primary" @click="handleAddAddressConfirm">确定</el-button>
    </span>
  </template>
</el-dialog>
</template>

<style scoped lang="scss">
.xtx-pay-checkout-page {
  margin-top: 20px;

  .wrapper {
    background: #fff;
    padding: 0 20px;

    .box-title {
      font-size: 16px;
      font-weight: normal;
      padding-left: 10px;
      line-height: 70px;
      border-bottom: 1px solid #f5f5f5;
    }

    .box-body {
      padding: 20px 0;
    }
  }
}

.address {
  border: 1px solid #f5f5f5;
  display: flex;
  align-items: center;

  .text {
    flex: 1;
    min-height: 90px;
    display: flex;
    align-items: center;

    .none {
      line-height: 90px;
      color: #999;
      text-align: center;
      width: 100%;
    }

    >ul {
      flex: 1;
      padding: 20px;

      li {
        line-height: 30px;

        span {
          color: #999;
          margin-right: 5px;

          >i {
            width: 0.5em;
            display: inline-block;
          }
        }
      }
    }

    >a {
      color: $xtxColor;
      width: 160px;
      text-align: center;
      height: 90px;
      line-height: 90px;
      border-right: 1px solid #f5f5f5;
    }
  }

  .action {
    width: 420px;
    text-align: center;

    .btn {
      width: 140px;
      height: 46px;
      line-height: 44px;
      font-size: 14px;

      &:first-child {
        margin-right: 10px;
      }
    }
  }
}

.goods {
  width: 100%;
  border-collapse: collapse;
  border-spacing: 0;

  .info {
    display: flex;
    text-align: left;

    img {
      width: 70px;
      height: 70px;
      margin-right: 20px;
    }

    .right {
      line-height: 24px;

      p {
        &:last-child {
          color: #999;
        }
      }
    }
  }

  tr {
    th {
      background: #f5f5f5;
      font-weight: normal;
    }

    td,
    th {
      text-align: center;
      padding: 20px;
      border-bottom: 1px solid #f5f5f5;

      &:first-child {
        border-left: 1px solid #f5f5f5;
      }

      &:last-child {
        border-right: 1px solid #f5f5f5;
      }
    }
  }
}

.my-btn {
  width: 228px;
  height: 50px;
  border: 1px solid #e4e4e4;
  text-align: center;
  line-height: 48px;
  margin-right: 25px;
  color: #666666;
  display: inline-block;

  &.active,
  &:hover {
    border-color: $xtxColor;
  }
}

.total {
  dl {
    display: flex;
    justify-content: flex-end;
    line-height: 50px;

    dt {
      i {
        display: inline-block;
        width: 2em;
      }
    }

    dd {
      width: 240px;
      text-align: right;
      padding-right: 70px;

      &.price {
        font-size: 20px;
        color: $priceColor;
      }
    }
  }
}

.submit {
  text-align: right;
  padding: 60px;
  border-top: 1px solid #f5f5f5;
}

.addressWrapper {
  max-height: 500px;
  overflow-y: auto;
}

.text {
  flex: 1;
  min-height: 90px;
  display: flex;
  align-items: center;

  &.item {
    border: 1px solid #f5f5f5;
    margin-bottom: 10px;
    cursor: pointer;
    position: relative;
    padding-right: 40px;

    &.active,
    &:hover {
      border-color: $xtxColor;
      background: lighten($xtxColor, 50%);
    }

    >ul {
      padding: 10px;
      font-size: 14px;
      line-height: 30px;
    }

    .delete-btn {
      position: absolute;
      top: 10px;
      right: 10px;
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      color: #999;
      border-radius: 50%;
      transition: all 0.3s;

      &:hover {
        color: #ff4757;
        background: #ffe6e6;
      }

      i {
        font-size: 12px;
      }
    }
  }
}

.add-address-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .el-input, .el-textarea {
    width: 100%;
  }
}
</style>

<style>
/* 删除确认对话框样式 */
.delete-confirm-dialog {
  .el-message-box {
    width: 300px !important;
    background: #2c3e50 !important;
    border-radius: 8px !important;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3) !important;
    position: fixed !important;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-50%, -50%) !important;
    z-index: 9999 !important;
  }
  
  .el-message-box__header {
    display: none !important;
  }
  
  .el-message-box__content {
    color: #ecf0f1 !important;
    text-align: center !important;
    padding: 20px !important;
  }
  
  .el-message-box__btns {
    text-align: center !important;
    padding: 0 20px 20px !important;
  }
  
  .el-button {
    margin: 0 10px !important;
    background: #34495e !important;
    border-color: #34495e !important;
    color: #ecf0f1 !important;
  }
  
  .el-button--primary {
    background: #e74c3c !important;
    border-color: #e74c3c !important;
  }
  
  .el-button:hover {
    background: #2c3e50 !important;
    border-color: #2c3e50 !important;
  }
  
  .el-button--primary:hover {
    background: #c0392b !important;
    border-color: #c0392b !important;
  }
}

.el-overlay {
  background-color: rgba(0, 0, 0, 0.7) !important;
  z-index: 9998 !important;
}
</style>
<template>
  <div class="publish-product">
    <div class="header">
      <h2>发布商品</h2>
      <p>请填写商品信息，带*号为必填项</p>
    </div>
    
    <el-form 
      ref="formRef" 
      :model="formData" 
      :rules="rules" 
      label-width="120px"
      class="product-form"
    >
      <!-- 基本信息 -->
      <el-card class="form-section" shadow="never">
        <template #header>
          <h3>基本信息</h3>
        </template>
        
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        
        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        
        <el-form-item label="商品价格" prop="price">
          <el-input-number 
            v-model="formData.price" 
            :min="0" 
            :precision="2"
            placeholder="请输入商品价格"
          />
        </el-form-item>
      </el-card>

      <!-- 商品图片 -->
      <el-card class="form-section" shadow="never">
        <template #header>
          <h3>商品图片 <span class="required">*</span></h3>
        </template>
        
        <div class="image-upload-section">
          <div class="upload-tabs">
            <el-tabs v-model="activeTab" @tab-click="handleTabClick">
              <el-tab-pane label="本地上传" name="local">
                <el-upload
                  ref="localUploadRef"
                  :file-list="localFileList"
                  :on-change="handleLocalChange"
                  :on-remove="handleLocalRemove"
                  :before-upload="beforeUpload"
                  :auto-upload="false"
                  list-type="picture-card"
                  :limit="5"
                  accept="image/*"
                  multiple
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
              </el-tab-pane>
              
              <el-tab-pane label="URL上传" name="url">
                <div class="url-upload">
                  <div v-for="(item, index) in urlInputs" :key="index" class="url-input-item">
                    <el-input
                      v-model="item.url"
                      placeholder="请输入图片URL"
                      @blur="handleUrlBlur(index)"
                    >
                      <template #append>
                        <el-button @click="removeUrlInput(index)" :disabled="urlInputs.length <= 1">
                          删除
                        </el-button>
                      </template>
                    </el-input>
                    <div v-if="item.preview" class="url-preview">
                      <img :src="item.url" alt="预览" @error="handleImageError(index)" />
                    </div>
                  </div>
                  <el-button 
                    v-if="urlInputs.length < 5" 
                    @click="addUrlInput" 
                    type="primary" 
                    plain
                  >
                    添加图片URL
                  </el-button>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </el-card>

      <!-- 商品规格 -->
      <el-card class="form-section" shadow="never">
        <template #header>
          <h3>商品规格 <span class="required">*</span></h3>
        </template>
        
        <div class="specs-section">
          <div v-for="(spec, index) in formData.specs" :key="index" class="spec-item">
            <el-input
              v-model="spec.name"
              placeholder="规格名称"
              style="width: 200px; margin-right: 10px;"
            />
            <el-input
              v-model="spec.value"
              placeholder="规格值"
              style="width: 200px; margin-right: 10px;"
            />
            <el-button 
              @click="removeSpec(index)" 
              type="danger" 
              plain
              :disabled="formData.specs.length <= 1"
            >
              删除
            </el-button>
          </div>
          <el-button 
            v-if="formData.specs.length < 10" 
            @click="addSpec" 
            type="primary" 
            plain
          >
            添加规格
          </el-button>
        </div>
      </el-card>

      <!-- 商品尺码 -->
      <el-card class="form-section" shadow="never">
        <template #header>
          <h3>商品尺码 <span class="optional">(可选)</span></h3>
        </template>
        
        <div class="sizes-section">
          <el-switch
            v-model="enableSizes"
            active-text="启用尺码"
            inactive-text="不启用尺码"
            @change="handleSizeToggle"
          />
          
          <div v-if="enableSizes" class="sizes-list">
            <div v-for="(size, index) in formData.sizes" :key="index" class="size-item">
              <el-input
                v-model="size.name"
                placeholder="尺码名称"
                style="width: 200px; margin-right: 10px;"
              />
              <el-input
                v-model="size.value"
                placeholder="尺码值"
                style="width: 200px; margin-right: 10px;"
              />
              <el-button 
                @click="removeSize(index)" 
                type="danger" 
                plain
                :disabled="formData.sizes.length <= 1"
              >
                删除
              </el-button>
            </div>
            <el-button 
              v-if="formData.sizes.length < 10" 
              @click="addSize" 
              type="primary" 
              plain
            >
              添加尺码
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 提交按钮 -->
      <div class="form-actions">
        <el-button @click="resetForm">重置</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          发布商品
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 表单引用
const formRef = ref()
const localUploadRef = ref()

// 表单数据
const formData = reactive({
  name: '',
  description: '',
  price: null,
  images: [],
  specs: [{ name: '', value: '' }],
  sizes: [{ name: '', value: '' }]
})

// 上传相关
const activeTab = ref('local')
const localFileList = ref([])
const urlInputs = ref([{ url: '', preview: false }])
const enableSizes = ref(false)
const submitting = ref(false)

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' }
  ]
}

// 处理标签页切换
const handleTabClick = (tab) => {
  if (tab.name === 'url') {
    // 切换到URL模式时，清空本地文件
    localFileList.value = []
  } else {
    // 切换到本地模式时，清空URL输入
    urlInputs.value = [{ url: '', preview: false }]
  }
}

// 本地文件上传处理
const handleLocalChange = (file, fileList) => {
  localFileList.value = fileList
  // 将文件转换为base64存储
  if (file.raw) {
    const reader = new FileReader()
    reader.onload = (e) => {
      if (!formData.images.find(img => img.type === 'local' && img.file === file.name)) {
        formData.images.push({
          type: 'local',
          file: file.name,
          url: e.target.result,
          name: file.name
        })
      }
    }
    reader.readAsDataURL(file.raw)
  }
}

const handleLocalRemove = (file, fileList) => {
  localFileList.value = fileList
  // 从formData中移除对应的图片
  const index = formData.images.findIndex(img => img.file === file.name)
  if (index > -1) {
    formData.images.splice(index, 1)
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return false // 阻止自动上传
}

// URL输入处理
const addUrlInput = () => {
  if (urlInputs.value.length < 5) {
    urlInputs.value.push({ url: '', preview: false })
  }
}

const removeUrlInput = (index) => {
  if (urlInputs.value.length > 1) {
    urlInputs.value.splice(index, 1)
    // 从formData中移除对应的图片
    const imageIndex = formData.images.findIndex(img => img.type === 'url' && img.index === index)
    if (imageIndex > -1) {
      formData.images.splice(imageIndex, 1)
    }
  }
}

const handleUrlBlur = (index) => {
  const url = urlInputs.value[index].url
  if (url) {
    // 验证URL格式
    const urlPattern = /^https?:\/\/.+\.(jpg|jpeg|png|gif|webp)$/i
    if (urlPattern.test(url)) {
      urlInputs.value[index].preview = true
      // 添加到formData
      if (!formData.images.find(img => img.type === 'url' && img.url === url)) {
        formData.images.push({
          type: 'url',
          url: url,
          index: index
        })
      }
    } else {
      ElMessage.warning('请输入有效的图片URL')
    }
  }
}

const handleImageError = (index) => {
  ElMessage.error('图片加载失败，请检查URL是否正确')
  urlInputs.value[index].preview = false
}

// 规格管理
const addSpec = () => {
  if (formData.specs.length < 10) {
    formData.specs.push({ name: '', value: '' })
  }
}

const removeSpec = (index) => {
  if (formData.specs.length > 1) {
    formData.specs.splice(index, 1)
  }
}

// 尺码管理
const addSize = () => {
  if (formData.sizes.length < 10) {
    formData.sizes.push({ name: '', value: '' })
  }
}

const removeSize = (index) => {
  if (formData.sizes.length > 1) {
    formData.sizes.splice(index, 1)
  }
}

const handleSizeToggle = (value) => {
  if (!value) {
    formData.sizes = [{ name: '', value: '' }]
  }
}

// 表单提交
const submitForm = async () => {
  try {
    await formRef.value.validate()
    
    // 验证图片
    if (formData.images.length === 0) {
      ElMessage.error('请至少上传一张商品图片')
      return
    }
    
    // 验证规格
    const validSpecs = formData.specs.filter(spec => spec.name && spec.value)
    if (validSpecs.length === 0) {
      ElMessage.error('请至少填写一个规格')
      return
    }
    
    // 验证尺码（如果启用）
    if (enableSizes.value) {
      const validSizes = formData.sizes.filter(size => size.name && size.value)
      if (validSizes.length === 0) {
        ElMessage.error('请至少填写一个尺码')
        return
      }
    }
    
    submitting.value = true
    
    // 准备提交数据
    const submitData = {
      ...formData,
      specs: validSpecs,
      sizes: enableSizes.value ? formData.sizes.filter(size => size.name && size.value) : [],
      images: formData.images
    }
    
    // 保存到本地存储
    const savedProducts = JSON.parse(localStorage.getItem('publishedProducts') || '[]')
    const newProduct = {
      id: Date.now(),
      ...submitData,
      createdAt: new Date().toISOString(),
      status: 'published'
    }
    savedProducts.push(newProduct)
    localStorage.setItem('publishedProducts', JSON.stringify(savedProducts))
    
    ElMessage.success('商品发布成功！')
    resetForm()
    
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  formData.images = []
  formData.specs = [{ name: '', value: '' }]
  formData.sizes = [{ name: '', value: '' }]
  localFileList.value = []
  urlInputs.value = [{ url: '', preview: false }]
  enableSizes.value = false
  activeTab.value = 'local'
}

// 组件挂载时初始化
onMounted(() => {
  // 可以在这里加载已保存的商品数据
})
</script>

<style scoped lang="scss">
.publish-product {
  padding: 20px;
  max-width: 1000px;
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

  .form-section {
    margin-bottom: 30px;
    
    h3 {
      margin: 0;
      color: #333;
      
      .required {
        color: #f56c6c;
      }
      
      .optional {
        color: #909399;
        font-size: 14px;
        font-weight: normal;
      }
    }
  }

  .image-upload-section {
    .upload-tabs {
      .url-upload {
        .url-input-item {
          margin-bottom: 15px;
          
          .url-preview {
            margin-top: 10px;
            
            img {
              max-width: 200px;
              max-height: 150px;
              border-radius: 4px;
              border: 1px solid #dcdfe6;
            }
          }
        }
      }
    }
  }

  .specs-section, .sizes-section {
    .spec-item, .size-item {
      display: flex;
      align-items: center;
      margin-bottom: 15px;
    }
  }

  .sizes-section {
    .sizes-list {
      margin-top: 15px;
    }
  }

  .form-actions {
    text-align: center;
    padding: 30px 0;
    
    .el-button {
      margin: 0 10px;
      min-width: 100px;
    }
  }
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>

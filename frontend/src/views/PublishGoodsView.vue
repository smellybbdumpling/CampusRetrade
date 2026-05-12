<template>
  <MainLayout>
    <section class="container page-block">
      <div class="card form-card">
        <h2 class="section-title">发布商品</h2>
        <p class="section-desc">先上传图片，再提交商品信息。封面图会作为主图展示。</p>
        <div class="form-grid">
          <select v-model="form.categoryId" class="select">
            <option value="">选择分类</option>
            <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
          </select>
          <input v-model="form.title" class="input" placeholder="商品标题" />
          <input v-model="form.price" class="input" type="number" min="0.01" step="0.01" placeholder="价格" />
          <input v-model="form.stock" class="input" type="number" min="1" step="1" placeholder="库存" />
          <textarea v-model="form.description" class="textarea" rows="5" placeholder="商品描述"></textarea>
          <input type="file" multiple @change="handleUpload" />
          <div class="preview-grid">
            <img v-for="image in imageUrls" :key="image" :src="image" alt="preview" />
          </div>
          <button class="btn btn-accent" @click="submit">提交发布</button>
        </div>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import MainLayout from '../layouts/MainLayout.vue'
import { getCategories, publishGoods, uploadGoodsImage } from '../api/goods'
import { useUiStore } from '../stores/ui'
import { positiveNumber, required } from '../utils/validators'

const MAX_FILE_SIZE = 10 * 1024 * 1024
const ALLOWED_FILE_TYPES = ['image/jpeg', 'image/png', 'image/webp']
const categories = ref([])
const imageUrls = ref([])
const form = reactive({ categoryId: '', title: '', price: '', stock: 1, description: '' })
const uiStore = useUiStore()

async function fetchCategories() {
  categories.value = await getCategories()
}

async function handleUpload(event) {
  const files = Array.from(event.target.files || [])
  for (const file of files) {
    if (!ALLOWED_FILE_TYPES.includes(file.type)) {
      uiStore.showToast('上传失败：仅支持 jpg、jpeg、png、webp 格式图片', 'error')
      continue
    }
    if (file.size > MAX_FILE_SIZE) {
      uiStore.showToast('上传失败：单张图片不能超过 10MB', 'error')
      continue
    }
    const formData = new FormData()
    formData.append('file', file)
    const path = await uploadGoodsImage(formData)
    imageUrls.value.push(path)
  }
}

async function submit() {
  required(form.categoryId, '分类')
  required(form.title, '商品标题')
  required(form.description, '商品描述')
  positiveNumber(form.price, '商品价格')
  positiveNumber(form.stock, '商品库存')
  if (!imageUrls.value.length) {
    throw new Error('请至少上传一张商品图片')
  }
  await publishGoods({
    ...form,
    categoryId: Number(form.categoryId),
    price: Number(form.price),
    stock: Number(form.stock),
    coverImage: imageUrls.value[0],
    imageUrls: imageUrls.value
  })
  uiStore.showToast('发布成功，等待管理员审核', 'success')
  form.categoryId = ''
  form.title = ''
  form.price = ''
  form.stock = 1
  form.description = ''
  imageUrls.value = []
}

onMounted(fetchCategories)
</script>

<style scoped>
.page-block {
  padding: 30px 0 48px;
}

.form-card {
  padding: 24px;
}

.form-grid {
  display: grid;
  gap: 14px;
  margin-top: 18px;
}

.preview-grid {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.preview-grid img {
  width: 110px;
  height: 110px;
  border-radius: 16px;
  object-fit: cover;
}
</style>
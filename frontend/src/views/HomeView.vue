<template>
  <MainLayout>
    <section class="hero container">
      <div class="hero-copy">
        <span class="status-tag">Campus resale flow</span>
        <h1>欢迎来到 校友有点闲</h1>
        <p>校友有点闲是一款面向高校师生打造的便捷交易平台，致力于为校园闲置物品提供一个安全、高效、可靠的流通渠道。</p>
        <div class="hero-highlights">
          <div class="highlight-item">
            <strong>安全</strong>
            <span>校园用户场景更明确，交易路径更清晰。</span>
          </div>
          <div class="highlight-item">
            <strong>高效</strong>
            <span>发布、审核、购买、发货、收货一条链路快速完成。</span>
          </div>
          <div class="highlight-item">
            <strong>环保</strong>
            <span>让教材、数码和生活用品继续在校园里循环流动。</span>
          </div>
        </div>
      </div>
      <div class="hero-panel card">
        <div class="panel-row">
          <label>关键词</label>
          <input v-model="query.keyword" class="input" placeholder="搜索教材、耳机、自行车" />
        </div>
        <div class="panel-row">
          <label>分类</label>
          <select v-model="query.categoryId" class="select">
            <option value="">全部分类</option>
            <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
          </select>
        </div>
        <div class="hero-actions">
          <button class="btn btn-accent" @click="applyFilters">筛选商品</button>
          <button class="btn" @click="resetFilters">重置</button>
        </div>
      </div>
    </section>

    <section class="container category-section">
      <div class="section-head">
        <div>
          <h2 class="section-title">分类入口</h2>
          <p class="section-desc">从教材书籍、数码电子、学习办公、交通工具等热门分类快速进入，减少搜索成本。</p>
        </div>
      </div>
      <div class="category-grid">
        <button
          v-for="item in categories"
          :key="item.id"
          class="category-card card"
          @click="selectCategory(item.id)"
        >
          <span class="category-mark">{{ item.name.slice(0, 1) }}</span>
          <strong>{{ item.name }}</strong>
          <small>点击查看该分类在售商品</small>
        </button>
      </div>
    </section>

    <section class="container goods-section">
      <div class="section-head">
        <div>
          <h2 class="section-title">在售商品</h2>
          <p class="section-desc">首页默认展示每页 6 件商品，商品过多时通过分页切换，避免页面排版拥挤。</p>
        </div>
      </div>

      <div v-if="goods.records.length" class="goods-grid">
        <router-link v-for="item in goods.records" :key="item.id" :to="`/goods/${item.id}`" class="goods-card card">
          <img :src="normalizeImage(item.coverImage)" :alt="item.title" @error="(event) => handleImageError(event, item.title || 'Goods')" />
          <div class="goods-body">
            <span class="status-tag">{{ item.categoryName || '未分类' }}</span>
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
            <div class="goods-meta">
              <strong>￥{{ item.price }}</strong>
              <span>{{ item.sellerName }}</span>
            </div>
          </div>
        </router-link>
      </div>
      <EmptyState v-else icon="◎" title="暂无商品" description="当前筛选条件下还没有商品，试试切换分类或关键词。" />
      <PaginationBar :total="goods.total" :page-num="query.pageNum" :page-size="query.pageSize" @change="changePage" />
    </section>

    <section class="container featured-section" v-if="featuredSections.length">
      <div class="section-head">
        <div>
          <h2 class="section-title">特色专区</h2>
          <p class="section-desc">每页展示 3 件商品，商品过多时通过分页切换。</p>
        </div>
      </div>
      <div class="featured-stack">
        <div v-for="section in featuredSections" :key="section.id" class="featured-block card">
          <div class="featured-head">
            <div>
              <strong>{{ section.name }}</strong>
              <p>精选商品由管理员审核通过分配展示</p>
            </div>
          </div>
          <div v-if="getPagedFeaturedGoods(section).length" class="featured-grid">
            <router-link v-for="item in getPagedFeaturedGoods(section)" :key="item.id" :to="`/goods/${item.id}`" class="featured-item">
              <img :src="normalizeImage(item.coverImage)" :alt="item.title" @error="(event) => handleImageError(event, item.title || 'Goods')" />
              <div>
                <span class="status-tag">{{ item.categoryName }}</span>
                <h3>{{ item.title }}</h3>
                <p>￥{{ item.price }}</p>
              </div>
            </router-link>
          </div>
          <PaginationBar
            :total="section.goodsList?.length || 0"
            :page-num="featuredPageMap[section.id] || 1"
            :page-size="featuredPageSize"
            @change="changeFeaturedPage(section.id, $event)"
          />
        </div>
      </div>
    </section>

    <section class="container value-section">
      <div class="section-head">
        <div>
          <h2 class="section-title">平台优势</h2>
          <p class="section-desc">围绕校园场景，强调安全、高效和资源循环，让二手交易真正好用。</p>
        </div>
      </div>
      <div class="value-grid">
        <article class="value-card card">
          <span class="status-tag">可信身份</span>
          <h3>贴近校园关系链</h3>
          <p>面向校内用户构建交易场景，沟通和交付路径更清晰，降低陌生交易的不确定性。</p>
        </article>
        <article class="value-card card">
          <span class="status-tag">审核流程</span>
          <h3>发布后先审再上架</h3>
          <p>商品进入平台前可由管理员审核，有助于过滤无效或不合规内容，提升整体质量。</p>
        </article>
        <article class="value-card card">
          <span class="status-tag">循环利用</span>
          <h3>闲置流动起来</h3>
          <p>教材书籍、数码电子、服饰鞋包、交通工具等都可以被再次使用，让同学省钱，也让资源更环保地流转。</p>
        </article>
      </div>
    </section>

    <section class="container process-section">
      <div class="section-head">
        <div>
          <h2 class="section-title">交易流程</h2>
          <p class="section-desc">从发布到成交的主链路已经跑通，用户和管理员各司其职。</p>
        </div>
      </div>
      <div class="process-grid">
        <article class="process-card card">
          <strong>01 发布商品</strong>
          <p>卖家上传真实图片，填写标题、分类、价格、库存和描述信息。</p>
        </article>
        <article class="process-card card">
          <strong>02 后台审核</strong>
          <p>管理员在后台完成商品审核，通过后商品进入在售列表。</p>
        </article>
        <article class="process-card card">
          <strong>03 买家下单</strong>
          <p>买家浏览商品详情，加入购物车并提交订单，系统校验库存与状态。</p>
        </article>
        <article class="process-card card">
          <strong>04 发货收货</strong>
          <p>卖家发货，买家确认收货，订单状态完成闭环，交易流程结束。</p>
        </article>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import EmptyState from '../components/EmptyState.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PaginationBar from '../components/PaginationBar.vue'
import { getCategories, getFeaturedSections, getGoodsPage } from '../api/goods'
import { buildImageUrl, handleImageError } from '../utils/image'

const categories = ref([])
const featuredSections = ref([])
const goods = reactive({ total: 0, records: [] })
const query = reactive({ pageNum: 1, pageSize: 6, keyword: '', categoryId: '' })
const featuredPageMap = reactive({})
const featuredPageSize = 3

function normalizeImage(path) {
  return buildImageUrl(path, 'Goods')
}

async function fetchCategories() {
  categories.value = await getCategories()
}

async function fetchGoods() {
  const data = await getGoodsPage({ ...query, categoryId: query.categoryId || undefined })
  goods.total = data.total
  goods.records = data.records
}

async function fetchFeaturedSections() {
  featuredSections.value = await getFeaturedSections()
  for (const section of featuredSections.value) {
    if (!featuredPageMap[section.id]) {
      featuredPageMap[section.id] = 1
    }
  }
}

function changePage(page) {
  query.pageNum = page
  fetchGoods()
}

function applyFilters() {
  query.pageNum = 1
  fetchGoods()
}

function resetFilters() {
  query.keyword = ''
  query.categoryId = ''
  query.pageNum = 1
  fetchGoods()
}

function selectCategory(categoryId) {
  query.categoryId = String(categoryId)
  query.pageNum = 1
  fetchGoods()
}

function getPagedFeaturedGoods(section) {
  const pageNum = featuredPageMap[section.id] || 1
  const start = (pageNum - 1) * featuredPageSize
  return (section.goodsList || []).slice(start, start + featuredPageSize)
}

function changeFeaturedPage(sectionId, page) {
  featuredPageMap[sectionId] = page
}

onMounted(async () => {
  await Promise.all([fetchCategories(), fetchGoods(), fetchFeaturedSections()])
})
</script>

<style scoped>
.hero {
  display: grid;
  grid-template-columns: 1.3fr 0.9fr;
  gap: 24px;
  padding: 34px 0 28px;
}

.hero-copy h1 {
  font-size: clamp(36px, 5vw, 64px);
  line-height: 1.04;
  margin: 14px 0 16px;
  max-width: 680px;
}

.hero-copy p {
  max-width: 620px;
  color: var(--muted);
  font-size: 18px;
  line-height: 1.75;
}

.hero-highlights {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.highlight-item {
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 250, 244, 0.72);
  border: 1px solid rgba(55, 35, 20, 0.08);
}

.highlight-item strong {
  display: block;
  margin-bottom: 6px;
  font-size: 15px;
}

.highlight-item span {
  color: var(--muted);
  line-height: 1.6;
  font-size: 14px;
}

.hero-panel {
  padding: 22px;
  display: grid;
  gap: 14px;
  align-content: start;
}

.panel-row label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
}

.hero-actions {
  display: flex;
  gap: 10px;
}

.goods-section {
  padding-bottom: 50px;
}

.category-section,
.value-section,
.process-section,
.featured-section {
  padding-bottom: 30px;
}

.section-head {
  margin-bottom: 20px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.category-card {
  padding: 18px;
  text-align: left;
  border: none;
}

.category-card strong {
  display: block;
  margin: 14px 0 6px;
  font-size: 20px;
}

.category-card small {
  color: var(--muted);
}

.category-mark {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: rgba(212, 106, 46, 0.14);
  color: var(--accent-deep);
  font-weight: 700;
}

.value-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.value-card,
.process-card {
  padding: 20px;
}

.value-card h3 {
  margin: 14px 0 10px;
}

.value-card p,
.process-card p {
  color: var(--muted);
  line-height: 1.7;
}

.process-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.featured-stack {
  display: grid;
  gap: 18px;
}

.featured-block {
  padding: 18px;
}

.featured-head p {
  color: var(--muted);
  margin: 6px 0 0;
}

.featured-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.featured-item {
  display: grid;
  grid-template-columns: 92px 1fr;
  gap: 12px;
  padding: 12px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.65);
  border: 1px solid rgba(55, 35, 20, 0.08);
}

.featured-item img {
  width: 92px;
  height: 92px;
  border-radius: 14px;
  object-fit: cover;
}

.featured-item h3 {
  margin: 10px 0 8px;
  font-size: 16px;
}

.featured-item p {
  margin: 0;
  color: var(--accent-deep);
  font-weight: 700;
}

.process-card strong {
  display: block;
  margin-bottom: 12px;
  font-size: 18px;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.goods-card {
  overflow: hidden;
}

.goods-card img {
  width: 100%;
  height: 240px;
  object-fit: cover;
}

.goods-body {
  padding: 18px;
}

.goods-body h3 {
  margin: 12px 0 8px;
}

.goods-body p {
  color: var(--muted);
  min-height: 44px;
}

.goods-meta {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
}

@media (max-width: 960px) {
  .hero,
  .hero-highlights,
  .category-grid,
  .value-grid,
  .process-grid,
  .featured-grid,
  .goods-grid {
    grid-template-columns: 1fr;
  }
}
</style>
<template>
  <MainLayout>
    <section class="container public-profile" v-if="profile.id">
      <div class="card profile-hero">
        <div class="avatar-wrap">
          <img v-if="profile.avatar" :src="buildImageUrl(profile.avatar, 'Avatar')" alt="avatar" @error="(event) => handleImageError(event, 'Avatar')" />
          <span v-else>{{ (profile.nickname || profile.username || 'U').slice(0, 1) }}</span>
        </div>
        <div>
          <span class="status-tag">卖家资料</span>
          <h1>{{ profile.nickname || profile.username }}</h1>
          <p>{{ profile.bio || '这个用户暂未填写个人简介。' }}</p>
        </div>
      </div>

      <div class="profile-grid">
        <section class="card info-card">
          <h2>基础资料</h2>
          <div class="info-list">
            <span>学校：{{ profile.school || '未填写' }}</span>
            <span>学院：{{ profile.college || '未填写' }}</span>
            <span>专业：{{ profile.major || '未填写' }}</span>
            <span>年级：{{ profile.grade || '未填写' }}</span>
          </div>
        </section>

        <section class="card info-card">
          <h2>联系方式</h2>
          <div class="info-list">
            <span>手机号：{{ profile.phone || '未公开' }}</span>
            <span>微信号：{{ profile.wechat || '未公开' }}</span>
            <span>QQ号：{{ profile.qq || '未填写' }}</span>
            <span>邮箱：{{ profile.email || '未填写' }}</span>
          </div>
        </section>

        <section class="card info-card wide-card">
          <h2>交易偏好</h2>
          <div class="info-list two-col">
            <span>常用交易地点：{{ profile.tradeLocation || '未填写' }}</span>
            <span>常在线时间：{{ profile.onlineTime || '未填写' }}</span>
            <span>支持交易方式：{{ profile.tradeMethods || '未填写' }}</span>
            <span>是否接受议价：{{ profile.acceptBargain === false ? '不接受' : '接受' }}</span>
          </div>
        </section>

        <section class="card info-card wide-card">
          <h2>信用与成交</h2>
          <div class="info-list two-col">
            <span>发布商品数：{{ profile.publishedGoodsCount ?? 0 }}</span>
            <span>审核通过商品数：{{ profile.approvedGoodsCount ?? 0 }}</span>
            <span>买家已完成订单：{{ profile.finishedBuyOrderCount ?? 0 }}</span>
            <span>卖家已完成订单：{{ profile.finishedSellOrderCount ?? 0 }}</span>
            <span>总举报次数：{{ profile.totalReportCount ?? 0 }}</span>
            <span>有效举报次数：{{ profile.effectiveReportCount ?? 0 }}</span>
            <span>处罚次数：{{ profile.penaltyCount ?? 0 }}</span>
            <span>信用分：{{ profile.creditScore ?? 100 }} / {{ profile.creditLevel || '正常' }}</span>
          </div>
        </section>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import { getPublicProfile } from '../api/auth'
import { buildImageUrl, handleImageError } from '../utils/image'

const route = useRoute()
const profile = ref({})

async function fetchProfile() {
  profile.value = await getPublicProfile(route.params.id)
}

onMounted(fetchProfile)
</script>

<style scoped>
.public-profile {
  padding: 30px 0 48px;
}

.profile-hero {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 18px;
  align-items: center;
  padding: 24px;
  margin-bottom: 18px;
}

.profile-hero h1 {
  margin: 12px 0 8px;
}

.profile-hero p {
  margin: 0;
  color: var(--muted);
}

.avatar-wrap {
  width: 104px;
  height: 104px;
}

.avatar-wrap img,
.avatar-wrap span {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  display: grid;
  place-items: center;
  background: rgba(212, 106, 46, 0.18);
  font-size: 34px;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.info-card {
  padding: 22px;
}

.info-card h2 {
  margin: 0 0 14px;
  font-size: 22px;
}

.wide-card {
  grid-column: 1 / -1;
}

.info-list {
  display: grid;
  gap: 10px;
  color: var(--muted);
}

.info-list.two-col {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

@media (max-width: 860px) {
  .profile-hero,
  .profile-grid,
  .info-list.two-col {
    grid-template-columns: 1fr;
  }
}
</style>

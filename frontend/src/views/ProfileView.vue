<template>
  <MainLayout>
    <section class="container profile-page">
      <div class="profile-head card">
        <div class="avatar-wrap">
          <img v-if="profile.avatar" :src="buildImageUrl(profile.avatar, 'Avatar')" alt="avatar" @error="(event) => handleImageError(event, 'Avatar')" />
          <span v-else>{{ (profile.nickname || profile.username || 'U').slice(0, 1) }}</span>
        </div>
        <div class="profile-title">
          <span class="status-tag">{{ profile.role || 'USER' }}</span>
          <h1>{{ profile.nickname || profile.username }}</h1>
          <p>{{ profile.bio || '这个人还没有写简介。' }}</p>
        </div>
        <button class="btn logout-btn" @click="logout">退出登录</button>
      </div>

      <div class="profile-stack">
        <section class="card section-card">
          <div class="section-head">
            <h2 class="section-title">基础资料</h2>
            <p class="section-desc">完善校园身份信息，方便交易时建立基本信任。</p>
          </div>
          <div class="avatar-upload-row">
            <div class="avatar-wrap small">
              <img v-if="profile.avatar" :src="buildImageUrl(profile.avatar, 'Avatar')" alt="avatar" @error="(event) => handleImageError(event, 'Avatar')" />
              <span v-else>{{ (profile.nickname || profile.username || 'U').slice(0, 1) }}</span>
            </div>
            <input type="file" @change="uploadFile" />
          </div>
          <div class="form-grid two-col">
            <label class="field-label">
              昵称
              <input v-model="profile.nickname" class="input" maxlength="50" placeholder="昵称" />
            </label>
            <label class="field-label">
              性别
              <select v-model="profile.gender" class="select">
                <option value="">暂不填写</option>
                <option value="MALE">男</option>
                <option value="FEMALE">女</option>
                <option value="OTHER">其他</option>
              </select>
            </label>
            <label class="field-label full-span">
              个人简介
              <textarea v-model="profile.bio" class="textarea" rows="4" maxlength="500" placeholder="介绍一下你的交易偏好、常出没地点或校园身份"></textarea>
            </label>
            <label class="field-label">
              学校
              <input v-model="profile.school" class="input" maxlength="100" placeholder="学校" />
            </label>
            <label class="field-label">
              学院
              <input v-model="profile.college" class="input" maxlength="100" placeholder="学院" />
            </label>
            <label class="field-label">
              专业
              <input v-model="profile.major" class="input" maxlength="100" placeholder="专业" />
            </label>
            <label class="field-label">
              年级
              <input v-model="profile.grade" class="input" maxlength="50" placeholder="例如：2023级 / 大二" />
            </label>
          </div>
        </section>

        <section class="card section-card">
          <div class="section-head">
            <h2 class="section-title">联系方式</h2>
            <p class="section-desc">用于交易沟通，是否公开由隐私设置控制。</p>
          </div>
          <div class="form-grid two-col">
            <label class="field-label">
              手机号
              <input v-model="profile.phone" class="input" inputmode="numeric" maxlength="11" placeholder="11位手机号" />
            </label>
            <label class="field-label">
              微信号
              <input v-model="profile.wechat" class="input" maxlength="20" placeholder="微信号" />
            </label>
            <label class="field-label">
              QQ号
              <input v-model="profile.qq" class="input" inputmode="numeric" maxlength="12" placeholder="QQ号" />
            </label>
            <label class="field-label">
              邮箱
              <input v-model="profile.email" class="input" type="email" maxlength="100" placeholder="邮箱" />
            </label>
          </div>
        </section>

        <section class="card section-card">
          <div class="section-head">
            <h2 class="section-title">交易偏好</h2>
            <p class="section-desc">说明你更习惯的交易方式，让沟通更高效。</p>
          </div>
          <div class="form-grid two-col">
            <label class="field-label">
              常用交易地点
              <input v-model="profile.tradeLocation" class="input" maxlength="100" placeholder="例如：图书馆门口、宿舍楼下" />
            </label>
            <label class="field-label">
              常在线时间
              <input v-model="profile.onlineTime" class="input" maxlength="100" placeholder="例如：晚上 19:00-22:00" />
            </label>
            <div class="field-label full-span">
              支持交易方式
              <div class="choice-row">
                <label v-for="method in tradeMethodOptions" :key="method" class="check-option">
                  <input v-model="selectedTradeMethods" type="checkbox" :value="method" />
                  <span>{{ method }}</span>
                </label>
              </div>
            </div>
            <label class="switch-line full-span">
              <input v-model="profile.acceptBargain" type="checkbox" />
              <span>接受议价</span>
            </label>
          </div>
        </section>

        <section class="card section-card">
          <div class="section-head">
            <h2 class="section-title">信用与成交</h2>
            <p class="section-desc">查看当前账号的信用等级、成交情况和平台治理相关统计。</p>
          </div>
          <div class="credit-grid">
            <article class="credit-card">
              <span>信用分</span>
              <strong>{{ profile.creditScore ?? 100 }}</strong>
              <small>{{ profile.creditLevel || '正常' }}</small>
            </article>
            <article class="credit-card">
              <span>发布商品数</span>
              <strong>{{ profile.publishedGoodsCount ?? 0 }}</strong>
              <small>累计已发布商品</small>
            </article>
            <article class="credit-card">
              <span>审核通过商品数</span>
              <strong>{{ profile.approvedGoodsCount ?? 0 }}</strong>
              <small>当前被平台认可的商品</small>
            </article>
            <article class="credit-card">
              <span>买家已完成订单</span>
              <strong>{{ profile.finishedBuyOrderCount ?? 0 }}</strong>
              <small>已完成的购买订单</small>
            </article>
            <article class="credit-card">
              <span>卖家已完成订单</span>
              <strong>{{ profile.finishedSellOrderCount ?? 0 }}</strong>
              <small>已完成的出售订单</small>
            </article>
            <article class="credit-card">
              <span>总举报次数</span>
              <strong>{{ profile.totalReportCount ?? 0 }}</strong>
              <small>累计被举报记录</small>
            </article>
            <article class="credit-card">
              <span>有效举报次数</span>
              <strong>{{ profile.effectiveReportCount ?? 0 }}</strong>
              <small>经处理认定有效的举报</small>
            </article>
            <article class="credit-card">
              <span>处罚次数</span>
              <strong>{{ profile.penaltyCount ?? 0 }}</strong>
              <small>平台处罚或治理动作次数</small>
            </article>
          </div>
        </section>

        <section class="card section-card">
          <div class="section-head">
            <h2 class="section-title">隐私设置</h2>
            <p class="section-desc">控制联系方式展示，并在这里维护账号密码。</p>
          </div>
          <div class="privacy-grid">
            <div class="privacy-panel">
              <label class="switch-line">
                <input v-model="profile.phonePublic" type="checkbox" />
                <span>公开手机号</span>
              </label>
              <label class="switch-line">
                <input v-model="profile.wechatPublic" type="checkbox" />
                <span>公开微信号</span>
              </label>
            </div>
            <div class="password-panel">
              <h3>修改密码</h3>
              <div class="form-grid">
                <input v-model="passwordForm.oldPassword" type="password" class="input" placeholder="旧密码" />
                <input v-model="passwordForm.newPassword" type="password" class="input" placeholder="新密码" />
                <button class="btn" @click="savePassword">更新密码</button>
              </div>
            </div>
          </div>
        </section>

        <div class="save-bar card">
          <span>修改资料后记得保存。</span>
          <button class="btn btn-accent" @click="saveProfile">保存资料</button>
        </div>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import { changePassword, getProfile, updateProfile, uploadAvatar } from '../api/auth'
import { useAuthStore } from '../stores/auth'
import { emailOptional, maxLengthOptional, minLength, phoneOptional, qqOptional, required, wechatOptional } from '../utils/validators'
import { useUiStore } from '../stores/ui'
import { buildImageUrl, handleImageError } from '../utils/image'

const MAX_FILE_SIZE = 10 * 1024 * 1024
const ALLOWED_FILE_TYPES = ['image/jpeg', 'image/png', 'image/webp']
const tradeMethodOptions = ['线下面交', '校内自提', '快递', '可送到宿舍楼下']
const router = useRouter()
const authStore = useAuthStore()
const uiStore = useUiStore()
const profile = reactive({
  username: '',
  nickname: '',
  avatar: '',
  gender: '',
  bio: '',
  school: '',
  college: '',
  major: '',
  grade: '',
  phone: '',
  wechat: '',
  qq: '',
  email: '',
  tradeLocation: '',
  tradeMethods: '',
  acceptBargain: true,
  onlineTime: '',
  phonePublic: false,
  wechatPublic: false,
  role: ''
})
const passwordForm = reactive({ oldPassword: '', newPassword: '' })

const selectedTradeMethods = computed({
  get() {
    return profile.tradeMethods ? profile.tradeMethods.split(',').filter(Boolean) : []
  },
  set(value) {
    profile.tradeMethods = value.join(',')
  }
})

async function fetchProfile() {
  const data = await getProfile()
  Object.assign(profile, {
    ...data,
    acceptBargain: data.acceptBargain !== false,
    phonePublic: data.phonePublic === true,
    wechatPublic: data.wechatPublic === true
  })
  authStore.user = { ...authStore.user, ...data }
  localStorage.setItem('campus_trade_user', JSON.stringify(authStore.user))
}

async function saveProfile() {
  if (!validateProfile()) {
    return
  }
  await updateProfile({
    nickname: profile.nickname,
    gender: profile.gender,
    bio: profile.bio,
    school: profile.school,
    college: profile.college,
    major: profile.major,
    grade: profile.grade,
    phone: profile.phone,
    wechat: profile.wechat,
    qq: profile.qq,
    email: profile.email,
    tradeLocation: profile.tradeLocation,
    tradeMethods: profile.tradeMethods,
    acceptBargain: profile.acceptBargain,
    onlineTime: profile.onlineTime,
    phonePublic: profile.phonePublic,
    wechatPublic: profile.wechatPublic
  })
  uiStore.showToast('个人信息已更新', 'success')
  await fetchProfile()
}

function validateProfile() {
  try {
    required(profile.nickname, '昵称')
    phoneOptional(profile.phone)
    emailOptional(profile.email)
    qqOptional(profile.qq)
    wechatOptional(profile.wechat)
    maxLengthOptional(profile.nickname, 50, '昵称')
    maxLengthOptional(profile.bio, 500, '个人简介')
    maxLengthOptional(profile.school, 100, '学校')
    maxLengthOptional(profile.college, 100, '学院')
    maxLengthOptional(profile.major, 100, '专业')
    maxLengthOptional(profile.grade, 50, '年级')
    maxLengthOptional(profile.tradeLocation, 100, '常用交易地点')
    maxLengthOptional(profile.onlineTime, 100, '常在线时间')
    return true
  } catch (error) {
    uiStore.showToast(error.message || '请检查个人资料填写格式', 'error')
    return false
  }
}

async function savePassword() {
  try {
    required(passwordForm.oldPassword, '旧密码')
    minLength(passwordForm.newPassword, 6, '新密码')
  } catch (error) {
    uiStore.showToast(error.message || '请检查密码填写格式', 'error')
    return
  }
  await changePassword(passwordForm)
  uiStore.showToast('密码修改成功，请重新牢记新密码', 'success')
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
}

async function uploadFile(event) {
  const file = event.target.files?.[0]
  if (!file) return
  if (!ALLOWED_FILE_TYPES.includes(file.type)) {
    uiStore.showToast('上传失败：头像仅支持 jpg、jpeg、png、webp 格式', 'error')
    return
  }
  if (file.size > MAX_FILE_SIZE) {
    uiStore.showToast('上传失败：头像文件不能超过 10MB', 'error')
    return
  }
  const formData = new FormData()
  formData.append('file', file)
  const data = await uploadAvatar(formData)
  profile.avatar = data.avatar
  uiStore.showToast('头像上传成功', 'success')
  await fetchProfile()
}

function logout() {
  authStore.logout()
  uiStore.showToast('已退出当前账号', 'success')
  router.push('/login')
}

onMounted(fetchProfile)
</script>

<style scoped>
.profile-page {
  padding: 30px 0 56px;
}

.profile-head {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 18px;
  align-items: center;
  padding: 24px;
  margin-bottom: 18px;
}

.profile-title h1 {
  margin: 10px 0 8px;
  font-size: 30px;
}

.profile-title p {
  margin: 0;
  color: var(--muted);
}

.profile-stack {
  display: grid;
  gap: 18px;
}

.section-card {
  padding: 24px;
}

.section-head {
  margin-bottom: 18px;
}

.avatar-upload-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

.avatar-wrap {
  width: 104px;
  height: 104px;
}

.avatar-wrap.small {
  width: 76px;
  height: 76px;
}

.avatar-wrap img,
.avatar-wrap span {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: grid;
  place-items: center;
  object-fit: cover;
  background: rgba(212, 106, 46, 0.18);
  font-size: 34px;
}

.form-grid {
  display: grid;
  gap: 14px;
}

.two-col {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.full-span {
  grid-column: 1 / -1;
}

.field-label {
  display: grid;
  gap: 8px;
  color: var(--muted);
  font-weight: 600;
}

.choice-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.check-option,
.switch-line {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.45);
  color: var(--text);
  font-weight: 500;
}

.privacy-grid {
  display: grid;
  grid-template-columns: 0.8fr 1.2fr;
  gap: 18px;
}

.credit-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.credit-card {
  display: grid;
  gap: 8px;
  padding: 16px;
  border-radius: 18px;
  border: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.35);
}

.credit-card span,
.credit-card small {
  color: var(--muted);
}

.credit-card strong {
  font-size: 28px;
  color: var(--accent-deep);
}

.privacy-panel,
.password-panel {
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.35);
}

.privacy-panel {
  display: grid;
  gap: 12px;
  align-content: start;
}

.password-panel h3 {
  margin: 0 0 12px;
}

.save-bar {
  position: sticky;
  bottom: 16px;
  z-index: 5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
}

.save-bar span {
  color: var(--muted);
}

.logout-btn {
  background: rgba(182, 58, 58, 0.1);
  color: var(--danger);
}

@media (max-width: 860px) {
  .profile-head,
  .two-col,
  .privacy-grid,
  .credit-grid {
    grid-template-columns: 1fr;
  }

  .save-bar {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>

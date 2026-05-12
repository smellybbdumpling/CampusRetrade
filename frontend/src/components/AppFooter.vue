<template>
  <footer class="footer-wrap">
    <div class="container footer-inner">
      <div>
        <strong>Campus Re:Trade</strong>
        <p>{{ currentCopy }}</p>
      </div>
      <div class="footer-side">
        <span>Vue3 + Vite</span>
        <span>Spring Boot API</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
const STORAGE_KEY = 'campus_trade_footer_copy'

const copies = [
  '闲置不浪费，交易更实惠。',
  '校园二手交易系统，帮你轻松出售闲置、快速淘到好物。',
  '教材、台灯、自行车、数码配件……你不需要的，正是别人想要的。',
  '让每一件闲置，都找到新的主人。',
  '买卖二手，就在校园里。',
  '校园二手交易系统，为师生提供更安全、更方便、更高效的交易平台。',
  '低价淘好物，快速出闲置，资源循环利用，让校园生活更省钱、更环保。',
  '毕业季清仓、开学季捡漏。',
  '一个平台全搞定。',
  '校园二手交易系统，让闲置流动起来，让实惠看得见。'
]

let currentCopy = localStorage.getItem(STORAGE_KEY)
if (!currentCopy || !copies.includes(currentCopy)) {
  currentCopy = copies[Math.floor(Math.random() * copies.length)]
  localStorage.setItem(STORAGE_KEY, currentCopy)
}
</script>

<style scoped>
.footer-wrap {
  padding: 32px 0 48px;
}

.footer-inner {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  color: var(--muted);
}

.footer-inner p {
  margin: 6px 0 0;
  max-width: 560px;
  line-height: 1.7;
}

.footer-side {
  display: flex;
  gap: 14px;
  align-items: center;
}

@media (max-width: 768px) {
  .footer-inner {
    flex-direction: column;
  }
}
</style>
<template>
  <div class="empty-state card">
    <div class="icon">{{ icon }}</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    default: '·'
  },
  title: {
    type: String,
    default: '暂无数据'
  },
  description: {
    type: String,
    default: '当前列表还没有可展示内容'
  }
})
</script>

<style scoped>
.empty-state {
  padding: 32px 24px;
  text-align: center;
}

.icon {
  width: 52px;
  height: 52px;
  margin: 0 auto 14px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 26px;
  background: rgba(212, 106, 46, 0.12);
  color: var(--accent-deep);
}

h3 {
  margin: 0;
}

p {
  margin: 8px 0 0;
  color: var(--muted);
}
</style>
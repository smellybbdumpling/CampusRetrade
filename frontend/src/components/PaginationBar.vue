<template>
  <div class="pagination-bar" v-if="total > pageSize">
    <button class="btn" :disabled="pageNum <= 1" @click="$emit('change', pageNum - 1)">上一页</button>
    <span>第 {{ pageNum }} / {{ totalPages }} 页</span>
    <button class="btn" :disabled="pageNum >= totalPages" @click="$emit('change', pageNum + 1)">下一页</button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: {
    type: Number,
    default: 0
  },
  pageNum: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

defineEmits(['change'])

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))
</script>

<style scoped>
.pagination-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 22px;
}

.pagination-bar .btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
</style>
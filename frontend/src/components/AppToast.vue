<template>
  <transition name="toast-fade">
    <div v-if="uiStore.toast.visible" :class="['toast', uiStore.toast.type]">
      {{ uiStore.toast.message }}
    </div>
  </transition>
</template>

<script setup>
import { useUiStore } from '../stores/ui'

const uiStore = useUiStore()
</script>

<style scoped>
.toast {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 120;
  max-width: 360px;
  padding: 14px 16px;
  border-radius: 18px;
  color: #fff;
  box-shadow: 0 18px 36px rgba(0, 0, 0, 0.18);
}

.toast.info {
  background: #2b1d17;
}

.toast.success {
  background: #2f7d57;
}

.toast.error {
  background: #b63a3a;
}

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.2s ease;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
<template>
  <transition name="overlay-fade">
    <div v-if="uiStore.loading" class="overlay">
      <div class="loader-card">
        <div class="spinner"></div>
        <span>正在处理中...</span>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { useUiStore } from '../stores/ui'

const uiStore = useUiStore()
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  z-index: 110;
  display: grid;
  place-items: center;
  background: rgba(35, 22, 15, 0.18);
  backdrop-filter: blur(6px);
}

.loader-card {
  display: grid;
  place-items: center;
  gap: 12px;
  min-width: 180px;
  padding: 22px 24px;
  border-radius: 22px;
  background: rgba(255, 250, 244, 0.96);
  border: 1px solid rgba(55, 35, 20, 0.08);
}

.spinner {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 3px solid rgba(212, 106, 46, 0.18);
  border-top-color: var(--accent);
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.overlay-fade-enter-active,
.overlay-fade-leave-active {
  transition: opacity 0.2s ease;
}

.overlay-fade-enter-from,
.overlay-fade-leave-to {
  opacity: 0;
}
</style>
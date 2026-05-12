<template>
  <transition name="overlay-fade">
    <div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">
      <div class="modal-card card">
        <div class="modal-head">
          <h3>{{ title }}</h3>
          <button class="close-btn" @click="$emit('update:modelValue', false)">×</button>
        </div>
        <div class="modal-body">
          <slot />
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  }
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  padding: 24px;
  display: grid;
  place-items: center;
  background: rgba(35, 22, 15, 0.24);
  backdrop-filter: blur(8px);
}

.modal-card {
  width: min(780px, 100%);
  max-height: calc(100vh - 48px);
  overflow: auto;
  padding: 22px;
}

.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.modal-head h3 {
  margin: 0;
}

.modal-body {
  margin-top: 16px;
}

.close-btn {
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(35, 22, 15, 0.08);
  font-size: 22px;
}
</style>
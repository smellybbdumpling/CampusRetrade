<template>
  <AppLoading />
  <router-view />
  <AppToast />
</template>

<script setup>
import AppLoading from './components/AppLoading.vue'
import AppToast from './components/AppToast.vue'
</script>
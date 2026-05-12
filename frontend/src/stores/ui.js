import { defineStore } from 'pinia'

export const useUiStore = defineStore('ui', {
  state: () => ({
    loadingCount: 0,
    toast: {
      visible: false,
      type: 'info',
      message: ''
    }
  }),
  getters: {
    loading: (state) => state.loadingCount > 0
  },
  actions: {
    startLoading() {
      this.loadingCount += 1
    },
    stopLoading() {
      this.loadingCount = Math.max(0, this.loadingCount - 1)
    },
    showToast(message, type = 'info') {
      this.toast = { visible: true, type, message }
      window.clearTimeout(this._timer)
      this._timer = window.setTimeout(() => {
        this.toast.visible = false
      }, 2600)
    }
  }
})
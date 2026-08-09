<template>
  <router-view />
</template>

<script setup>
import { onMounted, onBeforeUnmount, watch } from 'vue'
import { useUserStore } from './stores/user.js'
import { useNotificationStore } from './stores/notification.js'

const userStore = useUserStore()
const notifStore = useNotificationStore()

// 用户登录后自动连接通知 WebSocket，并定时刷新未读数
watch(() => userStore.userId, (id) => {
  if (id) {
    notifStore.connectWs(id)
    notifStore.fetchUnreadCount(id)
  } else {
    notifStore.disconnectWs()
  }
})

onMounted(() => {
  if (userStore.userId) {
    notifStore.connectWs(userStore.userId)
    notifStore.fetchUnreadCount(userStore.userId)
  }
})

onBeforeUnmount(() => {
  notifStore.disconnectWs()
})
</script>

<style>
</style>

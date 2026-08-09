<template>
  <van-tabbar v-model="active" active-color="#26A65B" inactive-color="#999" route>
    <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
    <van-tabbar-item to="/my-pets" icon="like-o">爱宠</van-tabbar-item>
    <van-tabbar-item to="/profile" icon="contact-o" :badge="unreadCount || ''">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user.js'
import { useNotificationStore } from '../stores/notification.js'

const route = useRoute()
const userStore = useUserStore()
const notifStore = useNotificationStore()

const active = ref(0)

const routeMap = {
  '/home': 0,
  '/my-pets': 1,
  '/profile': 2
}

active.value = routeMap[route.path] ?? 0

// 直接从 store 计算 badge 值
const unreadCount = computed(() => {
  const n = notifStore.unreadCount
  return n > 99 ? '99+' : n || ''
})

onMounted(() => {
  if (userStore.userId) {
    notifStore.fetchUnreadCount(userStore.userId)
  }
})
</script>

<template>
  <div class="page notif-page">
    <StatusBar />
    <NavHeader title="消息通知">
      <template #right>
        <span class="read-all" @click="markAllRead" v-if="notifStore.notifications.length > 0">全部已读</span>
      </template>
    </NavHeader>

    <div v-if="loading" class="empty-wrapper">
      <van-loading size="30" color="#26A65B">加载中...</van-loading>
    </div>
    <div v-else-if="notifStore.notifications.length === 0" class="empty-wrapper">
      <div class="empty-box">
        <van-icon name="bell-o" size="50" color="#ddd" />
        <p>暂无通知</p>
      </div>
    </div>
    <div class="notif-list" v-else>
      <div class="notif-item" v-for="n in notifStore.notifications" :key="n.id" :class="{ unread: n.isRead === 0 }" @click="onClick(n)">
        <div class="notif-icon" :class="iconClass(n.type)">
          <van-icon :name="iconName(n.type)" size="20" />
        </div>
        <div class="notif-body">
          <div class="notif-header-row">
            <span class="notif-title">{{ n.title }}</span>
            <span class="notif-time">{{ formatTime(n.createdAt) }}</span>
          </div>
          <p class="notif-content">{{ n.content }}</p>
        </div>
        <div v-if="n.isRead === 0" class="notif-dot" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import { useUserStore } from '../stores/user.js'
import { useNotificationStore } from '../stores/notification.js'

const router = useRouter()
const userStore = useUserStore()
const notifStore = useNotificationStore()

const loading = ref(true)

const iconMap = { SYSTEM: 'info-o', CONSULT: 'chat-o', HEALTH: 'fire-o' }
const iconBgMap = { SYSTEM: 'bg-gray', CONSULT: 'bg-blue', HEALTH: 'bg-green' }

function iconName(type) { return iconMap[type] || 'info-o' }
function iconClass(type) { return iconBgMap[type] || 'bg-gray' }

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t), now = new Date(), diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return t.slice(0, 10)
}

function onClick(n) {
  notifStore.markRead(n.id, userStore.userId)
  if (n.type === 'CONSULT' && n.relatedId) {
    router.push('/dialog?orderNo=' + n.relatedId)
  }
}

async function markAllRead() {
  await notifStore.markAllRead(userStore.userId)
  showToast('全部已读')
}

onMounted(async () => {
  await notifStore.fetchNotifications(userStore.userId)
  loading.value = false
})
</script>

<style scoped>
.notif-page { background: #F0F8F0; min-height: 100vh; }
.empty-wrapper { display: flex; justify-content: center; padding-top: 80px; }
.empty-box { display: flex; flex-direction: column; align-items: center; gap: 12px; color: #999; font-size: 14px; }
.read-all { font-size: 12px; color: #26A65B; cursor: pointer; white-space: nowrap; }
.notif-list { padding: 0 15px; }
.notif-item {
  display: flex; align-items: flex-start; gap: 12px;
  background: #fff; border-radius: 12px; padding: 14px;
  margin-bottom: 10px; position: relative;
}
.notif-item.unread { background: #f8fff8; }
.notif-icon {
  width: 36px; height: 36px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; margin-top: 2px;
}
.notif-icon.bg-gray { background: #f0f0f0; color: #999; }
.notif-icon.bg-blue { background: #E3F2FD; color: #1976D2; }
.notif-icon.bg-green { background: #E8F5E9; color: #4CAF50; }
.notif-body { flex: 1; min-width: 0; }
.notif-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.notif-title { font-size: 14px; font-weight: 500; color: #333; }
.notif-time { font-size: 11px; color: #bbb; white-space: nowrap; }
.notif-content { font-size: 13px; color: #666; line-height: 1.4; margin: 0; }
.notif-dot {
  width: 8px; height: 8px; border-radius: 50%; background: #26A65B;
  position: absolute; top: 14px; right: 14px;
}
</style>
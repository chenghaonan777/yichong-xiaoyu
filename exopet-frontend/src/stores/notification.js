import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request.js'
import { CONFIG } from '../utils/config.js'

export const useNotificationStore = defineStore('notification', () => {
  const unreadCount = ref(0)
  const notifications = ref([])
  let ws = null
  let reconnectTimer = null

  async function fetchUnreadCount(userId) {
    try {
      const data = await request.get(`/notification/unread/${userId}`)
      unreadCount.value = data?.count ?? 0
    } catch { /* ignore */ }
  }

  async function fetchNotifications(userId, page = 1, size = 20) {
    try {
      const data = await request.get(`/notification/list/${userId}`, {
        params: { page, size }
      })
      const records = data?.records || []
      if (page === 1) {
        notifications.value = records
      } else {
        notifications.value = notifications.value.concat(records)
      }
      return { records, total: data?.total || 0 }
    } catch {
      return { records: [], total: 0 }
    }
  }

  async function markRead(notificationId, userId) {
    // 先更新本地，再同步后端
    const item = notifications.value.find(n => n.id === notificationId)
    if (item) {
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    }
    try {
      await request.put(`/notification/read/${notificationId}`, null, {
        params: { userId }
      })
    } catch { /* ignore */ }
  }

  async function markAllRead(userId) {
    // 先清本地未读，再异步通知后端（乐观更新）
    notifications.value.forEach(n => { n.isRead = 1 })
    unreadCount.value = 0
    try {
      await request.put(`/notification/read-all/${userId}`)
    } catch { /* 后端不同步不影响前端体验 */ }
  }

  function connectWs(userId) {
    if (!userId) return
    const wsUrl = `${CONFIG.WS_NOTIFICATION}?userId=${userId}`
    ws = new WebSocket(wsUrl)
    ws.onopen = () => console.log('[通知WS] 已连接')

    ws.onmessage = (event) => {
      try {
        const notification = JSON.parse(event.data)
        // 插入到列表顶部
        notifications.value.unshift(notification)
        unreadCount.value++
      } catch { /* ignore */ }
    }

    ws.onclose = () => {
      console.log('[通知WS] 已断开，3秒后重连')
      reconnectTimer = setTimeout(() => connectWs(userId), 3000)
    }

    ws.onerror = () => { ws?.close() }
  }

  function disconnectWs() {
    if (reconnectTimer) clearTimeout(reconnectTimer)
    if (ws) {
      ws.onclose = null
      ws.close()
      ws = null
    }
  }

  /** 本地添加一条通知（预约成功/失败等场景使用） */
  function addLocalNotification(title, content, type = 'SYSTEM') {
    const notif = {
      id: Date.now(),
      userId: 0,
      type,
      title,
      content,
      isRead: 0,
      createdAt: new Date().toISOString()
    }
    notifications.value.unshift(notif)
    unreadCount.value++
  }

  return {
    unreadCount, notifications,
    fetchUnreadCount, fetchNotifications,
    markRead, markAllRead,
    connectWs, disconnectWs,
    addLocalNotification
  }
})
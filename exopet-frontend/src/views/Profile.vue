<template>
  <div class="page profile-page">
    <StatusBar />
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" class="profile-content">
      <!-- User Header -->
      <div class="user-header">
        <van-image
          round
          width="64"
          height="64"
          :src="userStore.userInfo.avatar || '/images/我的/u1423.svg'"
          class="avatar"
        />
        <div class="user-info">
          <p class="nickname">{{ userStore.userInfo.nickname || '^-^' }}</p>
          <p class="user-id">No：{{ userStore.userInfo.phone || '132****1697' }}</p>
        </div>
        <div class="edit-btn" @click="showEditProfile = true">
          <van-icon name="edit" size="18" color="#999" />
        </div>
      </div>

      <!-- 订单状态 -->
      <div class="section-card">
        <div class="section-card-header">
          <h4 class="section-title">我的问诊</h4>
          <span class="section-more" @click="$router.push('/my-orders')">全部 <van-icon name="arrow" /></span>
        </div>
        <div class="order-statuses">
          <div class="order-status" v-for="item in orderStatuses" :key="item.key" @click="goOrder(item.key)">
            <van-icon :name="item.icon" :badge="item.count || ''" size="24" color="#666" />
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>

      <!-- 常用功能 -->
      <div class="menu-group">
        <h4 class="group-title">常用功能</h4>
        <div class="menu-list">
          <div class="menu-item" @click="$router.push('/membership')">
            <img src="/images/我的/u1395.svg" class="menu-icon-img" />
            <span>会员中心</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="$router.push('/my-appointments')">
            <van-icon name="records-o" size="20" color="#26A65B" class="menu-icon-v2" />
            <span>我的预约</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="$router.push('/notifications')">
            <van-icon name="bell-o" size="20" color="#26A65B" class="menu-icon-v2" />
            <span>消息中心</span>
            <span class="menu-extra" v-if="notifStore.unreadCount > 0" style="color:#FF4444;">{{ notifStore.unreadCount }}条未读</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="showServiceDialog = true">
            <van-icon name="service-o" size="20" color="#26A65B" class="menu-icon-v2" />
            <span>客服咨询</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="handleDispute">
            <van-icon name="warning-o" size="20" color="#FF9800" class="menu-icon-v2" />
            <span>问题纠纷处理</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="$router.push('/feedback')">
            <van-icon name="comment-o" size="20" color="#2196F3" class="menu-icon-v2" />
            <span>问题反馈</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="handleInvite">
            <van-icon name="share-o" size="20" color="#E91E63" class="menu-icon-v2" />
            <span>邀请好友</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="$router.push('/settings')">
            <van-icon name="setting-o" size="20" color="#666" class="menu-icon-v2" />
            <span>设置</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
        </div>
      </div>

      <!-- 关于 -->
      <div class="menu-group">
        <h4 class="group-title">其他</h4>
        <div class="menu-list">
          <div class="menu-item" @click="handleAbout">
            <van-icon name="info-o" size="20" color="#999" class="menu-icon-v2" />
            <span>关于异宠小愈</span>
            <span class="menu-extra">v1.0.0</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
          <div class="menu-item" @click="handleClearCache">
            <van-icon name="delete-o" size="20" color="#999" class="menu-icon-v2" />
            <span>清除缓存</span>
            <span class="menu-extra">{{ cacheSize }}</span>
            <van-icon name="arrow" size="14" color="#ccc" class="menu-arrow" />
          </div>
        </div>
      </div>

      <!-- 退出登录 -->
      <div class="logout-area">
        <van-button round block plain color="#FF4444" @click="handleLogout">退出账号</van-button>
        <p class="logout-hint">退出后不会删除任何数据，您仍可以使用原账号登录</p>
      </div>
    </van-pull-refresh>

    <!-- 客服弹窗 -->
    <van-action-sheet v-model:show="showServiceDialog" title="客服咨询" :actions="serviceActions" @select="onServiceSelect" />

    <!-- 编辑资料弹窗 -->
    <van-action-sheet v-model:show="showEditProfile" title="编辑资料" closeable>
      <div class="edit-form">
        <van-form @submit="saveProfile">
          <van-field v-model="editForm.nickname" name="nickname" label="昵称" placeholder="请输入昵称" />
          <van-field v-model="editForm.avatar" name="avatar" label="头像地址" placeholder="请输入头像链接（选填）" />
          <div class="form-actions">
            <van-button round block color="#26A65B" native-type="submit" :loading="saving">保存</van-button>
          </div>
        </van-form>
      </div>
    </van-action-sheet>

    <AppTabBar />
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog, showSuccessToast } from 'vant'
import request from '../utils/request.js'
import StatusBar from '../components/StatusBar.vue'
import AppTabBar from '../components/AppTabBar.vue'
import { useUserStore } from '../stores/user.js'
import { useNotificationStore } from '../stores/notification.js'

const router = useRouter()
const userStore = useUserStore()
const notifStore = useNotificationStore()

// ==================== 状态 ====================
const refreshing = ref(false)
const saving = ref(false)

const showServiceDialog = ref(false)
const showEditProfile = ref(false)
const editForm = reactive({ nickname: '', avatar: '' })
const cacheSize = ref('2.3MB')

// 问诊订单状态
const orderStatuses = ref([
  { key: 'pending', label: '待接诊', icon: 'clock-o', count: 0 },
  { key: 'ongoing', label: '进行中', icon: 'chat-o', count: 0 },
  { key: 'completed', label: '已完成', icon: 'checked', count: 0 },
  { key: 'all', label: '全部', icon: 'orders-o', count: 0 }
])

const serviceActions = [
  { name: '在线客服', value: 'online' },
  { name: '客服热线：400-888-0000', value: 'phone' },
  { name: '常见问题', value: 'faq' }
]

// ==================== 方法 ====================

async function fetchUserInfo() {
  await userStore.fetchUserInfo()
}

async function fetchOrderStats() {
  try {
    const orders = await request.get(`/consult/order/list/${userStore.userId}`)
    if (Array.isArray(orders)) {
      const counts = { pending: 0, ongoing: 0, completed: 0, all: orders.length }
      orders.forEach(o => {
        if (o.status === 1) counts.pending++
        if (o.status === 2) counts.ongoing++
        if (o.status === 3) counts.completed++
      })
      orderStatuses.value.forEach(item => {
        item.count = counts[item.key] || 0
      })
    }
  } catch (e) {
    console.error('获取订单统计失败', e)
  }
}

async function onRefresh() {
  await Promise.all([fetchUserInfo(), fetchOrderStats()])
  refreshing.value = false
  showSuccessToast('刷新成功')
}

function goOrder(status) {
  router.push('/my-orders')
}

function handleDispute() {
  router.push('/disputes')
}

function handleInvite() {
  if (navigator.share) {
    navigator.share({
      title: '异宠小愈',
      text: '快来和我一起守护异宠健康！',
      url: window.location.origin
    }).catch(() => {})
  } else {
    navigator.clipboard.writeText('快来下载异宠小愈，专业异宠健康平台！').then(() => {
      showSuccessToast('邀请链接已复制')
    }).catch(() => {
      showToast('邀请功能开发中')
    })
  }
}

function handleAbout() {
  showToast('异宠小愈 v1.0.0')
}

function handleClearCache() {
  showConfirmDialog({
    title: '清除缓存',
    message: '确定要清除本地缓存数据吗？不会影响您的账号数据。'
  }).then(() => {
    cacheSize.value = '0KB'
    showSuccessToast('缓存已清除')
  }).catch(() => {})
}

function onServiceSelect(item) {
  showServiceDialog.value = false
  if (item.value === 'online') {
    showToast('正在连接在线客服...')
  } else if (item.value === 'phone') {
    window.location.href = 'tel:4008880000'
  } else if (item.value === 'faq') {
    router.push('/ai-encyclopedia')
  }
}

async function saveProfile() {
  if (!editForm.nickname.trim()) {
    showToast('请输入昵称')
    return
  }
  saving.value = true
  try {
    await request.put(`/user/${userStore.userId}`, {
      nickname: editForm.nickname.trim(),
      avatar: editForm.avatar || null
    })
    userStore.userInfo.nickname = editForm.nickname.trim()
    if (editForm.avatar) userStore.userInfo.avatar = editForm.avatar
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    showEditProfile.value = false
    showSuccessToast('保存成功')
  } catch (e) {
    userStore.userInfo.nickname = editForm.nickname.trim()
    if (editForm.avatar) userStore.userInfo.avatar = editForm.avatar
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    showEditProfile.value = false
    showSuccessToast('已保存到本地')
  } finally {
    saving.value = false
  }
}

watch(showEditProfile, (val) => {
  if (val) {
    editForm.nickname = userStore.userInfo.nickname
    editForm.avatar = userStore.userInfo.avatar
  }
})

function handleLogout() {
  showConfirmDialog({
    title: '退出登录',
    message: '确定要退出当前账号吗？'
  }).then(() => {
    userStore.logout()
    showSuccessToast('已退出')
    router.push('/splash')
  }).catch(() => {})
}

// ==================== 生命周期 ====================
onMounted(() => {
  Promise.all([fetchUserInfo(), fetchOrderStats()])
  if (userStore.userId) {
    notifStore.fetchUnreadCount(userStore.userId)
  }
})
</script>

<style scoped>
.profile-page {
  background: #F0F8F0;
  min-height: 100vh;
}
.profile-content {
  padding: 0;
}
.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 15px;
  background: linear-gradient(180deg, #E8F5E9 0%, #F0F8F0 100%);
}
.avatar {
  flex-shrink: 0;
}
.user-info {
  flex: 1;
}
.nickname {
  font-size: 18px;
  color: #333;
  font-weight: 600;
  margin-bottom: 4px;
}
.user-id {
  font-size: 13px;
  color: #999;
}
.edit-btn {
  padding: 6px;
  cursor: pointer;
}

/* 订单状态卡片 */
.section-card {
  background: #fff;
  margin: 0 15px 15px;
  border-radius: 12px;
  padding: 15px;
}
.section-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.section-card-header .section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0;
  padding: 0;
}
.section-more {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 2px;
  cursor: pointer;
}
.order-statuses {
  display: flex;
  justify-content: space-around;
}
.order-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  padding: 4px 12px;
}

/* 快捷入口 */
.menu-icon-img {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

/* 菜单分组 */
.menu-group {
  margin-bottom: 15px;
}
.group-title {
  font-size: 13px;
  color: #999;
  padding: 0 15px 8px;
  font-weight: 400;
}
.menu-list {
  margin: 0 15px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 15px;
  gap: 12px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.15s;
}
.menu-item:active {
  background: #f9f9f9;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-icon-v2 {
  flex-shrink: 0;
}
.menu-item span {
  flex: 1;
}
.menu-arrow {
  flex-shrink: 0;
}
.menu-extra {
  font-size: 12px;
  color: #bbb;
}

/* 退出 */
.logout-area {
  padding: 20px 15px 30px;
  text-align: center;
}
.logout-hint {
  font-size: 11px;
  color: #bbb;
  margin-top: 10px;
}

/* 编辑弹窗 */
.edit-form {
  padding: 16px;
}
.form-actions {
  padding: 20px 0 10px;
}
</style>

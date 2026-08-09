<template>
  <div class="page orders-page">
    <StatusBar />
    <div class="orders-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>我的问诊</h3>
    </div>

    <!-- Loading -->
    <div class="loading-box" v-if="loading">
      <van-loading size="30" color="#26A65B">加载中...</van-loading>
    </div>

    <van-tabs v-else v-model:active="activeTab" color="#26A65B" title-active-color="#26A65B" sticky>
      <van-tab v-for="tab in tabs" :key="tab.key" :title="tab.label">
        <div class="order-list" v-if="filteredOrders.length > 0">
          <div class="order-card" v-for="order in filteredOrders" :key="order.id"
            @click="goOrder(order)">
            <div class="order-card-header">
              <span class="order-no">{{ order.orderNo }}</span>
              <span class="order-status" :class="statusClass(order.status)">
                {{ statusLabel(order.status) }}
              </span>
            </div>
            <div class="order-card-body">
              <p class="order-type">{{ typeLabel(order.type) }}</p>
              <p class="order-desc" v-if="order.symptomDesc">{{ order.symptomDesc }}</p>
            </div>
            <div class="order-card-footer">
              <span class="order-time">{{ formatTime(order.createdAt) }}</span>
              <span class="order-amount" v-if="order.amount">￥{{ order.amount }}</span>
            </div>
          </div>
        </div>
        <div class="empty-state" v-else>
          <van-icon name="orders-o" size="50" color="#ddd" />
          <p>{{ emptyText(tab.key) }}</p>
        </div>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request.js'
import StatusBar from '../components/StatusBar.vue'
import { useUserStore } from '../stores/user.js'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref(0)
const loading = ref(true)

const tabs = [
  { key: 'all', label: '全部' },
  { key: 'pending', label: '待接诊' },
  { key: 'ongoing', label: '进行中' },
  { key: 'completed', label: '已完成' }
]

const allOrders = ref([])

// 后端订单 status: 0=待支付, 1=待接诊, 2=问诊中, 3=已完成, 4=已取消
const statusMap = { pending: 1, ongoing: 2, completed: 3 }

const filteredOrders = computed(() => {
  if (tabs[activeTab.value].key === 'all') return allOrders.value
  return allOrders.value.filter(o => o.status === statusMap[tabs[activeTab.value].key])
})

// ==================== API ====================
async function fetchOrders() {
  loading.value = true
  try {
    const data = await request.get(`/consult/order/list/${userStore.userId}`)
    if (Array.isArray(data)) {
      allOrders.value = data
    }
  } catch (e) {
    console.error('获取订单列表失败', e)
  } finally {
    loading.value = false
  }
}

// ==================== 工具方法 ====================
const typeLabel = (t) => {
  const map = { 1: 'AI问诊', 2: '专人问诊', 3: '视频问诊', 4: '急诊问诊' }
  return map[t] || '问诊'
}

const statusLabel = (s) => {
  const map = { 0: '待支付', 1: '待接诊', 2: '问诊中', 3: '已完成', 4: '已取消' }
  return map[s] || '未知'
}

const statusClass = (s) => {
  const map = { 0: 's-pay', 1: 's-pending', 2: 's-ongoing', 3: 's-completed', 4: 's-cancel' }
  return map[s] || ''
}

const emptyText = (key) => {
  const map = { all: '暂无问诊记录', pending: '暂无待接诊订单', ongoing: '暂无进行中的问诊', completed: '暂无已完成的问诊' }
  return map[key] || '暂无数据'
}

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ')
}

function goOrder(order) {
  if (order.orderNo) {
    router.push('/dialog?orderNo=' + order.orderNo)
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-page { background: #F0F8F0; min-height: 100vh; }
.orders-header { display: flex; align-items: center; padding: 8px 15px; gap: 10px; }
.orders-header h3 { font-size: 18px; color: #333; margin: 0; }
.back-btn { display: flex; align-items: center; justify-content: center; width: 30px; height: 30px; cursor: pointer; }

.order-list { padding: 15px; display: flex; flex-direction: column; gap: 10px; }
.order-card {
  background: #fff; border-radius: 12px; padding: 15px;
  cursor: pointer; border: 1px solid #f0f0f0;
}
.order-card:active { background: #f9f9f9; }
.order-card-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;
  padding-bottom: 10px; border-bottom: 1px solid #f5f5f5;
}
.order-no { font-size: 13px; color: #666; font-weight: 500; }
.order-status { font-size: 12px; padding: 2px 10px; border-radius: 10px; }
.s-pending { background: #FFF3E0; color: #FF9800; }
.s-ongoing { background: #E3F2FD; color: #1976D2; }
.s-completed { background: #E8F5E9; color: #4CAF50; }
.s-cancel { background: #F5F5F5; color: #999; }

.order-card-body { font-size: 13px; }
.order-type { color: #333; font-weight: 500; margin-bottom: 2px; }
.order-desc { color: #666; font-size: 12px; line-height: 1.4; }

.order-card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.order-time { font-size: 11px; color: #bbb; }
.order-amount { font-size: 15px; color: #FF4444; font-weight: 600; }

.empty-state { display: flex; flex-direction: column; align-items: center; padding: 80px 0; gap: 10px; color: #bbb; font-size: 14px; }
.loading-box { display: flex; justify-content: center; padding: 80px 0; }
.s-pay { background: #FCE4EC; color: #E91E63; }
</style>

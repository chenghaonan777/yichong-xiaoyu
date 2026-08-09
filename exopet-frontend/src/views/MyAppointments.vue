<template>
  <div class="page appointments-page">
    <StatusBar />
    <NavHeader title="我的预约" />
    <PageState :loading="loading" :empty="appointments.length === 0 && !loading" empty-icon="records-o" empty-text="暂无预约记录">
      <div class="appt-list">
        <div class="appt-card" v-for="a in appointments" :key="a.id">
          <div class="appt-header">
            <span class="appt-hospital">医院ID: {{ a.hospitalId }}</span>
            <span class="appt-status" :class="statusClass(a.status)">{{ statusLabel(a.status) }}</span>
          </div>
          <div class="appt-body">
            <p class="appt-date">📅 {{ a.appointDate }} {{ a.timeSlot }}</p>
            <p class="appt-contact" v-if="a.contactName">👤 {{ a.contactName }} {{ a.contactPhone }}</p>
            <p class="appt-remark" v-if="a.remark">📝 {{ a.remark }}</p>
          </div>
          <div class="appt-footer">
            <span class="appt-time">预约时间：{{ formatTime(a.createdAt) }}</span>
          </div>
        </div>
      </div>
    </PageState>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import PageState from '../components/PageState.vue'
import request from '../utils/request.js'
import { useUserStore } from '../stores/user.js'

const userStore = useUserStore()
const loading = ref(true)
const appointments = ref([])

const statusMap = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }
function statusLabel(s) { return statusMap[s] || '未知' }
function statusClass(s) {
  const map = { 0: 's-pending', 1: 's-confirmed', 2: 's-done', 3: 's-cancel' }
  return map[s] || ''
}
function formatTime(t) { return t ? t.replace('T', ' ') : '' }

onMounted(async () => {
  try {
    const result = await request.get('/hospital/appointment/list/' + userStore.userId, { params: { page: 1, size: 20 } })
    appointments.value = result?.records || []
  } catch { appointments.value = [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.appointments-page { background: #F0F8F0; min-height: 100vh; }
.appt-list { padding: 15px; display: flex; flex-direction: column; gap: 10px; }
.appt-card { background: #fff; border-radius: 12px; padding: 15px; border: 1px solid #f0f0f0; }
.appt-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; padding-bottom: 10px; border-bottom: 1px solid #f5f5f5; }
.appt-hospital { font-size: 14px; color: #333; font-weight: 500; }
.appt-status { font-size: 11px; padding: 2px 10px; border-radius: 10px; }
.s-pending { background: #FFF3E0; color: #FF9800; }
.s-confirmed { background: #E3F2FD; color: #1976D2; }
.s-done { background: #E8F5E9; color: #4CAF50; }
.s-cancel { background: #F5F5F5; color: #999; }
.appt-body { font-size: 13px; color: #666; margin-bottom: 8px; }
.appt-body p { margin: 0 0 4px; }
.appt-footer { font-size: 11px; color: #bbb; }
</style>
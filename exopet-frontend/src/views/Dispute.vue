<template>
  <div class="page dispute-page">
    <StatusBar />
    <div class="dispute-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>问题纠纷处理</h3>
    </div>

    <!-- Tab 切换 -->
    <van-tabs v-model:active="activeTab" color="#26A65B" title-active-color="#26A65B">
      <van-tab title="处理中">
        <div class="dispute-list" v-if="pendingList.length > 0">
          <div class="dispute-card" v-for="item in pendingList" :key="item.id"
            @click="viewDispute(item)">
            <div class="card-header">
              <span class="dispute-no">{{ item.disputeNo }}</span>
              <span class="status-badge pending">{{ item.statusLabel }}</span>
            </div>
            <div class="card-body">
              <p class="dispute-type">{{ typeLabel(item.disputeType) }}</p>
              <p class="dispute-desc">{{ item.description }}</p>
              <div class="card-footer">
                <span class="dispute-time">{{ item.createTime }}</span>
                <span class="dispute-progress">查看处理进度 ></span>
              </div>
            </div>
          </div>
        </div>
        <div class="empty-state" v-else>
          <van-icon name="orders-o" size="50" color="#ddd" />
          <p>暂无处理中的纠纷</p>
        </div>
      </van-tab>

      <van-tab title="已解决">
        <div class="dispute-list" v-if="resolvedList.length > 0">
          <div class="dispute-card" v-for="item in resolvedList" :key="item.id"
            @click="viewDispute(item)">
            <div class="card-header">
              <span class="dispute-no">{{ item.disputeNo }}</span>
              <span class="status-badge resolved">{{ item.statusLabel }}</span>
            </div>
            <div class="card-body">
              <p class="dispute-type">{{ typeLabel(item.disputeType) }}</p>
              <p class="dispute-desc">{{ item.description }}</p>
              <div class="card-footer">
                <span class="dispute-time">{{ item.resolveTime }}</span>
                <span class="dispute-progress resolved-text">查看详情 ></span>
              </div>
            </div>
          </div>
        </div>
        <div class="empty-state" v-else>
          <van-icon name="checked" size="50" color="#ddd" />
          <p>暂无已解决的纠纷</p>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 底部发起按钮 -->
    <div class="submit-bar">
      <van-button round block color="#FF9800" icon="add-o" @click="showForm = true">发起纠纷申诉</van-button>
      <p class="submit-tip">如对问诊服务不满意，可在此发起纠纷申诉</p>
    </div>

    <!-- ========== 发起纠纷弹窗 ========== -->
    <van-action-sheet v-model:show="showForm" title="发起纠纷申诉" closeable>
      <div class="form-wrap">
        <van-form @submit="submitDispute">
          <!-- 选择关联订单 -->
          <van-field
            v-model="disputeForm.orderLabel"
            is-link readonly name="order" label="关联订单"
            placeholder="请选择关联的问诊订单"
            :rules="[{ required: true, message: '请选择关联订单' }]"
            @click="showOrderPicker = true"
          />
          <!-- 纠纷类型 -->
          <van-field
            v-model="disputeForm.disputeTypeLabel"
            is-link readonly name="disputeType" label="纠纷类型"
            placeholder="请选择纠纷类型"
            :rules="[{ required: true, message: '请选择纠纷类型' }]"
            @click="showTypePicker2 = true"
          />
          <!-- 问题描述 -->
          <van-field
            v-model="disputeForm.description"
            rows="3" type="textarea" maxlength="300"
            name="description" label="问题描述"
            placeholder="请详细描述您遇到的问题..."
            show-word-limit
            :rules="[{ required: true, message: '请描述问题' }]"
          />
          <!-- 上传凭证 -->
          <div class="upload-section">
            <p class="upload-label">上传凭证（选填，最多3张）</p>
            <van-uploader v-model="disputeForm.images" multiple :max-count="3"
              :after-read="afterReadFile" />
          </div>
          <!-- 联系方式 -->
          <van-field
            v-model="disputeForm.contact"
            name="contact" label="联系方式"
            placeholder="请输入手机号，方便客服联系您"
          />
          <div class="form-actions">
            <van-button round block color="#FF9800" native-type="submit" :loading="submitting">提交申诉</van-button>
          </div>
        </van-form>
      </div>
    </van-action-sheet>

    <!-- 选择订单弹窗 -->
    <van-action-sheet v-model:show="showOrderPicker" title="选择关联订单" :actions="orderPickerActions" @select="onOrderSelect" />

    <!-- 选择纠纷类型弹窗 -->
    <van-action-sheet v-model:show="showTypePicker2" title="选择纠纷类型" :actions="disputeTypes" @select="onTypeSelect" />

    <!-- ========== 纠纷详情弹窗 ========== -->
    <van-action-sheet v-model:show="showDetail" title="纠纷详情" closeable>
      <div class="detail-wrap" v-if="currentDispute">
        <div class="detail-header">
          <span class="detail-no">{{ currentDispute.disputeNo }}</span>
          <span class="status-badge" :class="currentDispute.status === 0 ? 'pending' : 'resolved'">
            {{ currentDispute.statusLabel }}
          </span>
        </div>
        <div class="detail-row">
          <span class="detail-label">纠纷类型</span>
          <span class="detail-value">{{ typeLabel(currentDispute.disputeType) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">关联订单</span>
          <span class="detail-value">{{ currentDispute.orderNo || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">问题描述</span>
          <span class="detail-value">{{ currentDispute.description }}</span>
        </div>
        <div class="detail-row" v-if="currentDispute.images && currentDispute.images.length > 0">
          <span class="detail-label">上传凭证</span>
          <div class="detail-images">
            <img v-for="(img, idx) in currentDispute.images" :key="idx" :src="img" class="detail-img" />
          </div>
        </div>
        <div class="detail-row">
          <span class="detail-label">提交时间</span>
          <span class="detail-value">{{ currentDispute.createTime }}</span>
        </div>
        <div class="detail-row" v-if="currentDispute.status === 1">
          <span class="detail-label">解决时间</span>
          <span class="detail-value">{{ currentDispute.resolveTime }}</span>
        </div>
        <div class="detail-row" v-if="currentDispute.reply">
          <span class="detail-label">平台回复</span>
          <span class="detail-value reply-text">{{ currentDispute.reply }}</span>
        </div>
      </div>
    </van-action-sheet>

    <AppTabBar />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { showToast, showSuccessToast } from 'vant'
import request from '../utils/request.js'
import StatusBar from '../components/StatusBar.vue'
import AppTabBar from '../components/AppTabBar.vue'
import { useUserStore } from '../stores/user.js'

const userStore = useUserStore()
const activeTab = ref(0)
const showForm = ref(false)
const showOrderPicker = ref(false)
const showTypePicker2 = ref(false)
const showDetail = ref(false)
const submitting = ref(false)
const currentDispute = ref(null)

const STORAGE_KEY = 'disputes'
function loadDisputes() {
  try { return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]') } catch (e) { return [] }
}
function saveDisputes(list) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(list))
}

const disputeList = ref(loadDisputes())

const pendingList = computed(() => disputeList.value.filter(d => d.status === 0))
const resolvedList = computed(() => disputeList.value.filter(d => d.status === 1))

const disputeTypes = [
  { name: '服务态度问题', value: 'attitude' },
  { name: '诊断质量存疑', value: 'diagnosis' },
  { name: '费用争议', value: 'fee' },
  { name: '医生未及时响应', value: 'no_response' },
  { name: '其他问题', value: 'other' }
]

const typeLabel = (t) => {
  const map = { attitude: '服务态度问题', diagnosis: '诊断质量存疑', fee: '费用争议', no_response: '医生未及时响应', other: '其他问题' }
  return map[t] || t
}

const realOrders = ref([])
const orderPickerActions = computed(() => {
  if (realOrders.value.length === 0) {
    return [{ name: '暂无问诊订单，请先去问诊', value: '', disabled: true }]
  }
  return realOrders.value.map(o => ({
    name: `${o.orderNo} — ${typeLabel2(o.type)}`,
    value: o.orderNo
  }))
})

async function fetchOrdersForPicker() {
  try {
    const data = await request.get(`/consult/order/list/${userStore.userId}`)
    if (Array.isArray(data)) {
      realOrders.value = data
    }
  } catch (e) {
    realOrders.value = []
  }
}

const typeLabel2 = (t) => {
  const map = { 1: 'AI问诊', 2: '专人问诊', 3: '视频问诊', 4: '急诊问诊' }
  return map[t] || '问诊'
}

const disputeForm = reactive({
  orderNo: '',
  orderLabel: '',
  disputeType: '',
  disputeTypeLabel: '',
  description: '',
  images: [],
  contact: ''
})

function onOrderSelect(item) {
  disputeForm.orderNo = item.value
  disputeForm.orderLabel = item.name
  showOrderPicker.value = false
}

function onTypeSelect(item) {
  disputeForm.disputeType = item.value
  disputeForm.disputeTypeLabel = item.name
  showTypePicker2.value = false
}

function afterReadFile(file) {
  // 模拟上传：将文件转为 base64 预览
  // 实际对接后端时需要上传到 OSS
}

function submitDispute() {
  if (!disputeForm.orderNo || !disputeForm.disputeType || !disputeForm.description.trim()) {
    showToast('请填写完整信息')
    return
  }
  submitting.value = true

  const now = new Date()
  const ts = now.toISOString().slice(0, 19).replace('T', ' ')
  const newDispute = {
    id: Date.now(),
    disputeNo: 'DP' + now.getFullYear() + String(now.getMonth() + 1).padStart(2, '0') + String(now.getDate()).padStart(2, '0') + String(Math.random()).slice(2, 6),
    orderNo: disputeForm.orderNo,
    disputeType: disputeForm.disputeType,
    description: disputeForm.description.trim(),
    images: disputeForm.images.map(f => f.content || f.url || ''),
    contact: disputeForm.contact,
    status: 0,
    statusLabel: '处理中',
    createTime: ts,
    resolveTime: '',
    reply: ''
  }

  disputeList.value.unshift(newDispute)
  saveDisputes(disputeList.value)

  disputeForm.orderNo = ''
  disputeForm.orderLabel = ''
  disputeForm.disputeType = ''
  disputeForm.disputeTypeLabel = ''
  disputeForm.description = ''
  disputeForm.images = []
  disputeForm.contact = ''
  showForm.value = false
  submitting.value = false
  activeTab.value = 0
  showSuccessToast('申诉已提交，请耐心等待处理')
}

function viewDispute(item) {
  currentDispute.value = item
  showDetail.value = true
}

onMounted(fetchOrdersForPicker)
</script>

<style scoped>
.dispute-page { background: #F0F8F0; min-height: 100vh; padding-bottom: 80px; }
.dispute-header { display: flex; align-items: center; padding: 8px 15px; gap: 10px; }
.dispute-header h3 { font-size: 18px; color: #333; margin: 0; }
.back-btn { display: flex; align-items: center; justify-content: center; width: 30px; height: 30px; cursor: pointer; }

/* 列表 */
.dispute-list { padding: 15px; display: flex; flex-direction: column; gap: 10px; }
.dispute-card {
  background: #fff; border-radius: 12px; padding: 15px; cursor: pointer;
  border: 1px solid #f0f0f0;
}
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.dispute-no { font-size: 12px; color: #999; }
.status-badge { font-size: 11px; padding: 2px 10px; border-radius: 10px; }
.status-badge.pending { background: #FFF3E0; color: #FF9800; }
.status-badge.resolved { background: #E8F5E9; color: #4CAF50; }

.card-body { font-size: 13px; }
.dispute-type { color: #333; font-weight: 500; margin-bottom: 4px; }
.dispute-desc { color: #666; line-height: 1.5; margin-bottom: 8px;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.dispute-time { font-size: 11px; color: #bbb; }
.dispute-progress { font-size: 11px; color: #FF9800; }
.resolved-text { color: #4CAF50; }

/* 空状态 */
.empty-state { display: flex; flex-direction: column; align-items: center; padding: 80px 0; gap: 10px; color: #bbb; font-size: 14px; }

/* 底部提交 */
.submit-bar { padding: 15px; }
.submit-tip { text-align: center; font-size: 11px; color: #bbb; margin-top: 10px; }

/* 表单 */
.form-wrap { padding: 16px; }
.upload-section { padding: 10px 16px; }
.upload-label { font-size: 13px; color: #999; margin-bottom: 8px; }
.form-actions { padding: 20px 0 10px; }

/* 详情 */
.detail-wrap { padding: 16px; max-height: 60vh; overflow-y: auto; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #f0f0f0; }
.detail-no { font-size: 14px; color: #333; font-weight: 500; }
.detail-row { margin-bottom: 14px; }
.detail-label { font-size: 12px; color: #999; display: block; margin-bottom: 4px; }
.detail-value { font-size: 14px; color: #333; line-height: 1.5; }
.reply-text { background: #FFF8E1; padding: 10px; border-radius: 8px; color: #795548; }
.detail-images { display: flex; gap: 8px; flex-wrap: wrap; }
.detail-img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; }
</style>

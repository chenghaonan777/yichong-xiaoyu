<template>
  <div class="page dialog-page">
    <StatusBar />
    <NavHeader :title="'问诊对话 - ' + (order?.orderNo?.slice(-6) || '')" />
    <PageState :loading="loading" :empty="false">
      <!-- 订单信息卡片（仅在有订单时显示） -->
      <div class="order-info" v-if="order">
        <div class="order-row">
          <span class="order-label">订单编号：</span>
          <span class="order-value">{{ order.orderNo }}</span>
        </div>
        <div class="order-row">
          <span class="order-label">问诊类型：</span>
          <span class="order-value">{{ typeLabel(order.type) }}</span>
        </div>
        <div class="order-row" v-if="order.amount">
          <span class="order-label">金额</span>
          <span class="order-price">￥{{ order.amount }}</span>
        </div>
      </div>

      <!-- Connected Badge -->
      <div class="connected-badge">
        <img src="/images/对话框/u485.svg" class="connected-img" />
        <span class="ws-status" :class="wsConnected ? 'online' : 'offline'">
          {{ wsConnected ? '已连接' : '连接中...' }}
        </span>
      </div>

      <!-- Chat Area -->
      <div class="chat-messages" ref="chatRef">
        <div class="message" v-for="msg in messages" :key="msg.id"
          :class="msg.senderType === 1 ? 'user' : 'ai'">
          <div class="msg-avatar" v-if="msg.senderType === 2">医</div>
          <div class="msg-bubble" :class="{ 'user-bubble': msg.senderType === 1 }">
            <template v-if="msg.msgType === 2 && msg.image">
              <img :src="msg.image" class="msg-image" @click="showImagePreview([msg.image])" />
            </template>
            <template v-else>{{ msg.content }}</template>
          </div>
        </div>
      </div>

      <!-- 图片预览 -->
      <div class="pending-preview" v-if="dialogPendingPreview" @click="showImagePreview([dialogPendingPreview])">
        <img :src="dialogPendingPreview" />
        <van-icon name="close" class="preview-close" @click.stop="dialogPendingPreview='';dialogPendingImage=null" />
      </div>
      <!-- Input Bar -->
      <div class="input-bar">
        <div class="upload-btn" @click="dialogImageInputRef?.click()">
          <van-icon name="photo-o" size="22" color="#666" />
        </div>
        <input type="file" ref="dialogImageInputRef" accept="image/*" style="display:none" @change="onDialogImageSelected" />
        <van-field v-model="newMessage" placeholder="输入消息..." @keypress.enter="sendMessage" />
        <van-button round color="#26A65B" size="small" @click="sendMessage" :disabled="!wsConnected">发送</van-button>
      </div>
    </PageState>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import request from '../utils/request.js'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import PageState from '../components/PageState.vue'
import { CONFIG } from '../utils/config.js'

const route = useRoute()
const order = ref(null)
const messages = ref([])
const newMessage = ref('')
const loading = ref(true)
const wsConnected = ref(false)
const chatRef = ref(null)
const orderId = ref(null)
const orderNo = route.query.orderNo || ''
const dialogPendingImage = ref(null)
const dialogPendingPreview = ref('')
const dialogImageInputRef = ref(null)
let ws = null
let reconnectTimer = null

const typeLabel = (type) => {
  const map = { 1: 'AI问诊', 2: '真人图文', 3: '真人视频', 4: '急诊' }
  return map[type] || '问诊'
}

async function fetchData() {
  // 先连 WebSocket（不管订单存不存在）
  connectWs()
  if (!orderNo) {
    loading.value = false
    return
  }
  try {
    const orderData = await request.get(`/consult/order/${orderNo}`).catch(() => null)
    order.value = orderData
    if (orderData?.id) {
      orderId.value = orderData.id
      const msgData = await request.get(`/consult/message/list/${orderData.id}`).catch(() => [])
      messages.value = msgData || []
    }
  } catch (e) {
    console.error('加载问诊数据失败', e)
  } finally {
    loading.value = false
  }
  await nextTick()
  scrollToBottom()
}

function onDialogImageSelected(e) {
  const file = e.target.files?.[0]
  if (!file) return
  dialogPendingImage.value = file
  dialogPendingPreview.value = URL.createObjectURL(file)
  e.target.value = ''
}

function connectWs() {
  ws = new WebSocket(CONFIG.WS_CONSULT)
  ws.onopen = () => { wsConnected.value = true }
  ws.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      messages.value.push(msg)
      nextTick(() => scrollToBottom())
    } catch { /* ignore */ }
  }
  ws.onclose = () => {
    wsConnected.value = false
    reconnectTimer = setTimeout(connectWs, 3000)
  }
  ws.onerror = () => { ws?.close() }
}

function sendMessage() {
  const text = newMessage.value.trim()
  if ((!text && !dialogPendingImage.value) || !ws || ws.readyState !== WebSocket.OPEN) {
    showToast('连接未就绪')
    return
  }
  const cid = orderId.value || Date.now()
  if (dialogPendingImage.value) {
    const reader = new FileReader()
    reader.onload = (ev) => {
      const base64 = ev.target?.result
      ws.send(JSON.stringify({ consultId: cid, content: text || '', senderType: 1, msgType: 2, image: base64 }))
      messages.value.push({ id: Date.now(), senderType: 1, content: text || '', image: base64 })
      dialogPendingImage.value = null; dialogPendingPreview.value = ''
      newMessage.value = ''
      nextTick(() => scrollToBottom())
    }
    reader.readAsDataURL(dialogPendingImage.value)
  } else {
    ws.send(JSON.stringify({ consultId: cid, content: text, senderType: 1 }))
    messages.value.push({ id: Date.now(), senderType: 1, content: text })
    newMessage.value = ''
    nextTick(() => scrollToBottom())
  }
}

onBeforeUnmount(() => {
  if (reconnectTimer) clearTimeout(reconnectTimer)
  if (ws) {
    ws.onclose = null
    ws.close()
    ws = null
  }
})

function scrollToBottom() {
  if (chatRef.value) {
    chatRef.value.scrollTop = chatRef.value.scrollHeight
  }
}

onMounted(fetchData)
</script>

<style scoped>
.dialog-page { background: #F0F8F0; }
.order-info { background: #fff; margin: 10px 15px; border-radius: 10px; padding: 12px; }
.order-row { display: flex; justify-content: space-between; align-items: center; padding: 4px 0; font-size: 13px; }
.order-label { color: #999; }
.order-value { color: #333; font-weight: 500; }
.order-price { color: #FF4444; font-weight: 600; font-size: 15px; }
.connected-badge { text-align: center; padding: 8px 0; display: flex; align-items: center; justify-content: center; gap: 8px; }
.connected-img { width: 24px; }
.ws-status { font-size: 11px; }
.ws-status.online { color: #4CAF50; }
.ws-status.offline { color: #FF9800; }
.chat-messages { flex: 1; padding: 15px; overflow-y: auto; display: flex; flex-direction: column; gap: 12px; }
.message { display: flex; gap: 10px; }
.message.user { justify-content: flex-end; }
.msg-avatar { width: 32px; height: 32px; border-radius: 50%; background: #26A65B; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; flex-shrink: 0; }
.msg-bubble { max-width: 70%; background: #fff; border-radius: 12px; padding: 10px 14px; font-size: 14px; color: #333; line-height: 1.5; }
.user-bubble { background: #26A65B; color: #fff; }
.input-bar { display: flex; align-items: center; gap: 8px; padding: 10px 15px; background: #fff; }
.input-bar :deep(.van-field) { flex: 1; }
.msg-image { max-width: 200px; max-height: 200px; border-radius: 8px; cursor: pointer; display: block; }
.upload-btn { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 50%; cursor: pointer; flex-shrink: 0; }
.upload-btn:active { background: #f0f0f0; }
.pending-preview { position: relative; margin: 0 15px 8px; display: inline-block; }
.pending-preview img { width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #e0e0e0; }
.preview-close { position: absolute !important; top: -6px; right: -6px; background: #999; color: #fff; border-radius: 50%; padding: 2px; font-size: 12px; }
</style>

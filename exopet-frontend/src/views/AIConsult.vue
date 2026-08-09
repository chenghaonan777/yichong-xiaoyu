<template>
  <div class="page aiconsult-page">
    <StatusBar />
    <div class="consult-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>AI问诊</h3>
    </div>

    <!-- 前置表单：选择和描述 -->
    <van-action-sheet v-model:show="showForm" title="填写问诊信息" closeable :close-on-popstate="false">
      <div class="sheet-form">
        <van-form @submit="startDiagnose">
          <van-field v-model="form.breedType" is-link readonly name="breedType" label="宠物大类" placeholder="请选择"
            :rules="[{ required: true, message: '请选择宠物大类' }]" @click="showBreedPicker = true" />
          <van-field v-model="form.breedName" name="breedName" label="具体品种" placeholder="如：豹纹守宫、牡丹鹦鹉"
            :rules="[{ required: true, message: '请输入品种' }]" />
          <van-field v-model="form.symptoms" name="symptoms" label="症状标签" placeholder="如：拒食,精神萎靡,软便（逗号分隔）"
            :rules="[{ required: true, message: '请输入症状' }]" />
          <van-field v-model="form.symptomDesc" name="symptomDesc" label="详细描述" type="textarea" rows="3"
            placeholder="请详细描述宠物的情况..." />
          <van-uploader v-model="form.images" :max-count="1" :after-read="onImageRead" />
          <div class="form-actions">
            <van-button round block color="#26A65B" native-type="submit" :loading="loading">开始问诊</van-button>
          </div>
        </van-form>
      </div>
    </van-action-sheet>

    <van-action-sheet v-model:show="showBreedPicker" :actions="breedOptions" @select="onBreedSelect" />

    <div class="consult-content" v-if="!showForm">
      <!-- 对话消息 -->
      <div class="chat-messages" ref="chatRef">
        <div class="message ai" v-for="(msg, idx) in messages" :key="idx">
          <div class="msg-avatar">
            <img src="/images/首页/u97.svg" class="ai-icon" />
          </div>
          <div class="msg-bubble" v-if="msg.content">{{ msg.content }}</div>
          <!-- 最终结论 -->
          <div class="conclusion-card" v-if="msg.raw?.isDone && msg.raw?.conclusion">
            <div class="conclusion-section" v-if="msg.raw.conclusion.possibleDiseases">
              <p class="conclusion-title">🩺 可能疾病</p>
              <div class="disease-item" v-for="d in msg.raw.conclusion.possibleDiseases" :key="d.name">
                <span class="disease-name">{{ d.name }}</span>
                <span class="disease-prob">{{ (d.probability * 100).toFixed(0) }}%</span>
                <span class="disease-severity" :class="severityClass(d.severity)">{{ d.severity }}</span>
              </div>
            </div>
            <div class="conclusion-section" v-if="msg.raw.conclusion.carePlan">
              <p class="conclusion-title">📋 养护方案</p>
              <p v-if="msg.raw.conclusion.carePlan.temperature">🌡️ {{ msg.raw.conclusion.carePlan.temperature }}</p>
              <p v-if="msg.raw.conclusion.carePlan.diet">🍽️ {{ msg.raw.conclusion.carePlan.diet }}</p>
              <p v-if="msg.raw.conclusion.carePlan.medication">💊 {{ msg.raw.conclusion.carePlan.medication }}</p>
            </div>
            <div class="confidence-bar" v-if="msg.raw.conclusion.confidence">
              <span>置信度：{{ (msg.raw.conclusion.confidence * 100).toFixed(0) }}%</span>
              <van-button v-if="msg.raw.conclusion.confidence < 0.6" size="small" round color="#FF9800"
                @click="$router.push('/specialist-consult')">建议转诊真人</van-button>
            </div>
          </div>
        </div>
        <div class="message user" v-for="(msg, idx) in userMessages" :key="'u' + idx">
          <div class="msg-bubble user-bubble">{{ msg }}</div>
        </div>
        <div v-if="loading" class="message ai">
          <div class="msg-avatar"><img src="/images/首页/u97.svg" class="ai-icon" /></div>
          <div class="msg-bubble"><van-loading type="ball" size="18" color="#26A65B" /></div>
        </div>
      </div>

      <!-- 快捷标签 -->
      <div class="quick-questions" v-if="nextQuestion">
        <p class="quick-title">AI追问：</p>
        <div class="quick-tags">
          <span class="quick-tag" @click="sendMessage(nextQuestion)">{{ nextQuestion }}</span>
        </div>
      </div>

      <!-- 图片预览 -->
      <div class="pending-preview" v-if="pendingPreview" @click="showImagePreview([pendingPreview])">
        <img :src="pendingPreview" />
        <van-icon name="close" class="preview-close" @click.stop="pendingPreview='';pendingImage=null" />
      </div>
      <!-- 输入栏 -->
      <div class="input-bar" v-if="!isDone">
        <div class="upload-btn" @click="triggerImageUpload">
          <van-icon name="photo-o" size="22" color="#666" />
        </div>
        <input type="file" ref="imageInputRef" accept="image/*" style="display:none" @change="onImageSelected" />
        <van-field v-model="message" :placeholder="nextQuestion || '输入您的回复...'" @keypress.enter="sendMessage" />
        <van-button round color="#26A65B" size="small" @click="sendMessage" :disabled="(!message.trim() && !pendingImage) || loading">发送</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { showToast, showImagePreview } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import { useAiChat } from '../composables/useAiChat.js'
import { useUserStore } from '../stores/user.js'

const userStore = useUserStore()
const chat = useAiChat('/api/ai/diagnose', 'symptomDesc')

const message = ref('')
const showForm = ref(true)
const showBreedPicker = ref(false)
const chatRef = ref(null)
const uploadedImage = ref(null)
const imageInputRef = ref(null)
const pendingImage = ref(null)
const pendingPreview = ref('')

const messages = computed(() => chat.messages.value)
const userMessages = computed(() => chat.history.value.map(h => h.user))
const loading = computed(() => chat.loading.value)
const isDone = computed(() => chat.isDone.value)
const nextQuestion = computed(() => {
  const last = chat.messages.value[chat.messages.value.length - 1]
  return last?.raw?.nextQuestion || ''
})

const form = ref({
  breedType: '',
  breedName: '',
  symptoms: '',
  symptomDesc: '',
  images: []
})

const breedOptions = [
  { name: '爬行类', value: '爬行类' },
  { name: '鸟类', value: '鸟类' },
  { name: '水族', value: '水族' },
  { name: '小型哺乳', value: '小型哺乳' }
]

function onBreedSelect(item) {
  form.value.breedType = item.value
  showBreedPicker.value = false
}

function onImageRead(file) {
  uploadedImage.value = file.file
}

async function startDiagnose() {
  const f = form.value
  if (!f.breedType || !f.breedName || !f.symptoms) {
    showToast('请填写完整信息')
    return
  }
  showForm.value = false
  await chat.sendFirst({
    image: uploadedImage.value,
    breedType: f.breedType,
    breedName: f.breedName,
    symptoms: f.symptoms,
    symptomDesc: f.symptomDesc || undefined
  })
  scrollToBottom()
}

function triggerImageUpload() {
  imageInputRef.value?.click()
}

function onImageSelected(e) {
  const file = e.target.files?.[0]
  if (!file) return
  pendingImage.value = file
  // 生成预览 URL
  pendingPreview.value = URL.createObjectURL(file)
  showToast('已选择图片')
  // 重置 input 以允许重复选同一文件
  e.target.value = ''
}

async function sendMessage(text) {
  const msg = text || message.value
  if ((!msg.trim() && !pendingImage.value) || loading.value) return
  const extraParams = {
    breedType: form.value.breedType,
    breedName: form.value.breedName,
    symptoms: form.value.symptoms
  }
  if (pendingImage.value) {
    extraParams.image = pendingImage.value
    pendingImage.value = null
    pendingPreview.value = ''
  }
  message.value = ''
  await chat.sendNext(msg, extraParams)
  nextTick(() => scrollToBottom())
}

function scrollToBottom() {
  if (chatRef.value) {
    chatRef.value.scrollTop = chatRef.value.scrollHeight
  }
}

function severityClass(s) {
  if (s === '高') return 's-high'
  if (s === '中') return 's-mid'
  return 's-low'
}
</script>

<style scoped>
/* 保持原有样式不变，新增结论卡片样式 */
.aiconsult-page {
  background: #F0F8F0;
}
.consult-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
}
.consult-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
}
.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
}
.consult-content {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}
.chat-messages {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}
.message {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}
.message.ai {
  align-items: flex-start;
  flex-direction: row;
}
.message.user {
  justify-content: flex-end;
}
.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #E8F5E9;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.ai-icon {
  width: 22px;
  height: 22px;
}
.msg-bubble {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  font-size: 14px;
  color: #333;
  max-width: 80%;
  line-height: 1.5;
}
.user-bubble {
  background: #26A65B;
  color: #fff;
}
.quick-questions {
  padding: 10px 15px;
  background: #fff;
  border-top: 1px solid #eee;
}
.quick-title {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}
.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.quick-tag {
  background: #E8F5E9;
  color: #26A65B;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
}
.input-bar {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  padding-bottom: calc(8px + env(safe-area-inset-bottom, 0px));
  background: #fff;
  gap: 8px;
}
.input-bar :deep(.van-field) {
  flex: 1;
}
.upload-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
}
.upload-btn:active {
  background: #f0f0f0;
}
.pending-preview {
  position: relative;
  margin: 0 15px 8px;
  display: inline-block;
}
.pending-preview img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}
.preview-close {
  position: absolute !important;
  top: -6px;
  right: -6px;
  background: #999;
  color: #fff;
  border-radius: 50%;
  padding: 2px;
  font-size: 12px;
}
/* 结论卡片 */
.conclusion-card {
  background: #f8fff8;
  border: 1px solid #c8e6c9;
  border-radius: 12px;
  padding: 12px;
  margin-top: 8px;
  max-width: 85%;
}
.conclusion-section { margin-bottom: 10px; }
.conclusion-title { font-size: 13px; font-weight: 600; color: #333; margin-bottom: 6px; }
.disease-item { display: flex; align-items: center; gap: 8px; font-size: 12px; margin-bottom: 4px; }
.disease-name { flex: 1; color: #333; }
.disease-prob { color: #26A65B; font-weight: 600; }
.disease-severity { font-size: 11px; padding: 1px 8px; border-radius: 8px; }
.s-high { background: #FFEBEE; color: #C62828; }
.s-mid { background: #FFF3E0; color: #F57C00; }
.s-low { background: #E8F5E9; color: #388E3C; }
.confidence-bar { display: flex; align-items: center; gap: 8px; font-size: 12px; color: #666; padding-top: 8px; border-top: 1px solid #e8e8e8; }
/* 表单 */
.sheet-form { padding: 16px; max-height: 70vh; overflow-y: auto; }
.form-actions { padding: 16px 0; }
</style>

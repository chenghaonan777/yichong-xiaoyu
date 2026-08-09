<template>
  <div class="page mood-page">
    <StatusBar />
    <div class="mood-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>情绪分析</h3>
    </div>
    <div class="mood-content">
      <!-- 前置描述表单 -->
      <div class="analysis-card" v-if="!started">
        <img src="/images/情绪分析/u975.svg" class="analysis-icon" />
        <div class="analysis-text">
          <van-field v-model="description" type="textarea" rows="4" placeholder="请描述宠物的行为表现，如：我家鹦鹉最近总是拔自己的羽毛，脾气也变得暴躁..." />
          <van-uploader v-model="images" :max-count="1" class="mood-uploader" />
          <van-button round color="#26A65B" size="small" class="start-btn" @click="startAnalysis" :loading="loading">开始分析</van-button>
        </div>
      </div>

      <!-- AI Result -->
      <div class="analysis-card" v-if="aiMessage">
        <img src="/images/情绪分析/u975.svg" class="analysis-icon" />
        <div class="analysis-text">
          <p>{{ aiMessage.content }}</p>
          <!-- 最终结论 -->
          <div class="mood-conclusion" v-if="aiMessage.raw?.isDone && aiMessage.raw?.conclusion">
            <div class="mood-tag">
              <span class="mood-label">情绪判断：</span>
              <span class="mood-value">{{ aiMessage.raw.conclusion.mood }}</span>
            </div>
            <div class="mood-confidence">
              置信度：{{ (aiMessage.raw.conclusion.confidence * 100).toFixed(0) }}%
            </div>
            <div class="mood-advice" v-if="aiMessage.raw.conclusion.advice">
              <p class="advice-title">💡 养护建议</p>
              <p>{{ aiMessage.raw.conclusion.advice }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Chat Area (后续轮次) -->
      <div class="chat-area" v-if="started">
        <!-- 对话气泡 -->
        <div class="question-bubble" v-for="(msg, idx) in moodMessages" :key="idx">
          <img src="/images/ai问诊/u273.svg" class="question-avatar" />
          <div class="question-text">{{ msg.role === 'ai' ? msg.content : '我：' + msg.content }}</div>
        </div>

        <!-- 追问 -->
        <div class="question-bubble" v-if="nextQuestion">
          <img src="/images/ai问诊/u273.svg" class="question-avatar" />
          <div class="question-text mood-prompt">{{ nextQuestion }}</div>
        </div>

        <!-- Input Area -->
        <div class="input-area">
          <div class="input-toolbar">
            <div class="toolbar-center">
              <van-field v-model="question" :placeholder="nextQuestion || '描述宠物行为...'" :border="false" @keypress.enter="sendQuestion" />
            </div>
            <van-button round color="#26A65B" size="small" class="send-btn" @click="sendQuestion" :loading="loading" :disabled="!question.trim()">发送</van-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { showToast } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import { useAiChat } from '../composables/useAiChat.js'

const chat = useAiChat('/api/ai/mood-analysis', 'description')
const question = ref('')
const description = ref('')
const images = ref([])
const started = ref(false)
const uploadedImage = ref(null)

const aiMessage = computed(() => {
  return chat.messages.value.find(m => m.role === 'ai') || null
})
const moodMessages = computed(() => chat.messages.value)
const loading = computed(() => chat.loading.value)
const nextQuestion = computed(() => {
  const last = chat.messages.value[chat.messages.value.length - 1]
  return last?.raw?.nextQuestion || ''
})

async function startAnalysis() {
  if (!description.value.trim()) {
    showToast('请描述宠物的行为表现')
    return
  }
  started.value = true
  await chat.sendFirst({
    image: uploadedImage.value,
    description: description.value.trim()
  })
}

async function sendQuestion() {
  const text = question.value.trim()
  if (!text || loading.value) return
  question.value = ''
  await chat.sendNext(text)
}

// 监听图片上传
function onImageRead(file) {
  uploadedImage.value = file.file
}
</script>

<style scoped>
.mood-page { background: #F0F8F0; }
.mood-header { display: flex; align-items: center; padding: 8px 15px; gap: 10px; background: #F0F8F0; }
.mood-header h3 { font-size: 18px; color: #333; margin: 0; }
.back-btn { display: flex; align-items: center; justify-content: center; width: 30px; height: 30px; }
.mood-content { padding: 15px; display: flex; flex-direction: column; height: calc(100vh - 60px); }

.analysis-card {
  background: #fff; border-radius: 12px; padding: 15px;
  display: flex; gap: 10px; margin-bottom: 12px; align-items: flex-start;
}
.analysis-icon { width: 50px; height: 50px; flex-shrink: 0; margin-top: 4px; }
.analysis-text { font-size: 13px; color: #333; line-height: 1.6; flex: 1; }

.start-btn { margin-top: 10px; }
.mood-uploader { margin-top: 8px; }

.mood-conclusion { margin-top: 12px; padding-top: 10px; border-top: 1px solid #e8e8e8; }
.mood-tag { margin-bottom: 6px; }
.mood-label { font-size: 12px; color: #999; }
.mood-value { font-size: 16px; color: #26A65B; font-weight: 600; }
.mood-confidence { font-size: 12px; color: #666; margin-bottom: 8px; }
.advice-title { font-weight: 600; color: #333; margin-bottom: 4px; }
.mood-advice { background: #FFF8E1; border-radius: 8px; padding: 10px; font-size: 13px; color: #795548; }

.chat-area { flex: 1; display: flex; flex-direction: column; overflow-y: auto; }
.question-bubble { display: flex; align-items: flex-start; gap: 8px; margin-bottom: 12px; }
.question-avatar { width: 36px; height: 36px; border-radius: 50%; flex-shrink: 0; }
.question-text {
  background: #fff; border-radius: 12px; padding: 10px 14px;
  font-size: 14px; color: #333; max-width: 70%; line-height: 1.5;
}
.mood-prompt { background: #E8F5E9; color: #26A65B; }

.input-area { margin-top: auto; background: #fff; border-radius: 12px; padding: 8px 10px; }
.input-toolbar { display: flex; align-items: center; gap: 8px; }
.toolbar-center { flex: 1; }
.toolbar-center :deep(.van-field) { padding: 4px 0; }
.send-btn { flex-shrink: 0; }
</style>

<style scoped>
.mood-page {
  background: #F0F8F0;
}
.mood-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
  background: #F0F8F0;
}
.mood-header h3 {
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
.mood-content {
  padding: 15px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}

/* Analysis Card */
.analysis-card {
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  align-items: flex-start;
}
.analysis-icon {
  width: 50px;
  height: 50px;
  flex-shrink: 0;
  margin-top: 4px;
}
.analysis-text {
  font-size: 13px;
  color: #333;
  line-height: 1.6;
  flex: 1;
}

/* Chat Area */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.question-bubble {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 12px;
}
.question-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}
.question-text {
  background: #fff;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
  color: #333;
  max-width: 70%;
}

/* Input Area */
.input-area {
  margin-top: auto;
  background: #fff;
  border-radius: 12px;
  padding: 8px 10px;
}
.input-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}
.toolbar-left {
  display: flex;
  gap: 6px;
}
.tool-icon {
  width: 28px;
  height: 28px;
  cursor: pointer;
}
.toolbar-center {
  flex: 1;
}
.toolbar-center :deep(.van-field) {
  padding: 4px 0;
}
.send-btn {
  flex-shrink: 0;
}
</style>

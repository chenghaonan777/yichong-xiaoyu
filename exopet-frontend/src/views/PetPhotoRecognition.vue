<template>
  <div class="page photoid-page">
    <StatusBar />
    <div class="photoid-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>拍照识宠</h3>
    </div>
    <div class="photoid-content">
      <!-- 前置：上传/描述 -->
      <div class="result-card" v-if="!started">
        <img src="/images/拍照识宠/u684.svg" class="result-icon" />
        <div class="result-text">
          <van-uploader v-model="images" :max-count="1" :after-read="onImageRead" />
          <van-field v-model="description" type="textarea" rows="3" placeholder="描述宠物外形特征，如：全身绿色，尾巴可以卷起来..." />
          <van-button round color="#26A65B" size="small" class="start-btn" @click="startRecognition" :loading="loading">识别</van-button>
        </div>
      </div>

      <!-- AI Result -->
      <div class="result-card" v-if="aiMessage">
        <img src="/images/拍照识宠/u684.svg" class="result-icon" />
        <div class="result-text">
          <p>{{ aiMessage.content }}</p>
          <!-- 最终结论 -->
          <div class="breed-conclusion" v-if="aiMessage.raw?.isDone && aiMessage.raw?.conclusion">
            <div class="breed-name-tag">{{ aiMessage.raw.conclusion.breedName }}</div>
            <p class="sci-name" v-if="aiMessage.raw.conclusion.sciName">{{ aiMessage.raw.conclusion.sciName }}</p>
            <p class="breed-desc" v-if="aiMessage.raw.conclusion.description">{{ aiMessage.raw.conclusion.description }}</p>
            <div class="char-list" v-if="aiMessage.raw.conclusion.characteristics">
              <span class="char-tag" v-for="c in aiMessage.raw.conclusion.characteristics" :key="c">{{ c }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Chat Area (后续轮次) -->
      <div class="chat-area" v-if="started">
        <div class="question-bubble" v-for="(msg, idx) in photoMessages" :key="idx">
          <img src="/images/ai问诊/u273.svg" class="question-avatar" />
          <div class="question-text">{{ msg.role === 'ai' ? msg.content : msg.content }}</div>
        </div>

        <div class="input-area">
          <div class="input-toolbar">
            <div class="toolbar-center">
              <van-field v-model="question" placeholder="输入您的问题..." :border="false" @keypress.enter="sendQuestion" />
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

const chat = useAiChat('/api/ai/breed-recognize', 'description')
const question = ref('')
const description = ref('')
const images = ref([])
const started = ref(false)
const uploadedImage = ref(null)

const aiMessage = computed(() => chat.messages.value.find(m => m.role === 'ai') || null)
const photoMessages = computed(() => chat.messages.value)
const loading = computed(() => chat.loading.value)

function onImageRead(file) {
  uploadedImage.value = file.file
}

async function startRecognition() {
  if (!description.value.trim() && !uploadedImage.value) {
    showToast('请描述宠物外形或上传图片')
    return
  }
  started.value = true
  await chat.sendFirst({
    image: uploadedImage.value,
    description: description.value.trim() || '请识别这只宠物'
  })
}

async function sendQuestion() {
  const text = question.value.trim()
  if (!text || loading.value) return
  question.value = ''
  await chat.sendNext(text)
}
</script>

<style scoped>
.photoid-page { background: #F0F8F0; }
.photoid-header { display: flex; align-items: center; padding: 8px 15px; gap: 10px; background: #F0F8F0; }
.photoid-header h3 { font-size: 18px; color: #333; margin: 0; }
.back-btn { display: flex; align-items: center; justify-content: center; width: 30px; height: 30px; }
.photoid-content { padding: 15px; display: flex; flex-direction: column; height: calc(100vh - 60px); }

.result-card {
  background: #fff; border-radius: 12px; padding: 15px;
  display: flex; gap: 10px; margin-bottom: 12px; align-items: flex-start;
}
.result-icon { width: 50px; height: 50px; flex-shrink: 0; margin-top: 4px; }
.result-text { font-size: 13px; color: #333; line-height: 1.6; flex: 1; }

.start-btn { margin-top: 10px; }

.breed-conclusion { margin-top: 12px; padding-top: 10px; border-top: 1px solid #e8e8e8; }
.breed-name-tag {
  display: inline-block; background: #26A65B; color: #fff;
  padding: 4px 14px; border-radius: 20px; font-size: 14px; font-weight: 600; margin-bottom: 6px;
}
.sci-name { font-size: 12px; color: #999; font-style: italic; margin-bottom: 6px; }
.breed-desc { font-size: 13px; color: #666; line-height: 1.6; margin-bottom: 8px; }
.char-list { display: flex; flex-wrap: wrap; gap: 6px; }
.char-tag { background: #E8F5E9; color: #26A65B; padding: 2px 10px; border-radius: 12px; font-size: 11px; }

.chat-area { flex: 1; display: flex; flex-direction: column; overflow-y: auto; }
.question-bubble { display: flex; align-items: flex-start; gap: 8px; margin-bottom: 12px; }
.question-avatar { width: 36px; height: 36px; border-radius: 50%; flex-shrink: 0; }
.question-text { background: #fff; border-radius: 12px; padding: 10px 14px; font-size: 14px; color: #333; max-width: 70%; }

.input-area { margin-top: auto; background: #fff; border-radius: 12px; padding: 8px 10px; }
.input-toolbar { display: flex; align-items: center; gap: 8px; }
.toolbar-center { flex: 1; }
.toolbar-center :deep(.van-field) { padding: 4px 0; }
.send-btn { flex-shrink: 0; }
</style>

<style scoped>
.photoid-page {
  background: #F0F8F0;
}
.photoid-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
  background: #F0F8F0;
}
.photoid-header h3 {
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
.photoid-content {
  padding: 15px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}

/* Result Card */
.result-card {
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  align-items: flex-start;
}
.result-icon {
  width: 50px;
  height: 50px;
  flex-shrink: 0;
  margin-top: 4px;
}
.result-text {
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
  flex-wrap: wrap;
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
}
.question-image {
  width: 100%;
  padding-left: 44px;
}
.question-thumb {
  width: 80px;
  height: auto;
  border-radius: 8px;
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

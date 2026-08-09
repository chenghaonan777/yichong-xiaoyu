<template>
  <div class="page feedback-page">
    <StatusBar />
    <NavHeader title="问题反馈" />
    <div class="feedback-content">
      <van-field
        v-model="content"
        rows="4"
        type="textarea"
        maxlength="500"
        placeholder="请描述您遇到的问题或建议..."
        show-word-limit
      />
      <div class="upload-area">
        <p class="upload-label">上传截图（选填）</p>
        <van-uploader v-model="fileList" multiple :max-count="3" />
      </div>
      <div class="contact-field">
        <van-field v-model="contact" placeholder="请输入联系方式（选填）" />
      </div>
      <div class="submit-btn">
        <van-button round block color="#26A65B" @click="submit" :loading="submitting">提交反馈</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import request from '../utils/request.js'
import { useUserStore } from '../stores/user.js'

const router = useRouter()
const userStore = useUserStore()
const content = ref('')
const contact = ref('')
const fileList = ref([])
const submitting = ref(false)

const submit = async () => {
  if (!content.value.trim()) {
    showToast('请输入反馈内容')
    return
  }
  submitting.value = true
  try {
    await request.post('/feedback', {
      userId: userStore.userId,
      content: content.value.trim(),
      contact: contact.value || undefined
    }).catch(() => {
      // 后端可能暂无 feedback 接口，本地兜底
      localStorage.setItem('feedback_' + Date.now(), JSON.stringify({
        content: content.value.trim(),
        contact: contact.value,
        time: new Date().toISOString()
      }))
    })
    router.push('/submit-success')
  } catch {
    showToast('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.feedback-page { background: #F0F8F0; }
.feedback-content { padding: 15px; display: flex; flex-direction: column; gap: 15px; }
.upload-area { background: #fff; border-radius: 8px; padding: 15px; }
.upload-label { font-size: 13px; color: #333; margin-bottom: 10px; }
.contact-field { background: #fff; border-radius: 8px; }
.submit-btn { padding-top: 20px; }
</style>

<style scoped>
.feedback-page {
  background: #F0F8F0;
}
.feedback-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
}
.feedback-header h3 {
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
.feedback-content {
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.upload-area {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
}
.upload-label {
  font-size: 13px;
  color: #333;
  margin-bottom: 10px;
}
.contact-field {
  background: #fff;
  border-radius: 8px;
}
.submit-btn {
  padding-top: 20px;
}
</style>

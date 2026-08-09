<template>
  <div class="login2-page">
    <StatusBar />
    <div class="login-content">
      <div class="back-btn" @click="$router.push('/splash')">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>

      <img src="/images/登录页2/u41.png" alt="logo" class="login-logo" />
      <h1 class="app-title">异宠小愈</h1>

      <div class="input-area">
        <div class="phone-row">
          <div class="country-code">
            <img src="/images/登录页2/u20.svg" class="code-icon" />
            <span>+86</span>
          </div>
          <van-field v-model="phone" placeholder="请输入手机号" class="phone-input" />
        </div>
        <div class="code-row">
          <van-field v-model="code" placeholder="请输入验证码" class="code-input" />
          <van-button size="small" round plain color="#26A65B" class="get-code-btn"
            :loading="sending" :disabled="countdown > 0" @click="sendCode">
            {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
          </van-button>
        </div>
      </div>

      <div class="login-btn-wrapper">
        <van-button round block color="#26A65B" size="large" @click="handleLogin">登录</van-button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '../utils/request.js'
import StatusBar from '../components/StatusBar.vue'
import { useUserStore } from '../stores/user.js'

const router = useRouter()
const userStore = useUserStore()
const phone = ref('')
const code = ref('')
const sending = ref(false)
const countdown = ref(0)
let timer = null

const sendCode = async () => {
  if (!phone.value || !/^1[3-9]\d{9}$/.test(phone.value)) {
    showToast('请输入正确的手机号')
    return
  }
  if (sending.value || countdown.value > 0) return
  sending.value = true
  try {
    await request.post('/auth/send-code', { phone: phone.value })
    showToast('验证码已发送')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        timer = null
      }
    }, 1000)
  } catch (e) {
    // request.js 拦截器已处理 toast
  } finally {
    sending.value = false
  }
}

const handleLogin = async () => {
  if (!phone.value) {
    showToast('请输入手机号')
    return
  }
  if (!code.value) {
    showToast('请输入验证码')
    return
  }
  try {
    const data = await request.post('/auth/login', {
      phone: phone.value,
      code: code.value
    })
    userStore.setLogin(data.token, data.userId)
    showToast('登录成功')
    router.push('/home')
  } catch (e) {
    // 拦截器已处理错误提示
  }
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.login2-page {
  min-height: 100vh;
  background: #fff;
}
.login-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 30px;
  position: relative;
}
.back-btn {
  position: absolute;
  left: 15px;
  top: 10px;
  width: 44px;
  height: 39px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-logo {
  width: 80px;
  height: 80px;
  margin-top: 60px;
  margin-bottom: 10px;
}
.app-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 50px;
}
.input-area {
  width: 100%;
  margin-bottom: 30px;
}
.phone-row {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
  margin-bottom: 20px;
}
.country-code {
  display: flex;
  align-items: center;
  gap: 4px;
  padding-right: 12px;
  border-right: 1px solid #ddd;
  font-size: 14px;
  color: #333;
}
.code-icon {
  width: 20px;
  height: 20px;
}
.phone-input {
  flex: 1;
  border: none;
  padding-left: 12px;
}
.code-row {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}
.code-input {
  flex: 1;
  border: none;
}
.get-code-btn {
  flex-shrink: 0;
  white-space: nowrap;
}
.login-btn-wrapper {
  width: 314px;
  margin-bottom: 40px;
}
.register-row {
  display: flex;
  align-items: center;
  gap: 4px;
}
.no-account {
  font-size: 14px;
  color: #999;
}
.register-link {
  font-size: 14px;
  color: #26A65B;
}
</style>

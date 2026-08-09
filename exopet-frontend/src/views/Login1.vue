<template>
  <div class="login1-page">
    <StatusBar />
    <div class="login-content">
      <div class="back-btn" @click="$router.push('/splash')">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <img src="/images/登录页2/u41.png" alt="logo" class="login-logo" />
      <h2 class="welcome-text">欢迎回来</h2>
      <h1 class="app-title">异宠小愈</h1>

<div class="login-btn-wrapper">
        <van-button round block color="#26A65B" size="large" :disabled="!agreed" @click="handleLogin">手机号一键登录</van-button>
      </div>
      <div class="other-login">
        <van-button round block plain color="#26A65B" size="large" @click="$router.push('/login2')">其他手机号登录</van-button>
      </div>


      <div class="agreement">
        <van-checkbox v-model="agreed" shape="square" checked-color="#26A65B" size="14px">
          <template #default>
            <span class="gray">登录即代表您已阅读并同意</span>
            <span class="green">《用户服务协议》</span>
            <span class="gray">与</span>
            <span class="green">《隐私保护政策》</span>
          </template>
        </van-checkbox>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import StatusBar from '../components/StatusBar.vue'

const router = useRouter()
const agreed = ref(false)

const handleLogin = () => {
  router.push('/login2')
}
</script>

<style scoped>
.login1-page {
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
.welcome-text {
  font-size: 16px;
  color: #999;
  margin-bottom: 4px;
}
.app-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 40px;
}
.phone-display {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}
.phone-number {
  font-size: 20px;
  color: #333;
}
.login-btn-wrapper {
  width: 314px;
  margin-bottom: 16px;
}
.other-login {
  width: 314px;
  margin-bottom: 40px;
}
.agreement {
  text-align: center;
  font-size: 11px;
  line-height: 1.8;
  margin-bottom: 60px;
}
.agreement :deep(.van-checkbox) {
  display: inline-flex;
}
.gray {
  color: #797979;
}
.green {
  color: #26A65B;
}
</style>

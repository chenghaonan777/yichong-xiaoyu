<template>
  <div class="splash-page">
    <StatusBar />
    <div class="splash-content">
      <div class="logo-area">
        <img src="/images/启动页/u13.svg" alt="logo" class="logo" />
      </div>
      <h1 class="app-name">异宠小愈</h1>
      <p class="app-slogan">异宠健康.专业守护</p>
      <div class="start-btn" @click="$router.push('/login1')">
        <van-button round block color="#26A65B" size="large">开始体验</van-button>
      </div>
      <div class="footer-links">
        <span class="link">用户协议</span>
        <span class="sep">|</span>
        <span class="link">隐私政策</span>
      </div>
      <p class="version">Version 1.0.0 · Powered by HealingPet Tech</p>
    </div>
  </div>
</template>

<script setup>
import StatusBar from '../components/StatusBar.vue'
</script>

<style scoped>
.splash-page {
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
}
.splash-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120px;
}
.logo-area {
  width: 100px;
  height: 100px;
  margin-bottom: 20px;
}
.logo {
  width: 100%;
  height: 100%;
}
.app-name {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}
.app-slogan {
  font-size: 14px;
  color: #999;
  margin-bottom: 120px;
}
.start-btn {
  width: 314px;
  margin-bottom: 40px;
}
.footer-links {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}
.link {
  font-size: 13px;
  color: #26A65B;
}
.sep {
  color: #ddd;
}
.version {
  font-size: 11px;
  color: #bbb;
}
</style>

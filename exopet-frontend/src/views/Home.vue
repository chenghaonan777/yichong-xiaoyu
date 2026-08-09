<template>
  <div class="page home-page">
    <StatusBar />
    <div class="home-content">
      <!-- Banner -->
      <div class="banner" @click="$router.push('/membership')">
        <img src="/images/首页/u129.png" alt="banner" class="banner-img" />
      </div>

      <!-- 医疗服务 -->
      <h3 class="section-title">医疗服务</h3>
      <div class="service-grid">
        <div class="service-card" @click="$router.push('/ai-consult')">
          <img src="/images/首页/u97.svg" class="service-icon" />
          <span class="service-name">AI问诊</span>
          <span class="service-desc">快速问诊</span>
        </div>
        <div class="service-card" @click="$router.push('/specialist-consult')">
          <img src="/images/首页/u104.svg" class="service-icon" />
          <span class="service-name">专人问诊</span>
          <span class="service-desc">医生诊断</span>
        </div>
        <div class="service-card" @click="$router.push('/emergency-consult')">
          <img src="/images/首页/u110.svg" class="service-icon" />
          <span class="service-name">急诊问诊</span>
          <span class="service-desc">24h专人在线</span>
        </div>
        <div class="service-card" @click="$router.push('/hospital-finder')">
          <img src="/images/首页/u116.svg" class="service-icon" />
          <span class="service-name">医院列表</span>
          <span class="service-desc">查找医院</span>
        </div>
      </div>

      <!-- 实用工具 -->
      <h3 class="section-title">实用工具</h3>
      <div class="tools-grid">
        <div class="tool-card" @click="$router.push('/pet-photo')">
          <img src="/images/首页/u122.svg" class="tool-icon" />
          <span class="tool-name">拍照识宠</span>
        </div>
        <div class="tool-card" @click="$router.push('/mood-analysis')">
          <img src="/images/首页/u127.svg" class="tool-icon" />
          <span class="tool-name">情绪分析</span>
        </div>
        <div class="tool-card" @click="$router.push('/ai-encyclopedia')">
          <img src="/images/首页/u91.svg" class="tool-icon" />
          <span class="tool-name">AI百科</span>
        </div>
      </div>
    </div>
    <AppTabBar />
  </div>
</template>

<script setup>
import StatusBar from '../components/StatusBar.vue'
import AppTabBar from '../components/AppTabBar.vue'
</script>

<style scoped>
.home-page { background: #F0F8F0; }
.home-content { padding: 0 14px; }
.banner { margin: 4px 0 12px; border-radius: 12px; overflow: hidden; cursor: pointer; }
.banner-img { display: block; width: 100%; height: auto; }
.section-title { font-size: 16px; font-weight: 700; color: #333; margin-bottom: 10px; }

/* 医疗服务 */
.service-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 16px; }
.service-card {
  background: #fff; border-radius: 15px; padding: 15px;
  display: flex; flex-direction: column; align-items: flex-start; gap: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04); cursor: pointer;
}
.service-icon { width: 40px; height: 40px; margin-bottom: 8px; }
.service-name { font-size: 16px; font-weight: 500; color: #333; letter-spacing: 1px; }
.service-desc { font-size: 11px; color: #797979; }

/* 实用工具 */
.tools-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 20px; }
.tool-card {
  background: #FBFBFB; border-radius: 15px; padding: 20px 10px;
  display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer;
}
.tool-icon { width: 42px; height: 42px; }
.tool-name { font-size: 15px; color: #333; letter-spacing: 1px; }
</style>

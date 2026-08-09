<template>
  <div class="page guinea-page">
    <StatusBar />
    <div class="guinea-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>荷兰猪</h3>
    </div>
    <div class="guinea-content">
      <!-- Hero -->
      <div class="info-card">
        <img src="/images/荷兰猪/u764.png" class="pet-main-img" />
        <h3>豚鼠（荷兰猪）</h3>
        <p class="sci-name">Cavia porcellus</p>
        <div class="tag-row">
          <span class="tag-badge">啮齿类</span>
          <span class="tag-badge">群居动物</span>
        </div>
      </div>

      <!-- Overview -->
      <div class="detail-section">
        <h4>Overview 概述</h4>
        <p class="overview-text">荷兰猪，学名豚鼠，是一种温顺且富有社交能力的啮齿类动物。它们以其独特的高频叫声、易于亲近的性格和多样化的毛色深受宠主喜爱。作为草食性动物，它们需要特定的饮食和充足的活动空间，是非常理想的室内伴侣动物。</p>
      </div>

      <!-- Basic Profile -->
      <div class="detail-section">
        <h4>Basic Species Profile 基础档案</h4>
        <div class="profile-grid">
          <div class="profile-item">
            <span class="profile-label">成年体长</span>
            <span class="profile-value">20 - 25 cm</span>
          </div>
          <div class="profile-item">
            <span class="profile-label">标准体重</span>
            <span class="profile-value">700g - 1200g</span>
          </div>
          <div class="profile-item">
            <span class="profile-label">平均心率</span>
            <span class="profile-value">240 - 350bpm</span>
          </div>
          <div class="profile-item">
            <span class="profile-label">适宜温度</span>
            <span class="profile-value">18 - 24℃</span>
          </div>
        </div>
      </div>

      <!-- Core Habits -->
      <div class="detail-section">
        <h4>Core Habits 核心习性</h4>
        <div class="habit-item">
          <span class="habit-dot"></span>
          <div>
            <strong>群居属性</strong>
            <p>极度渴望同类陪伴，建议成对饲养以防抑郁。</p>
          </div>
        </div>
        <div class="habit-item">
          <span class="habit-dot"></span>
          <div>
            <strong>持续进食</strong>
            <p>肠胃系统需要不断有高纤维草料通过以维持健康。</p>
          </div>
        </div>
        <div class="habit-item">
          <span class="habit-dot"></span>
          <div>
            <strong>语言</strong>
            <p>通过呼噜、吱吱等声音表达情绪。</p>
          </div>
        </div>
      </div>

      <!-- Main Varieties -->
      <div class="detail-section">
        <h4>Main Varieties 主要品种</h4>
        <div class="variety-item">
          <img src="/images/荷兰猪/u793.png" class="variety-img" />
          <div class="variety-info">
            <p class="variety-name">短毛美国豚鼠</p>
            <p class="variety-desc">最经典、易打理</p>
          </div>
        </div>
        <div class="variety-item">
          <img src="/images/荷兰猪/u794.png" class="variety-img" />
          <div class="variety-info">
            <p class="variety-name">阿比西尼亚豚鼠</p>
            <p class="variety-desc">标志性的旋涡状毛发</p>
          </div>
        </div>
        <div class="variety-item">
          <img src="/images/荷兰猪/u795.png" class="variety-img" />
          <div class="variety-info">
            <p class="variety-name">秘鲁长毛豚鼠</p>
            <p class="variety-desc">长发，需打理</p>
          </div>
        </div>
      </div>

      <!-- Care Plan -->
      <div class="detail-section">
        <h4>Standard Care Plan 护理方案</h4>
        <div class="care-item">
          <img src="/images/荷兰猪/u804.svg" class="care-icon" />
          <div>
            <strong>基础饮食 (80/15/5)</strong>
            <p>80% 提摩西草 + 15% 颗粒粮 + 5% 新鲜蔬果（特别是富含VC的）。</p>
          </div>
        </div>
        <div class="care-item">
          <img src="/images/荷兰猪/u808.svg" class="care-icon" />
          <div>
            <strong>居住环境</strong>
            <p>每只豚鼠至少 0.7 平方米的空间，底部需垫软垫保护脚掌。</p>
          </div>
        </div>
        <div class="care-item">
          <img src="/images/荷兰猪/u812.svg" class="care-icon" />
          <div>
            <strong>健康监测</strong>
            <p>每日观察食欲、排泄情况，每周称重，定期修剪指甲。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import StatusBar from '../components/StatusBar.vue'
</script>

<style scoped>
.guinea-page {
  background: #F0F8F0;
}
.guinea-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
  background: #F0F8F0;
}
.guinea-header h3 {
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
.guinea-content {
  padding: 15px;
}

/* Hero Card */
.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 15px;
}
.pet-main-img {
  width: 120px;
  height: auto;
  margin-bottom: 12px;
  border-radius: 8px;
}
.info-card h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 4px;
}
.sci-name {
  font-size: 13px;
  color: #999;
  font-style: italic;
  margin-bottom: 8px;
}
.tag-row {
  display: flex;
  gap: 8px;
}
.tag-badge {
  background: #26A65B;
  color: #fff;
  font-size: 12px;
  padding: 4px 14px;
  border-radius: 20px;
}

/* Sections */
.detail-section {
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
}
.detail-section h4 {
  font-size: 15px;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}
.overview-text {
  font-size: 13px;
  color: #666;
  line-height: 1.7;
}

/* Profile Grid */
.profile-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.profile-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.profile-label {
  font-size: 12px;
  color: #999;
}
.profile-value {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

/* Habits */
.habit-item {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}
.habit-item:last-child {
  margin-bottom: 0;
}
.habit-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #26A65B;
  flex-shrink: 0;
  margin-top: 6px;
}
.habit-item strong {
  color: #333;
  display: block;
  font-size: 14px;
}
.habit-item p {
  margin: 0;
}

/* Varieties */
.variety-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.variety-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.variety-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}
.variety-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  margin-bottom: 2px;
}
.variety-desc {
  font-size: 12px;
  color: #999;
}

/* Care */
.care-item {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}
.care-item:last-child {
  margin-bottom: 0;
}
.care-icon {
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  margin-top: 2px;
}
.care-item strong {
  color: #333;
  display: block;
  margin-bottom: 2px;
}
.care-item p {
  margin: 0;
}
</style>

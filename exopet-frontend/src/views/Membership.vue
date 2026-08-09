<template>
  <div class="page membership-page">
    <StatusBar />
    <NavHeader title="会员中心" />
    <div class="membership-content">
      <!-- User Info -->
      <div class="user-section">
        <div class="user-avatar">
          <img :src="userStore.userInfo.avatar || '/images/会员中心/u1556.svg'" class="avatar-img" />
        </div>
        <div class="user-info">
          <p class="user-name">{{ userStore.userInfo.nickname || '^-^' }}</p>
          <p class="user-expire">会员有效期至：{{ memberInfo.expireDate || '暂无' }}</p>
        </div>
        <img src="/images/会员中心/u1559.svg" class="member-badge" />
      </div>

      <!-- Plans -->
      <div class="plans-section">
        <div class="plan-card" v-for="plan in plans" :key="plan.id">
          <div class="plan-header">
            <span class="plan-label">{{ plan.name }}</span>
          </div>
          <div class="plan-price">
            <span class="price-symbol">￥</span>
            <span class="price-num">{{ plan.price }}</span>
          </div>
          <van-button
            :round="true" :color="plan.recommended ? '#26A65B' : '#26A65B'"
            :plain="!plan.recommended" size="small" class="plan-btn"
            :loading="activating === plan.id" @click="activatePlan(plan)">
            {{ plan.recommended ? '立即开通' : '立即开通' }}
          </van-button>
        </div>
      </div>

      <!-- Benefits -->
      <div class="benefits-section">
        <img src="/images/会员中心/u1567.png" class="benefits-img" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { showToast } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import request from '../utils/request.js'
import { useUserStore } from '../stores/user.js'

const userStore = useUserStore()
const activating = ref(null)
const plans = ref([])
const memberInfo = reactive({
  expireDate: '',
  level: ''
})

onMounted(async () => {
  try {
    const [planData, memberData] = await Promise.all([
      request.get('/member/plan/list').catch(() => [
        { id: 1, name: '月卡', price: '89.9', recommended: false },
        { id: 2, name: '年卡', price: '599', recommended: true }
      ]),
      request.get(`/member/user/${userStore.userId}`).catch(() => ({}))
    ])
    plans.value = planData || []
    if (memberData) {
      memberInfo.expireDate = memberData.expireDate || ''
      memberInfo.level = memberData.level || ''
    }
  } catch {
    plans.value = [
      { id: 1, name: '月卡', price: '89.9', recommended: false },
      { id: 2, name: '年卡', price: '599', recommended: true }
    ]
  }
})

async function activatePlan(plan) {
  activating.value = plan.id
  try {
    await request.post('/member/order', {
      userId: userStore.userId,
      planId: plan.id,
      amount: plan.price
    })
    showToast('开通成功')
  } catch {
    showToast('开通失败，请重试')
  } finally {
    activating.value = null
  }
}
</script>

<style scoped>
.membership-page { background: #F0F8F0; }
.membership-content { padding: 0; }

.user-section {
  background: #fff; display: flex; align-items: center;
  gap: 12px; padding: 20px 15px; position: relative;
}
.user-avatar { width: 50px; height: 50px; border-radius: 50%; overflow: hidden; }
.avatar-img { width: 100%; height: 100%; }
.user-name { font-size: 16px; color: #333; font-weight: 500; margin-bottom: 2px; }
.user-expire { font-size: 12px; color: #999; }
.member-badge { position: absolute; right: 15px; top: 20px; width: 60px; height: auto; }

.plans-section { display: flex; gap: 10px; padding: 15px; }
.plan-card {
  flex: 1; background: #fff; border-radius: 12px; padding: 20px 15px;
  display: flex; flex-direction: column; align-items: center;
  gap: 8px; border: 1px solid #e8e8e8;
}
.plan-card-alt { border-color: #26A65B; }
.plan-label { font-size: 14px; color: #333; font-weight: 500; }
.plan-price { display: flex; align-items: baseline; }
.price-symbol { font-size: 14px; color: #FF4444; }
.price-num { font-size: 24px; color: #FF4444; font-weight: 700; }
.plan-btn { width: 100%; }
.benefits-section { padding: 0 15px; }
.benefits-img { width: 100%; height: auto; border-radius: 12px; }
</style>

<style scoped>
.membership-page {
  background: #F0F8F0;
}
.membership-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
  background: #F0F8F0;
}
.membership-header h3 {
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
.membership-content {
  padding: 0;
}

/* User Section */
.user-section {
  background: #fff;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 15px;
  position: relative;
}
.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
}
.avatar-img {
  width: 100%;
  height: 100%;
}
.user-name {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  margin-bottom: 2px;
}
.user-expire {
  font-size: 12px;
  color: #999;
}
.member-badge {
  position: absolute;
  right: 15px;
  top: 20px;
  width: 60px;
  height: auto;
}

/* Plans */
.plans-section {
  display: flex;
  gap: 10px;
  padding: 15px;
}
.plan-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 20px 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  border: 1px solid #e8e8e8;
}
.plan-card-alt {
  border-color: #26A65B;
}
.plan-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.plan-price {
  display: flex;
  align-items: baseline;
}
.price-symbol {
  font-size: 14px;
  color: #FF4444;
}
.price-num {
  font-size: 24px;
  color: #FF4444;
  font-weight: 700;
}
.plan-btn {
  width: 100%;
}

/* Benefits */
.benefits-section {
  padding: 0 15px;
}
.benefits-img {
  width: 100%;
  height: auto;
  border-radius: 12px;
}
</style>

<template>
  <div class="page specialist-page">
    <StatusBar />
    <div class="specialist-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>专人问诊</h3>
    </div>
    <div class="specialist-content">
      <div class="loading-box" v-if="loading">
        <van-loading>加载中...</van-loading>
      </div>
      <div class="doctor-card" v-for="doc in doctors" :key="doc.id" v-else>
        <div class="doctor-avatar">
          <img :src="doc.avatar || '/images/首页/u96.svg'" class="doc-avatar" />
        </div>
        <div class="doctor-info">
          <p class="doctor-name">{{ doc.name }}</p>
          <p class="doctor-title">{{ doc.title }}</p>
          <p class="doctor-desc">{{ doc.intro || (doc.yearsExp + '年经验 | ' + doc.hospitalName) }}</p>
        </div>
        <van-button round size="small" color="#26A65B" @click="startConsult(doc)">咨询</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '../utils/request.js'
import StatusBar from '../components/StatusBar.vue'
import { useUserStore } from '../stores/user.js'

const router = useRouter()
const userStore = useUserStore()
const doctors = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    doctors.value = await request.get('/consult/doctor/list') || []
  } catch (e) {
    console.error('获取医生列表失败', e)
  } finally {
    loading.value = false
  }
})

async function startConsult(doc) {
  if (!doc?.id) {
    showToast('医生信息有误')
    return
  }
  try {
    const order = await request.post('/consult/order', {
      orderNo: 'W' + Date.now(),
      userId: userStore.userId,
      doctorId: doc.id,
      type: 2,
      status: 0,
      amount: doc.priceText || 29.90
    })
    router.push('/dialog?orderNo=' + order.orderNo)
  } catch (e) {
    showToast('发起问诊失败')
  }
}
</script>

<style scoped>
.specialist-page { background: #F0F8F0; }
.specialist-header {
  display: flex; align-items: center; padding: 8px 15px; gap: 10px;
}
.specialist-header h3 { font-size: 18px; color: #333; margin: 0; }
.back-btn {
  display: flex; align-items: center; justify-content: center;
  width: 30px; height: 30px;
}
.loading-box { display: flex; justify-content: center; padding: 50px 0; }
.specialist-content { padding: 15px; display: flex; flex-direction: column; gap: 12px; }
.doctor-card {
  display: flex; align-items: center; gap: 12px;
  background: #fff; border-radius: 12px; padding: 15px;
}
.doctor-avatar { width: 50px; height: 50px; border-radius: 50%; overflow: hidden; }
.doc-avatar { width: 100%; height: 100%; object-fit: cover; }
.doctor-info { flex: 1; }
.doctor-name { font-size: 16px; font-weight: 600; color: #333; margin: 0 0 2px; }
.doctor-title { font-size: 12px; color: #26A65B; margin: 0 0 2px; }
.doctor-desc { font-size: 11px; color: #999; margin: 0; }
</style>

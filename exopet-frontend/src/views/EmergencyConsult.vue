<template>
  <div class="page emergency-page">
    <StatusBar />
    <NavHeader title="急诊问诊" />
    <div class="emergency-content">
      <!-- Patient Selection -->
      <div class="section-label">选择患者</div>
      <PageState :loading="petsLoading" :empty="pets.length === 0 && !petsLoading" empty-text="请先在「我的爱宠」添加宠物">
        <div class="patient-tabs">
          <div class="patient-tab" v-for="(pet, idx) in pets" :key="pet.id"
            :class="{ active: activePetIdx === idx }" @click="activePetIdx = idx">
            <img :src="pet.avatar || '/images/急诊问诊/u425.svg'" class="patient-icon" />
            <span>{{ pet.name }}</span>
          </div>
          <div class="patient-tab patient-add" @click="$router.push('/my-pets')">
            <span>+</span>
          </div>
        </div>
      </PageState>

      <!-- Consultation Area -->
      <div class="consult-card">
        <div class="symptom-area">
          <p class="symptom-title">详细描述症状</p>
          <van-field v-model="symptom" type="textarea" rows="3" placeholder="请描述宠物症状..." :border="false" />
          <div class="symptom-upload-row">
            <div class="upload-btn" @click="triggerEmUpload">
              <van-icon name="photo-o" size="20" color="#666" />
            </div>
            <input type="file" ref="emImageInputRef" accept="image/*" style="display:none" @change="onEmImageSelected" />
            <span class="upload-hint" v-if="!emPreview">可上传图片辅助诊断</span>
            <div class="em-preview" v-if="emPreview">
              <img :src="emPreview" @click="showImagePreview([emPreview])" />
              <van-icon name="close" class="preview-close" @click="emPreview='';emImage=null" />
            </div>
          </div>
        </div>

        <div class="doctor-match">
          <div class="doctor-match-info">
            <img src="/images/急诊问诊/u458.svg" class="doctor-avatar-small" />
            <span>成功为您匹配医生</span>
          </div>
        </div>

        <div class="badge-row">
          <span class="badge-item">
            <img src="/images/急诊问诊/u449.svg" class="badge-icon" />
            三甲医生问诊开药
          </span>
          <span class="badge-item">
            <img src="/images/急诊问诊/u449.svg" class="badge-icon" />
            专业服务
          </span>
          <span class="badge-item">
            <img src="/images/急诊问诊/u449.svg" class="badge-icon" />
            品质保障
          </span>
        </div>

        <div class="info-tags">
          <span class="info-tag">三甲专家医生</span>
          <span class="info-tag">接诊量1000+</span>
        </div>

        <div class="payment-row">
          <div class="price-box">
            <span class="price-symbol">￥</span>
            <span class="price-main">{{ price }}</span>
          </div>
          <div class="payment-status">医生等待接诊，支付后可沟通...</div>
          <van-button round color="#26A65B" class="pay-btn" :loading="paying" @click="handlePay">去支付</van-button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showImagePreview } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import PageState from '../components/PageState.vue'
import request from '../utils/request.js'
import { useUserStore } from '../stores/user.js'

const userStore = useUserStore()
const pets = ref([])
const petsLoading = ref(true)
const activePetIdx = ref(0)
const symptom = ref('')
const paying = ref(false)
const price = ref('39.9')
const emImageInputRef = ref(null)
const emImage = ref(null)
const emPreview = ref('')

function triggerEmUpload() { emImageInputRef.value?.click() }
function onEmImageSelected(e) {
  const file = e.target.files?.[0]
  if (!file) return
  emImage.value = file
  emPreview.value = URL.createObjectURL(file)
  e.target.value = ''
}

onMounted(async () => {
  try {
    const data = await request.get(`/pet/list/by-user/${userStore.userId}`)
    pets.value = data || []
  } catch {
    pets.value = []
  } finally {
    petsLoading.value = false
  }
})

async function handlePay() {
  if (!symptom.value.trim()) {
    showToast('请描述宠物症状')
    return
  }
  const activePet = pets.value[activePetIdx.value]
  if (!activePet) {
    showToast('请选择患者')
    return
  }
  paying.value = true
  try {
    const order = await request.post('/consult/order', {
      orderNo: 'E' + Date.now(),
      userId: userStore.userId,
      petId: activePet.id,
      type: 4,
      status: 0,
      amount: price.value,
      symptomDesc: symptom.value.trim()
    })
    showToast('支付成功，请等待医生接诊')
    // 跳转对话框
    if (order?.orderNo) {
      // router.push('/dialog?orderNo=' + order.orderNo)
    }
  } catch {
    showToast('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

</script>

<style scoped>
.emergency-page { background: #F0F8F0; }
.emergency-content { padding: 0 15px 20px; }

.section-label { font-size: 14px; color: #333; font-weight: 500; margin: 10px 0 8px; }
.patient-tabs { display: flex; gap: 8px; margin-bottom: 12px; overflow-x: auto; }
.patient-tab {
  background: #fff; border-radius: 20px; padding: 6px 14px;
  display: flex; align-items: center; gap: 4px; font-size: 13px;
  color: #333; cursor: pointer; border: 1px solid transparent;
  white-space: nowrap;
}
.patient-tab.active { border-color: #26A65B; color: #26A65B; }
.patient-icon { width: 20px; height: 20px; }
.patient-add {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  padding: 0; font-size: 18px; color: #999;
}

.consult-card { background: #fff; border-radius: 12px; padding: 15px; margin-bottom: 12px; }
.symptom-title { font-size: 13px; color: #333; margin-bottom: 4px; }
.symptom-area :deep(.van-field) { background: #F9F9F9; border-radius: 8px; padding: 10px; }
.symptom-upload-row { display: flex; align-items: center; gap: 8px; margin-top: 8px; }
.upload-hint { font-size: 11px; color: #bbb; }
.em-preview { position: relative; display: inline-block; }
.em-preview img { width: 50px; height: 50px; object-fit: cover; border-radius: 6px; border: 1px solid #e0e0e0; }
.doctor-match { margin: 10px 0; }
.doctor-match-info { display: flex; align-items: center; gap: 8px; font-size: 12px; color: #666; }
.doctor-avatar-small { width: 30px; height: 30px; border-radius: 50%; }

.badge-row { display: flex; gap: 6px; margin: 8px 0; }
.badge-item { font-size: 11px; color: #666; display: flex; align-items: center; gap: 2px; }
.badge-icon { width: 12px; height: 12px; }

.info-tags { display: flex; gap: 8px; margin-bottom: 10px; }
.info-tag { font-size: 12px; color: #26A65B; background: #E8F5E9; padding: 3px 10px; border-radius: 12px; }

.payment-row { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.price-box { display: flex; align-items: baseline; }
.price-symbol { font-size: 13px; color: #FF4444; }
.price-main { font-size: 23px; color: #FF4444; font-weight: 700; }
.payment-status { font-size: 11px; color: #999; flex: 1; }
.pay-btn { flex-shrink: 0; }

</style>

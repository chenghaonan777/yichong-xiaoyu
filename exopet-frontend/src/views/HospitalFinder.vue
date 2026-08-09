<template>
  <div class="page hospital-page">
    <StatusBar />
    <div class="hospital-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>医院查找</h3>
    </div>

    <van-search
      v-model="keyword"
      shape="round"
      background="#F0F8F0"
      placeholder="搜索医院名称"
    />

    <!-- ===== 地图区域 ===== -->
    <div class="map-wrapper">
      <div ref="mapRef" style="width:100%;height:35vh"></div>
      <!-- 右下角定位按钮 -->
      <van-button
        class="locate-btn"
        round
        icon="location-o"
        size="small"
        type="primary"
        @click="locateUser"
      />
    </div>

    <!-- ===== 医院列表 ===== -->
    <div class="hospital-list-wrapper">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoadMore"
      >
        <div
          class="hospital-card"
          v-for="(h, idx) in hospitals"
          :key="h.id || idx"
          :class="{ active: activeHospitalId === h.id }"
          @click="onCardClick(h)"
        >
          <div class="hospital-img-wrapper">
            <img
              v-if="h.coverImage"
              :src="h.coverImage"
              class="hospital-img"
            />
            <div v-else class="hospital-img-placeholder">
              <span class="placeholder-icon">🏥</span>
            </div>
          </div>
          <div class="hospital-info">
            <p class="hospital-name">{{ h.name }}</p>
            <p class="hospital-addr">{{ h.address }}</p>
            <div class="hospital-meta">
              <span class="distance" v-if="h._distance">{{ h._distance }}</span>
              <span class="rating">评分：{{ h.rating ?? '-' }}</span>
            </div>
          </div>
        </div>
      </van-list>

  <!-- ===== InfoWindow 底部按钮（absolute 固定在底部） ===== -->
  <div class="info-btn-bar" v-if="activeHospitalId">
    <van-button round color="#26A65B" size="small" @click="onAppointClick" style="flex:1">
      预约就诊
    </van-button>
    <van-button round plain color="#26A65B" size="small" @click="onReviewClick" style="flex:1">
      查看评价
    </van-button>
  </div>
    </div>
  </div>

  <!-- ===== 预约表单 ===== -->
  <van-action-sheet v-model:show="showAppoint" title="预约就诊" closeable>
    <div class="sheet-form">
      <van-form @submit="submitAppoint">
        <van-field v-model="appointForm.appointDate" is-link readonly name="appointDate"
          label="预约日期" placeholder="请选择日期"
          :rules="[{ required: true, message: '请选择日期' }]"
          @click="showDatePicker = true" />
        <van-field v-model="appointForm.timeSlot" is-link readonly name="timeSlot"
          label="时间段" placeholder="请选择时间段"
          :rules="[{ required: true, message: '请选择时间段' }]"
          @click="showTimePicker = true" />
        <van-field v-model="appointForm.contactName" name="contactName"
          label="联系人" placeholder="请输入联系人" />
        <van-field v-model="appointForm.contactPhone" name="contactPhone"
          label="联系电话" type="tel" maxlength="11" placeholder="请输入联系电话" />
        <van-field v-model="appointForm.remark" name="remark" rows="3"
          type="textarea" label="备注" placeholder="如有特殊需求请备注" />
        <div class="form-actions">
          <van-button round block color="#26A65B" native-type="submit" :loading="submitting">
            提交预约
          </van-button>
        </div>
      </van-form>
    </div>
  </van-action-sheet>

  <!-- ===== 日期选择器 ===== -->
  <van-popup v-model:show="showDatePicker" position="bottom">
    <van-date-picker :min-date="minDate" @confirm="confirmDate" @cancel="showDatePicker = false" />
  </van-popup>

  <!-- ===== 时间段选择器 ===== -->
  <van-action-sheet v-model:show="showTimePicker" :actions="timeSlots"
    @select="selectTimeSlot" cancel-text="取消" />

  <!-- ===== 评价列表 ===== -->
  <van-action-sheet v-model:show="showReviews" title="用户评价" closeable>
    <div class="sheet-form">
      <template v-if="reviews.length === 0 && !reviewLoading">
        <div class="empty-state">
          <p style="color:#999;text-align:center;padding:30px 0;">暂无评价</p>
        </div>
      </template>
      <div v-for="r in reviews" :key="r.id" class="review-card">
        <div class="review-header">
          <van-rate :model-value="r.rating" readonly size="16" color="#FFB800" void-icon="star" void-color="#eee" />
          <span class="review-date">{{ formatDate(r.createdAt) }}</span>
        </div>
        <p class="review-content">{{ r.content }}</p>
      </div>
      <div class="form-actions">
        <van-button round block color="#26A65B" plain @click="openReviewForm">
          写评价
        </van-button>
      </div>
    </div>
  </van-action-sheet>

  <!-- ===== 提交评价表单 ===== -->
  <van-action-sheet v-model:show="showReviewForm" title="写评价" closeable>
    <div class="sheet-form">
      <div class="review-rating-row">
        <span class="review-label">评分：</span>
        <van-rate v-model="reviewForm.rating" size="24" color="#FFB800" void-icon="star" void-color="#eee" />
      </div>
      <van-field v-model="reviewForm.content" type="textarea" rows="4"
        maxlength="500" placeholder="分享您的就诊体验..." show-word-limit
        :rules="[{ required: true, message: '请输入评价内容' }]" />
      <div class="form-actions">
        <van-button round block color="#26A65B" :loading="submitting" @click="submitReview">
          提交评价
        </van-button>
      </div>
    </div>
  </van-action-sheet>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import request from '../utils/request.js'
import { loadAMap } from '../utils/amap.js'
import { useUserStore } from '../stores/user.js'
import { useNotificationStore } from '../stores/notification.js'

// ---------- 响应式状态 ----------
const keyword = ref('')
const hospitals = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const activeHospitalId = ref(null)
const mapRef = ref(null)

// ---------- 预约 & 评价 ----------
const router = useRouter()
const userStore = useUserStore()
const notifStore = useNotificationStore()
const currentHospital = ref(null)
const showAppoint = ref(false)
const showReviews = ref(false)
const showReviewForm = ref(false)
const submitting = ref(false)

const appointForm = ref({
  appointDate: '',
  timeSlot: '',
  contactName: '',
  contactPhone: '',
  petId: '',
  remark: '',
})
const timeSlots = [
  { name: '上午 9:00-12:00', value: '09:00-12:00' },
  { name: '下午 14:00-18:00', value: '14:00-18:00' },
  { name: '晚间 18:00-21:00', value: '18:00-21:00' },
]

const reviews = ref([])
const reviewLoading = ref(false)
const reviewFinished = ref(false)
const reviewPage = ref(1)
const reviewForm = ref({ rating: 5, content: '' })

// ---------- 地图实例（非响应式，避免 Vue 代理重量级对象） ----------
let mapInstance = null
let markers = []
let geolocation = null
let infoWindow = null
let userLocation = { lat: null, lng: null, located: false }

const PAGE_SIZE = 20

// ========== API 请求 ==========
async function fetchHospitals(p) {
  try {
    const params = { page: p, size: PAGE_SIZE }
    if (keyword.value) params.keyword = keyword.value

    // request 拦截器返回 body.data，后端接口格式为 { code, data: { records, total, ... }, msg }
    const result = await request.get('/hospital/list', { params })
    const records = result.records || []

    if (p === 1) {
      hospitals.value = records
    } else {
      hospitals.value = hospitals.value.concat(records)
    }

    finished.value = hospitals.value.length >= result.total

    // 如果有用户位置，计算距离
    if (userLocation.located) {
      calcDistances()
    }

    // 在地图上渲染这批医院
    renderMarkers(records, p === 1)
  } catch (e) {
    // 请求失败，停止加载
    if (page.value === 1) {
      hospitals.value = []
    }
    finished.value = true
    loading.value = false
  }
}

// ========== 地图 Markers ==========
function clearMarkers() {
  markers.forEach(m => mapInstance?.remove(m))
  markers = []
}

function renderMarkers(records, reset) {
  if (!mapInstance) return
  if (reset) clearMarkers()

  records.forEach(h => {
    if (!h.latitude || !h.longitude) return
    const position = [Number(h.longitude), Number(h.latitude)]

    const marker = new AMap.Marker({
      position,
      title: h.name,
      extData: h.id,
    })
    marker._hospital = h

    marker.on('click', () => onMarkerClick(h, marker))

    mapInstance.add(marker)
    markers.push(marker)
  })

  // 重置时调整视野到所有 Marker（try-catch 防止 NaN 错误）
  if (reset && markers.length > 0) {
    try {
      mapInstance.setFitView(markers, false, 80)
    } catch (_) {
      // setFitView 在地图未完全就绪时可能抛出 NaN 错误，静默忽略
    }
  }
}

// ========== 联动交互 ==========

/** 点击列表卡片 → 跳转医院详情页 */
function onCardClick(h) {
  router.push('/hospital-detail/' + h.id)
}

/** 点击地图 Marker → 弹出 InfoWindow + 列表滚动高亮 */
function onMarkerClick(h, marker) {
  showInfoWindow(h, marker.getPosition())
  activeHospitalId.value = h.id

  // 滚动列表到对应卡片
  nextTick(() => {
    const el = document.querySelector('.hospital-card.active')
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  })

  // 3 秒后自动取消高亮
  setTimeout(() => {
    if (activeHospitalId.value === h.id) activeHospitalId.value = null
  }, 3000)
}

// ========== InfoWindow（单例）==========
function showInfoWindow(h, position) {
  if (!infoWindow) {
    infoWindow = new AMap.InfoWindow({ offset: new AMap.Pixel(0, -30) })
  }

  const phoneHtml = h.phone ? `<p>📞 ${h.phone}</p>` : ''
  const hoursHtml = h.businessHours ? `<p>🕐 ${h.businessHours}</p>` : ''

  infoWindow.setContent(`
    <div style="min-width:180px; font-size:13px;">
      <h4 style="margin:0 0 4px; font-size:15px;">${h.name}</h4>
      <p style="margin:2px 0; color:#666;">${h.address}</p>
      ${phoneHtml}
      ${hoursHtml}
      <p style="margin:2px 0; color:#26A65B;">⭐ ${h.rating ?? '-'} 分</p>
    </div>
  `)
  infoWindow.open(mapInstance, position)
}

// ========== 用户定位 ==========
function locateUser() {
  if (!geolocation) return
  geolocation.getCurrentPosition((status, result) => {
    if (status === 'complete') {
      const pos = result.position
      userLocation = { lat: pos.lat, lng: pos.lng, located: true }
      mapInstance.setCenter([pos.lng, pos.lat])
      calcDistances()
    } else {
      showToast('定位失败，请检查定位权限')
    }
  })
}

// ========== 距离计算：Haversine 公式 ==========
function calcDistance(lat1, lng1, lat2, lng2) {
  const R = 6371 // 地球半径 km
  const dLat = ((lat2 - lat1) * Math.PI) / 180
  const dLng = ((lng2 - lng1) * Math.PI) / 180
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLng / 2) ** 2
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

function calcDistances() {
  if (!userLocation.located) return
  hospitals.value = hospitals.value.map(h => {
    if (h.latitude && h.longitude) {
      const km = calcDistance(
        userLocation.lat,
        userLocation.lng,
        Number(h.latitude),
        Number(h.longitude)
      )
      h._distance = km < 1 ? `${(km * 1000).toFixed(0)}m` : `${km.toFixed(1)}km`
    }
    return h
  })
}

// ========== 搜索防抖 300ms ==========
let searchTimer = null
watch(keyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    page.value = 1
    hospitals.value = []
    finished.value = false
    fetchHospitals(1)
  }, 300)
})

// ========== 上拉加载更多 ==========
async function onLoadMore() {
  page.value++
  await fetchHospitals(page.value)
  loading.value = false
}

// ========== 预约 & 评价 ==========

// 额外状态
const showDatePicker = ref(false)
const showTimePicker = ref(false)
const minDate = new Date()

function confirmDate({ selectedValues }) {
  appointForm.value.appointDate = selectedValues.join('-')
  showDatePicker.value = false
}

function selectTimeSlot(action) {
  appointForm.value.timeSlot = action.name
  showTimePicker.value = false
}

function onAppointClick() {
  const h = hospitals.value.find(x => x.id === activeHospitalId.value)
  if (!h) return
  currentHospital.value = h
  // 重置表单
  appointForm.value = { appointDate: '', timeSlot: '', contactName: '', contactPhone: '', petId: '', remark: '' }
  showAppoint.value = true
}

async function submitAppoint() {
  const h = currentHospital.value
  if (!h) return
  submitting.value = true
  try {
    await request.post('/hospital/appointment', {
      hospitalId: h.id,
      userId: userStore.userId,
      appointDate: appointForm.value.appointDate,
      timeSlot: appointForm.value.timeSlot,
      contactName: appointForm.value.contactName || undefined,
      contactPhone: appointForm.value.contactPhone || undefined,
      remark: appointForm.value.remark || undefined,
    })
    showToast('预约成功')
    showAppoint.value = false
    notifStore.addLocalNotification('预约成功', '您已成功预约 ' + (h?.name || '医院') + '，请按时就诊', 'CONSULT')
  } catch (e) {
    showToast('预约失败，请稍后重试')
    notifStore.addLocalNotification('预约失败', '预约未成功，请稍后重试', 'HEALTH')
  } finally {
    submitting.value = false
  }
}

function onReviewClick() {
  const h = hospitals.value.find(x => x.id === activeHospitalId.value)
  if (!h) return
  currentHospital.value = h
  reviewPage.value = 1
  reviews.value = []
  reviewFinished.value = false
  showReviews.value = true
  fetchReviews()
}

async function fetchReviews() {
  const h = currentHospital.value
  if (!h || reviewLoading.value) return
  reviewLoading.value = true
  try {
    const result = await request.get(`/hospital/review/list/${h.id}`, {
      params: { page: reviewPage.value, size: 20 },
    })
    const records = result.records || []
    if (reviewPage.value === 1) {
      reviews.value = records
    } else {
      reviews.value = reviews.value.concat(records)
    }
    reviewFinished.value = reviews.value.length >= result.total
  } catch (e) {
    showToast('加载评价失败')
  } finally {
    reviewLoading.value = false
  }
}

function openReviewForm() {
  showReviewForm.value = true
  reviewForm.value = { rating: 5, content: '' }
}

async function submitReview() {
  if (!reviewForm.value.content.trim()) {
    showToast('请输入评价内容')
    return
  }
  const h = currentHospital.value
  if (!h) return
  submitting.value = true
  try {
    await request.post('/hospital/review', {
      hospitalId: h.id,
      userId: userStore.userId,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content,
    })
    showToast('评价成功')
    showReviewForm.value = false
    // 刷新评价列表
    reviewPage.value = 1
    reviews.value = []
    fetchReviews()
  } catch (e) {
    showToast('提交评价失败')
  } finally {
    submitting.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.slice(0, 10)
}

// ========== 初始化地图 ==========
async function initMap() {
  try {
    const AMap = await loadAMap()
    const container = mapRef.value
    if (!container) return
    mapInstance = new AMap.Map(container, {
      zoom: 12,
      center: [116.397428, 39.90923],
    })

    geolocation = new AMap.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000,
      zoomToAccuracy: true,
    })

    // 地图先就绪，再加载数据
    await fetchHospitals(1)
    locateUser()
  } catch (e) {
    console.error('地图加载失败:', e)
  }
}

// ========== 生命周期 ==========
onMounted(() => {
  initMap()
})

onBeforeUnmount(() => {
  clearMarkers()
  mapInstance?.destroy()
  mapInstance = null
})
</script>

<style scoped>
.hospital-page {
  background: #f0f8f0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}
.hospital-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
}
.hospital-header h3 {
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

/* ===== 地图区域 ===== */
.map-wrapper {
  position: relative;
  flex-shrink: 0;
}
.locate-btn {
  position: absolute !important;
  right: 12px;
  bottom: 12px;
  z-index: 10;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

/* ===== 列表区域 ===== */
.hospital-list-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 10px 15px 60px;
}
.hospital-card {
  display: flex;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  transition: all 0.2s;
  border: 1.5px solid transparent;
  margin-bottom: 10px;
}
.hospital-card.active {
  border-color: #26a65b;
  background: #e8f5e9;
}
.hospital-img-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
  overflow: hidden;
}
.hospital-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.hospital-img-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}
.placeholder-icon {
  font-size: 32px;
}
.hospital-info {
  flex: 1;
  min-width: 0;
}
.hospital-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hospital-addr {
  font-size: 12px;
  color: #999;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hospital-meta {
  display: flex;
  gap: 12px;
}
.distance,
.rating {
  font-size: 11px;
  color: #26a65b;
}

/* ===== InfoWindow 底部按钮（absolute 固定在页面底部） ===== */
.info-btn-bar {
  display: flex;
  gap: 10px;
  padding: 10px 15px 16px;
  background: #f0f8f0;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.08);
}

/* ===== Action Sheet 表单容器 ===== */
.sheet-form {
  padding: 20px 16px;
  max-height: 60vh;
  overflow-y: auto;
}
.form-actions {
  padding: 20px 0;
}
.empty-state {
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ===== 评价卡片 ===== */
.review-card {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
}
.review-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.review-date {
  font-size: 11px;
  color: #999;
}
.review-content {
  font-size: 13px;
  color: #333;
  margin: 0;
  line-height: 1.5;
}
.review-rating-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 16px 16px;
}
.review-label {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
}
</style>

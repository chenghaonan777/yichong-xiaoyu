<template>
  <div class="page detail-page">
    <StatusBar />
    <NavHeader title="医院详情" />
    <PageState :loading="loading" :error="!hospital && !loading" error-text="医院信息加载失败">
      <div class="detail-content" v-if="hospital">
        <div class="detail-cover" v-if="hospital.coverImage">
          <img :src="hospital.coverImage" />
        </div>
        <div class="info-card">
          <h2 class="h-name">{{ hospital.name }}</h2>
          <div class="h-rating-row">
            <span class="h-star">⭐ {{ hospital.rating ?? '-' }}</span>
            <span class="h-review-count" v-if="hospital.reviewCount != null">{{ hospital.reviewCount }}条评价</span>
          </div>
          <div class="h-tags" v-if="hospital.expertiseTags">
            <span class="h-tag" v-for="tag in parseTags(hospital.expertiseTags)" :key="tag">{{ tag }}</span>
          </div>
          <div class="h-info-row"><van-icon name="location-o" />{{ hospital.address }}</div>
          <div class="h-info-row" v-if="hospital.phone"><van-icon name="phone-o" />{{ hospital.phone }}</div>
          <div class="h-info-row" v-if="hospital.businessHours"><van-icon name="clock-o" />{{ hospital.businessHours }}</div>
        </div>
        <div class="info-card" v-if="hospital.intro">
          <h4 class="card-title">医院简介</h4>
          <p class="intro-text">{{ hospital.intro }}</p>
        </div>
        <div class="info-card" v-if="envImages.length > 0">
          <h4 class="card-title">环境展示</h4>
          <div class="env-imgs">
            <img v-for="(img, idx) in envImages" :key="idx" :src="img" @click="showImagePreview([img])" />
          </div>
        </div>
        <div class="action-bar">
          <van-button round color="#26A65B" block @click="showAppoint = true">预约就诊</van-button>
        </div>
        <div class="info-card">
          <div class="review-header">
            <h4 class="card-title">用户评价</h4>
            <van-button size="small" round plain color="#26A65B" @click="openReviewForm">写评价</van-button>
          </div>
          <PageState :loading="reviewLoading" :empty="reviews.length === 0 && !reviewLoading" empty-text="暂无评价">
            <div class="review-item" v-for="r in reviews" :key="r.id">
              <div class="review-top">
                <van-rate :model-value="r.rating" readonly size="14" color="#FFB800" void-icon="star" void-color="#eee" />
                <span class="review-date">{{ formatDate(r.createdAt) }}</span>
              </div>
              <p class="review-content">{{ r.content }}</p>
            </div>
          </PageState>
        </div>
      </div>
    </PageState>
    <van-action-sheet v-model:show="showAppoint" title="预约就诊" closeable>
      <div class="sheet-form">
        <van-form @submit="submitAppoint">
          <van-field v-model="appointForm.appointDate" is-link readonly name="appointDate" label="预约日期" placeholder="请选择日期"
            :rules="[{ required: true, message: '请选择日期' }]" @click="showDatePicker = true" />
          <van-field v-model="appointForm.timeSlot" is-link readonly name="timeSlot" label="时间段" placeholder="请选择时间段"
            :rules="[{ required: true, message: '请选择时间段' }]" @click="showTimePicker = true" />
          <van-field v-model="appointForm.contactName" name="contactName" label="联系人" placeholder="请输入联系人" />
          <van-field v-model="appointForm.contactPhone" name="contactPhone" label="联系电话" type="tel" maxlength="11" placeholder="请输入联系电话" />
          <van-field v-model="appointForm.remark" name="remark" rows="3" type="textarea" label="备注" placeholder="如有特殊需求请备注" />
          <div class="form-actions"><van-button round block color="#26A65B" native-type="submit" :loading="submitting">提交预约</van-button></div>
        </van-form>
      </div>
    </van-action-sheet>
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker :min-date="minDate" @confirm="confirmDate" @cancel="showDatePicker = false" />
    </van-popup>
    <van-action-sheet v-model:show="showTimePicker" :actions="timeSlots" @select="selectTimeSlot" cancel-text="取消" />
    <van-action-sheet v-model:show="showReviewForm" title="写评价" closeable>
      <div class="sheet-form">
        <div class="review-rating-row">
          <span class="review-label">评分：</span>
          <van-rate v-model="reviewForm.rating" size="24" color="#FFB800" void-icon="star" void-color="#eee" />
        </div>
        <van-field v-model="reviewForm.content" type="textarea" rows="4" maxlength="500"
          placeholder="分享您的就诊体验..." show-word-limit
          :rules="[{ required: true, message: '请输入评价内容' }]" />
        <div class="form-actions">
          <van-button round block color="#26A65B" :loading="reviewSubmitting" @click="submitReview">提交评价</van-button>
        </div>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import PageState from '../components/PageState.vue'
import request from '../utils/request.js'
import { useUserStore } from '../stores/user.js'
import { useNotificationStore } from '../stores/notification.js'

const route = useRoute()
const userStore = useUserStore()
const notifStore = useNotificationStore()
const hospital = ref(null)
const loading = ref(true)
const showAppoint = ref(false)
const showDatePicker = ref(false)
const showTimePicker = ref(false)
const showReviewForm = ref(false)
const submitting = ref(false)
const reviewSubmitting = ref(false)
const minDate = new Date()
const reviews = ref([])
const reviewLoading = ref(false)
const appointForm = ref({ appointDate: '', timeSlot: '', contactName: '', contactPhone: '', remark: '' })
const reviewForm = ref({ rating: 5, content: '' })
const timeSlots = [
  { name: '上午 9:00-12:00', value: '09:00-12:00' },
  { name: '下午 14:00-18:00', value: '14:00-18:00' },
  { name: '晚间 18:00-21:00', value: '18:00-21:00' },
]
const envImages = computed(() => {
  if (!hospital.value?.images) return []
  try { return JSON.parse(hospital.value.images) } catch { return [] }
})
function parseTags(tags) {
  if (!tags) return []
  try { const p = JSON.parse(tags); return Array.isArray(p) ? p : [] } catch { return [] }
}
function formatDate(t) { return t ? t.slice(0, 10) : '' }
function confirmDate(d) { appointForm.value.appointDate = d.selectedValues.join('-'); showDatePicker.value = false }
function selectTimeSlot(a) { appointForm.value.timeSlot = a.name; showTimePicker.value = false }
async function submitAppoint() {
  submitting.value = true
  const hName = hospital.value?.name || '医院'
  try {
    await request.post('/hospital/appointment', {
      hospitalId: hospital.value.id, userId: userStore.userId,
      appointDate: appointForm.value.appointDate, timeSlot: appointForm.value.timeSlot,
      contactName: appointForm.value.contactName || undefined,
      contactPhone: appointForm.value.contactPhone || undefined,
      remark: appointForm.value.remark || undefined,
    })
    showToast('预约成功'); showAppoint.value = false
    notifStore.addLocalNotification('预约成功', '您已成功预约 ' + hName + '，请按时就诊', 'CONSULT')
  } catch {
    showToast('预约失败')
    notifStore.addLocalNotification('预约失败', hName + ' 预约未成功，请稍后重试', 'HEALTH')
  }
  finally { submitting.value = false }
}
function openReviewForm() { reviewForm.value = { rating: 5, content: '' }; showReviewForm.value = true }
async function submitReview() {
  if (!reviewForm.value.content.trim()) { showToast('请输入评价内容'); return }
  reviewSubmitting.value = true
  try {
    await request.post('/hospital/review', {
      hospitalId: hospital.value.id, userId: userStore.userId,
      rating: reviewForm.value.rating, content: reviewForm.value.content
    })
    showToast('评价成功'); showReviewForm.value = false
    notifStore.addLocalNotification('评价成功', '您对 ' + (hospital.value?.name || '医院') + ' 的评价已提交', 'SYSTEM')
    reviews.value = []; fetchReviews()
  } catch { showToast('提交失败') }
  finally { reviewSubmitting.value = false }
}
async function fetchReviews() {
  reviewLoading.value = true
  try {
    const url = '/hospital/review/list/' + hospital.value.id
    const result = await request.get(url, { params: { page: 1, size: 20 } })
    reviews.value = result?.records || []
  } catch { reviews.value = [] }
  finally { reviewLoading.value = false }
}
onMounted(async () => {
  const id = route.params.id
  if (!id) { loading.value = false; return }
  try { hospital.value = await request.get('/hospital/' + id) }
  catch { hospital.value = null }
  finally { loading.value = false }
  if (hospital.value) fetchReviews()
})
</script>

<style scoped>
.detail-page { background: #F0F8F0; min-height: 100vh; padding-bottom: 80px; }
.detail-content { padding: 0; }
.detail-cover { width: 100%; height: 200px; overflow: hidden; }
.detail-cover img { width: 100%; height: 100%; object-fit: cover; }
.info-card { background: #fff; border-radius: 12px; padding: 15px; margin: 10px 15px; }
.h-name { font-size: 20px; color: #333; margin: 0 0 6px; }
.h-rating-row { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; font-size: 14px; }
.h-star { color: #FF8C00; }
.h-review-count { color: #999; font-size: 12px; }
.h-tags { display: flex; gap: 6px; margin-bottom: 8px; flex-wrap: wrap; }
.h-tag { background: #E8F5E9; color: #26A65B; font-size: 11px; padding: 2px 10px; border-radius: 10px; }
.h-info-row { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #666; margin-bottom: 6px; }
.card-title { font-size: 15px; font-weight: 600; color: #333; margin: 0 0 10px; }
.intro-text { font-size: 13px; color: #666; line-height: 1.7; margin: 0; }
.env-imgs { display: flex; gap: 8px; overflow-x: auto; }
.env-imgs img { width: 120px; height: 90px; border-radius: 8px; object-fit: cover; cursor: pointer; }
.action-bar { padding: 10px 15px; }
.review-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; }
.review-header .card-title { margin: 0; }
.review-item { padding: 10px 0; border-bottom: 1px solid #f0f0f0; }
.review-item:last-child { border-bottom: none; }
.review-top { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.review-date { font-size: 11px; color: #bbb; }
.review-content { font-size: 13px; color: #333; line-height: 1.5; margin: 0; }
.sheet-form { padding: 20px 16px; }
.form-actions { padding: 20px 0; }
.review-rating-row { display: flex; align-items: center; gap: 10px; padding: 0 16px 16px; }
.review-label { font-size: 14px; color: #333; white-space: nowrap; }
</style>
<template>
  <div class="page coupons-page">
    <StatusBar />
    <NavHeader title="优惠券" />
    <div class="coupons-content">
      <van-tabs v-model="activeTab" color="#26A65B" title-active-color="#26A65B">
        <van-tab title="未使用">
          <PageState :loading="loading" :empty="unusedCoupons.length === 0 && !loading" empty-icon="coupon-o" empty-text="暂无可用优惠券">
            <div class="coupon-list">
              <div class="coupon-card" v-for="c in unusedCoupons" :key="c.id">
                <div class="coupon-left">
                  <span class="coupon-price">￥{{ c.amount }}</span>
                  <span class="coupon-condition">{{ c.condition }}</span>
                </div>
                <div class="coupon-right">
                  <span class="coupon-name">{{ c.name }}</span>
                  <span class="coupon-expire">{{ formatTime(c.expireTime) }}</span>
                  <van-button size="small" round color="#26A65B" @click="useCoupon(c)">立即使用</van-button>
                </div>
              </div>
            </div>
          </PageState>
        </van-tab>
        <van-tab title="已使用">
          <PageState :loading="loading" :empty="usedCoupons.length === 0 && !loading" empty-icon="coupon-o" empty-text="暂无已使用优惠券">
            <div class="coupon-list">
              <div class="coupon-card used" v-for="c in usedCoupons" :key="c.id">
                <div class="coupon-left">
                  <span class="coupon-price">￥{{ c.amount }}</span>
                  <span class="coupon-condition">{{ c.condition }}</span>
                </div>
                <div class="coupon-right">
                  <span class="coupon-name">{{ c.name }}</span>
                  <span class="coupon-expire">{{ formatTime(c.usedTime) }} 已使用</span>
                </div>
              </div>
            </div>
          </PageState>
        </van-tab>
        <van-tab title="已过期">
          <PageState :loading="loading" :empty="expiredCoupons.length === 0 && !loading" empty-icon="coupon-o" empty-text="暂无已过期优惠券">
            <div class="coupon-list">
              <div class="coupon-card expired" v-for="c in expiredCoupons" :key="c.id">
                <div class="coupon-left">
                  <span class="coupon-price">￥{{ c.amount }}</span>
                  <span class="coupon-condition">{{ c.condition }}</span>
                </div>
                <div class="coupon-right">
                  <span class="coupon-name">{{ c.name }}</span>
                  <span class="coupon-expire">{{ formatTime(c.expireTime) }} 已过期</span>
                </div>
              </div>
            </div>
          </PageState>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import StatusBar from '../components/StatusBar.vue'
import NavHeader from '../components/NavHeader.vue'
import PageState from '../components/PageState.vue'
import request from '../utils/request.js'
import { useUserStore } from '../stores/user.js'

const userStore = useUserStore()
const activeTab = ref(0)
const loading = ref(true)
const allCoupons = ref([])

const unusedCoupons = computed(() => allCoupons.value.filter(c => c.status === 0))
const usedCoupons = computed(() => allCoupons.value.filter(c => c.status === 1))
const expiredCoupons = computed(() => allCoupons.value.filter(c => c.status === 2))

function formatTime(t) {
  if (!t) return ''
  return t.slice(0, 10)
}

function useCoupon(c) {
  showToast(`使用优惠券：${c.name}`)
}

onMounted(async () => {
  try {
    const data = await request.get(`/coupon/list/${userStore.userId}`)
    allCoupons.value = data?.records || data || []
  } catch {
    allCoupons.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.coupons-page { background: #F0F8F0; }
.coupons-content { min-height: calc(100vh - 60px); }
.coupon-list { padding: 15px; display: flex; flex-direction: column; gap: 10px; }
.coupon-card { display: flex; background: #fff; border-radius: 10px; overflow: hidden; }
.coupon-card.used { opacity: 0.6; }
.coupon-card.expired { opacity: 0.4; }
.coupon-left {
  background: #26A65B; color: #fff; padding: 15px;
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; min-width: 100px;
}
.coupon-price { font-size: 28px; font-weight: 700; }
.coupon-condition { font-size: 11px; opacity: 0.9; }
.coupon-right { padding: 12px 15px; flex: 1; display: flex; flex-direction: column; gap: 4px; }
.coupon-name { font-size: 14px; color: #333; font-weight: 500; }
.coupon-expire { font-size: 11px; color: #999; margin-bottom: 4px; }
.empty-state { display: flex; flex-direction: column; align-items: center; padding-top: 100px; gap: 12px; color: #999; font-size: 14px; }
</style>

<style scoped>
.coupons-page {
  background: #F0F8F0;
}
.coupons-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
}
.coupons-header h3 {
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
.coupons-content {
  min-height: calc(100vh - 60px);
}
.coupon-list {
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.coupon-card {
  display: flex;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
}
.coupon-left {
  background: #26A65B;
  color: #fff;
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 100px;
}
.coupon-price {
  font-size: 28px;
  font-weight: 700;
}
.coupon-condition {
  font-size: 11px;
  opacity: 0.9;
}
.coupon-right {
  padding: 12px 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.coupon-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.coupon-expire {
  font-size: 11px;
  color: #999;
  margin-bottom: 4px;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 100px;
  gap: 12px;
  color: #999;
  font-size: 14px;
}
</style>

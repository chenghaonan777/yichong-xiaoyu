<template>
  <div class="page-state">
    <!-- Loading -->
    <div v-if="loading" class="state-box">
      <van-loading size="30" color="#26A65B">{{ loadingText }}</van-loading>
    </div>
    <!-- Empty -->
    <div v-else-if="empty" class="state-box empty-box">
      <van-icon :name="emptyIcon" size="50" color="#ddd" />
      <p>{{ emptyText }}</p>
    </div>
    <!-- Error -->
    <div v-else-if="error" class="state-box error-box">
      <van-icon name="warn-o" size="50" color="#FF9800" />
      <p>{{ errorText }}</p>
      <van-button size="small" round color="#26A65B" @click="$emit('retry')">重试</van-button>
    </div>
    <!-- Default slot -->
    <slot v-else />
  </div>
</template>

<script setup>
defineProps({
  loading: Boolean,
  empty: Boolean,
  error: Boolean,
  loadingText: { type: String, default: '加载中...' },
  emptyText: { type: String, default: '暂无数据' },
  emptyIcon: { type: String, default: 'info-o' },
  errorText: { type: String, default: '加载失败' }
})
defineEmits(['retry'])
</script>

<style scoped>
.page-state { min-height: 200px; }
.state-box {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 60px 0; gap: 12px;
  color: #999; font-size: 14px;
}
</style>
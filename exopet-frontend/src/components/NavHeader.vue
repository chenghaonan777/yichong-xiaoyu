<template>
  <div class="nav-header">
    <div class="back-btn" @click="onBack">
      <van-icon name="arrow-left" size="20" color="#333" />
    </div>
    <h3>{{ title }}</h3>
    <slot name="right" />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  title: { type: String, default: '' }
})
const emit = defineEmits(['back'])
const router = useRouter()

function onBack() {
  emit('back')
  router.back()
}
</script>

<style scoped>
.nav-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
  background: inherit;
}
.nav-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
  flex: 1;
}
.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  cursor: pointer;
}
</style>
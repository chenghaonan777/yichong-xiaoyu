<template>
  <div class="page success-page">
    <StatusBar />
    <div class="success-content">
      <div class="success-icon">
        <van-icon name="success" color="#26A65B" size="60" />
      </div>
      <h2>提交成功</h2>
      <p class="success-msg">感谢您的反馈，我们将尽快处理</p>
      <van-button round block color="#26A65B" style="width: 200px;" @click="$router.push('/home')">
        返回首页
      </van-button>
    </div>
  </div>
</template>

<script setup>
import StatusBar from '../components/StatusBar.vue'
</script>

<style scoped>
.success-page {
  background: #fff;
}
.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 120px;
  gap: 16px;
}
.success-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #E8F5E9;
  display: flex;
  align-items: center;
  justify-content: center;
}
h2 {
  font-size: 20px;
  color: #333;
}
.success-msg {
  font-size: 14px;
  color: #999;
  margin-bottom: 30px;
}
</style>

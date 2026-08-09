<template>
  <div class="page encyclopedia-page">
    <StatusBar />
    <div class="encyclopedia-header">
      <div class="back-btn" @click="$router.back()">
        <van-icon name="arrow-left" size="20" color="#333" />
      </div>
      <h3>AI百科</h3>
    </div>
    <div class="encyclopedia-content">
      <div class="search-area">
        <van-search shape="round" background="#F0F8F0" placeholder="搜索宠物品种、疾病..." v-model="keyword" />
      </div>
      <p class="disclaimer">内容由AI生成，仅供参考</p>
      <div class="card-grid">
        <div class="card-item" v-for="(item, idx) in cardList" :key="idx" @click="goDetail(item)">
          <div class="card-img-wrap">
            <img :src="item.img" class="card-img" />
          </div>
          <div class="card-body">
            <p class="card-name">{{ item.name }}</p>
            <span class="card-link">点击查看</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import StatusBar from '../components/StatusBar.vue'

const router = useRouter()
const keyword = ref('')

const cardList = [
  { name: '豚鼠（荷兰猪）', img: '/images/ai百科/u696.svg', route: '/guinea-pig' },
  { name: '猪鼻蛇', img: '/images/ai百科/u705.svg', route: '' },
  { name: '守宫囊肿', img: '/images/ai百科/u711.svg', route: '' },
  { name: '玄凤鹦鹉', img: '/images/ai百科/u717.svg', route: '' },
  { name: '龙猫', img: '/images/ai百科/u723.svg', route: '' },
  { name: '蜥蜴的饲养环境', img: '/images/ai百科/u729.svg', route: '' },
  { name: '龙猫', img: '/images/ai百科/u723.svg', route: '' },
  { name: '蜥蜴的饲养环境', img: '/images/ai百科/u729.svg', route: '' }
]

const goDetail = (item) => {
  if (item.route) {
    router.push(item.route)
  }
}
</script>

<style scoped>
.encyclopedia-page {
  background: #F0F8F0;
}
.encyclopedia-header {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  gap: 10px;
  background: #F0F8F0;
}
.encyclopedia-header h3 {
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
.encyclopedia-content {
  padding: 0 15px;
}
.search-area {
  margin-bottom: 4px;
}
.search-area :deep(.van-search) {
  padding: 0;
}
.disclaimer {
  font-size: 11px;
  color: #bbb;
  text-align: center;
  margin-bottom: 12px;
}
.card-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  padding-bottom: 20px;
}
.card-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
}
.card-img-wrap {
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
}
.card-img {
  width: 80%;
  height: auto;
}
.card-body {
  padding: 8px 12px 14px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.card-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.card-link {
  font-size: 11px;
  color: #26A65B;
}
</style>

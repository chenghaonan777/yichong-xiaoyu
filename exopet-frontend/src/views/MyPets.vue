<template>
  <div class="page mypets-page">
    <StatusBar />
    <div class="mypets-content">
      <h3 class="page-title">我的爱宠</h3>

      <!-- Loading -->
      <div class="loading-box" v-if="loading">
        <van-loading>加载中...</van-loading>
      </div>

      <!-- Pet Selector Tabs -->
      <div class="pet-tabs" v-if="!loading">
        <div v-for="(pet, idx) in pets" :key="pet.id"
          :class="['pet-tab', { active: activePetIdx === idx }]"
          @click="switchPet(idx)">
          <img :src="pet.avatar || '/images/爱宠/u1316.svg'" class="pet-avatar" />
          <span class="pet-name">{{ pet.name }}</span>
          <span class="pet-breed">{{ pet.breedName }} | {{ calcAge(pet.birthday) }}岁</span>
        </div>
        <!-- 新增爱宠按钮 -->
        <div class="pet-tab pet-add-btn" @click="showPetForm = true">
          <div class="add-icon">+</div>
          <span class="pet-name">添加</span>
        </div>
      </div>

      <!-- Health Record -->
      <div class="record-card" v-if="activePet && !loading">
        <div class="record-title-row">
          <h4 class="record-title">电子健康档案 — {{ activePet.name }}</h4>
          <van-button size="small" round color="#26A65B" icon="plus" @click="openAddForm">新增</van-button>
        </div>

        <!-- Basic Info -->
        <div class="info-section">
          <h5 class="info-label">基本信息</h5>
          <div class="info-grid">
            <div class="info-row">
              <span class="info-key">品种：</span>
              <span class="info-val">{{ activePet.breedName }}</span>
            </div>
            <div class="info-row">
              <span class="info-key">性别：</span>
              <span class="info-val">{{ genderLabel(activePet.gender) }}</span>
            </div>
            <div class="info-row">
              <span class="info-key">年龄：</span>
              <span class="info-val">{{ calcAge(activePet.birthday) }}岁</span>
            </div>
            <div class="info-row" v-if="activePet.weight">
              <span class="info-key">体重：</span>
              <span class="info-val">{{ activePet.weight }}g</span>
            </div>
          </div>
        </div>

        <!-- Health Records CRUD -->
        <div class="info-section">
          <h5 class="info-label">健康记录</h5>
          <div class="crud-list" v-if="healthRecords.length > 0">
            <div class="crud-item" v-for="(record, idx) in healthRecords" :key="record.id">
              <div class="crud-left">
                <span :class="['crud-type-badge', record.recordType]">{{ typeLabel(record.recordType) }}</span>
                <div class="crud-info">
                  <span class="crud-title">{{ record.title }}</span>
                  <span class="crud-date">{{ record.recordDate }}</span>
                </div>
              </div>
              <div class="crud-actions">
                <van-icon name="edit" size="16" color="#999" @click="openEditForm(record)" />
                <van-icon name="delete" size="16" color="#FF4444" @click="deleteRecord(record.id)" />
              </div>
            </div>
          </div>
          <div class="empty-tip" v-else>
            <van-icon name="records-o" size="40" color="#ddd" />
            <p>暂无健康记录，点击右上角「新增」添加</p>
          </div>
        </div>

        <!-- Reminders -->
        <div class="info-section" v-if="reminders.length > 0">
          <h5 class="info-label">待办提醒</h5>
          <div class="reminder-list">
            <div class="reminder-item" v-for="item in reminders" :key="item.id">
              <div class="reminder-left">
                <span class="reminder-name">{{ item.title }}</span>
                <span class="reminder-date">{{ item.remindDate }}</span>
              </div>
              <span :class="['reminder-status', item.status === 0 ? 'warning' : item.status === 1 ? 'done' : 'idle']">
                {{ item.status === 0 ? '待处理' : item.status === 1 ? '已完成' : '已过期' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Record Popup -->
    <van-action-sheet v-model:show="showForm" :title="formMode === 'add' ? '新增健康记录' : '编辑健康记录'" closeable>
      <div class="form-content">
        <van-form @submit="submitForm">
          <van-field
            v-model="formData.recordType"
            is-link readonly name="recordType" label="记录类型"
            placeholder="请选择类型"
            :rules="[{ required: true, message: '请选择记录类型' }]"
            @click="showTypePicker = true"
          />
          <van-field
            v-model="formData.title" name="title" label="标题"
            placeholder="如：狂犬疫苗第一针"
            :rules="[{ required: true, message: '请输入标题' }]"
          />
          <van-field
            v-model="formData.recordDate" is-link readonly name="recordDate" label="日期"
            placeholder="请选择日期"
            :rules="[{ required: true, message: '请选择日期' }]"
            @click="showDatePicker = true"
          />
          <van-field v-model="formData.doctorName" name="doctorName" label="兽医" placeholder="操作兽医（选填）" />
          <van-field v-model="formData.notes" name="notes" label="备注" type="textarea" rows="2" placeholder="备注信息（选填）" />
          <div class="form-actions">
            <van-button round block color="#26A65B" native-type="submit" :loading="submitting">
              {{ formMode === 'add' ? '添加' : '保存' }}
            </van-button>
          </div>
        </van-form>
      </div>
    </van-action-sheet>

    <!-- Type Picker -->
    <van-action-sheet v-model:show="showTypePicker" :actions="typeOptions" @select="selectType" />

    <!-- Date Picker -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker @confirm="confirmDate" @cancel="showDatePicker = false"
        :min-date="minDate" :max-date="maxDate" />
    </van-popup>

    <!-- Add Pet Popup -->
    <van-action-sheet v-model:show="showPetForm" title="新增爱宠" closeable>
      <div class="form-content">
        <van-form @submit="submitPet">
          <van-field v-model="petForm.name" name="name" label="宠物昵称" placeholder="如：小壁"
            :rules="[{ required: true, message: '请输入宠物昵称' }]" />
          <van-field v-model="petForm.breedType" is-link readonly name="breedType" label="宠物大类"
            placeholder="请选择类型" :rules="[{ required: true, message: '请选择类型' }]"
            @click="showBreedPicker = true" />
          <van-field v-model="petForm.breedName" name="breedName" label="具体品种" placeholder="如：豹纹守宫"
            :rules="[{ required: true, message: '请输入品种' }]" />
          <van-field v-model="petForm.gender" is-link readonly name="gender" label="性别"
            placeholder="请选择" @click="showGenderPicker = true" />
          <van-field v-model="petForm.birthday" is-link readonly name="birthday" label="生日"
            placeholder="请选择" @click="showBirthdayPicker = true" />
          <van-field v-model="petForm.weight" name="weight" label="体重(g)" placeholder="如：68" type="digit" />
          <div class="form-actions">
            <van-button round block color="#26A65B" native-type="submit" :loading="petSubmitting">添加</van-button>
          </div>
        </van-form>
      </div>
    </van-action-sheet>

    <!-- Breed Type Picker -->
    <van-action-sheet v-model:show="showBreedPicker" :actions="breedOptions" @select="selectBreed" />

    <!-- Gender Picker -->
    <van-action-sheet v-model:show="showGenderPicker" :actions="genderOptions" @select="selectGender" />

    <!-- Birthday Picker -->
    <van-popup v-model:show="showBirthdayPicker" position="bottom">
      <van-date-picker @confirm="confirmBirthday" @cancel="showBirthdayPicker = false"
        :min-date="birthMinDate" :max-date="birthMaxDate" title="选择生日" />
    </van-popup>

    <AppTabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import request from '../utils/request.js'
import StatusBar from '../components/StatusBar.vue'
import AppTabBar from '../components/AppTabBar.vue'
import { useUserStore } from '../stores/user.js'

// ==================== 状态 ====================
const userStore = useUserStore()
const loading = ref(true)
const submitting = ref(false)
const pets = ref([])
const activePetIdx = ref(0)
const healthRecords = ref([])
const reminders = ref([])
const showForm = ref(false)
const showTypePicker = ref(false)
const showDatePicker = ref(false)
const formMode = ref('add')
const editingId = ref(null)

const minDate = new Date(2020, 0, 1)
const maxDate = new Date()

const typeOptions = [
  { name: '疫苗', value: 'vaccine' },
  { name: '驱虫', value: 'deworm' },
  { name: '体检', value: 'checkup' },
  { name: '用药', value: 'medication' },
  { name: '体重', value: 'weight' }
]

// ==================== 新增宠物 ====================
const showPetForm = ref(false)
const petSubmitting = ref(false)
const showBreedPicker = ref(false)
const showGenderPicker = ref(false)
const showBirthdayPicker = ref(false)
const birthMinDate = new Date(2000, 0, 1)
const birthMaxDate = new Date()

const breedOptions = [
  { name: '爬行类', value: '爬行类' },
  { name: '鸟类', value: '鸟类' },
  { name: '水族', value: '水族' },
  { name: '小型哺乳', value: '小型哺乳' }
]
const genderOptions = [
  { name: '雄性', value: 1 },
  { name: '雌性', value: 2 },
  { name: '未知', value: 0 }
]

const petForm = ref({
  name: '', breedType: '', breedName: '',
  gender: '', birthday: '', weight: ''
})

function selectBreed(item) {
  petForm.value.breedType = item.value
  showBreedPicker.value = false
}
function selectGender(item) {
  const map = { 1: '雄性', 2: '雌性', 0: '未知' }
  petForm.value.gender = map[item.value]
  showGenderPicker.value = false
}
function confirmBirthday({ selectedValues }) {
  petForm.value.birthday = selectedValues.join('-')
  showBirthdayPicker.value = false
}

async function submitPet() {
  const f = petForm.value
  if (!f.name || !f.breedType || !f.breedName) {
    showToast('请填写完整信息')
    return
  }
  const genderMap = { '雄性': 1, '雌性': 2, '未知': 0 }
  petSubmitting.value = true
  try {
    await request.post('/pet', {
      userId: userStore.userId,
      name: f.name,
      breedType: f.breedType,
      breedName: f.breedName,
      gender: genderMap[f.gender] || 0,
      birthday: f.birthday || null,
      weight: f.weight ? parseFloat(f.weight) : null
    })
    showToast('添加成功')
    showPetForm.value = false
    petForm.value = { name: '', breedType: '', breedName: '', gender: '', birthday: '', weight: '' }
    await fetchPets()
  } catch (e) {
    showToast('添加失败')
  } finally {
    petSubmitting.value = false
  }
}

const initFormData = () => ({
  recordType: '',
  title: '',
  recordDate: '',
  doctorName: '',
  notes: ''
})
const formData = ref(initFormData())

// ==================== 计算属性 ====================
const activePet = computed(() => pets.value[activePetIdx.value])

// ==================== API 方法 ====================
/** 加载宠物列表 */
async function fetchPets() {
  try {
    const data = await request.get(`/pet/list/by-user/${userStore.userId}`)
    pets.value = data || []
    if (pets.value.length > 0) {
      activePetIdx.value = 0
      await loadPetData(0)
    }
  } catch (e) {
    console.error('获取宠物列表失败', e)
  } finally {
    loading.value = false
  }
}

/** 加载选中宠物的健康记录 + 提醒 */
async function loadPetData(idx) {
  const pet = pets.value[idx]
  if (!pet) return
  await Promise.all([
    fetchHealthRecords(pet.id),
    fetchReminders(pet.id)
  ])
}

async function fetchHealthRecords(petId) {
  try {
    const data = await request.get(`/pet/health-record/list/by-pet/${petId}?page=1&size=50`)
    healthRecords.value = data?.records || []
  } catch (e) {
    healthRecords.value = []
  }
}

async function fetchReminders(petId) {
  try {
    const data = await request.get(`/pet/reminder/list/by-pet/${petId}?page=1&size=50`)
    reminders.value = data?.records || []
  } catch (e) {
    reminders.value = []
  }
}

// ==================== 操作 ====================
function switchPet(idx) {
  activePetIdx.value = idx
  formMode.value = 'add'
  showForm.value = false
  loadPetData(idx)
}

function openAddForm() {
  formMode.value = 'add'
  editingId.value = null
  formData.value = initFormData()
  showForm.value = true
}

function openEditForm(record) {
  formMode.value = 'edit'
  editingId.value = record.id
  formData.value = {
    recordType: record.recordType,
    title: record.title,
    recordDate: record.recordDate,
    doctorName: record.doctorName || '',
    notes: record.notes || ''
  }
  showForm.value = true
}

async function submitForm() {
  if (!formData.value.recordType || !formData.value.title || !formData.value.recordDate) {
    showToast('请填写完整信息')
    return
  }
  submitting.value = true
  try {
    const body = {
      petId: activePet.value.id,
      recordType: formData.value.recordType,
      title: formData.value.title,
      recordDate: formData.value.recordDate,
      doctorName: formData.value.doctorName,
      notes: formData.value.notes
    }
    if (formMode.value === 'add') {
      await request.post('/pet/health-record', body)
      showToast('添加成功')
    } else {
      await request.put(`/pet/health-record/${editingId.value}`, body)
      showToast('保存成功')
    }
    showForm.value = false
    await fetchHealthRecords(activePet.value.id)
  } catch (e) {
    showToast('操作失败')
  } finally {
    submitting.value = false
  }
}

async function deleteRecord(id) {
  try {
    await showConfirmDialog({ title: '删除确认', message: '确定要删除这条健康记录吗？' })
    await request.delete(`/pet/health-record/${id}`)
    showToast('已删除')
    await fetchHealthRecords(activePet.value.id)
  } catch (e) {
    // 用户取消删除，忽略
  }
}

// ==================== 工具 ====================
const typeLabel = (type) => {
  const map = { vaccine: '疫苗', deworm: '驱虫', checkup: '体检', medication: '用药', weight: '体重' }
  return map[type] || type
}

const genderLabel = (gender) => {
  const map = { 0: '未知', 1: '雄性', 2: '雌性' }
  return map[gender] || '未知'
}

const calcAge = (birthday) => {
  if (!birthday) return '?'
  const birth = new Date(birthday)
  const now = new Date()
  let age = now.getFullYear() - birth.getFullYear()
  const m = now.getMonth() - birth.getMonth()
  if (m < 0 || (m === 0 && now.getDate() < birth.getDate())) age--
  return age >= 0 ? age : 0
}

function selectType(item) {
  formData.value.recordType = item.value
  showTypePicker.value = false
}

function confirmDate({ selectedValues }) {
  formData.value.recordDate = selectedValues.join('-')
  showDatePicker.value = false
}

// ==================== 生命周期 ====================
onMounted(fetchPets)
</script>

<style scoped>
.mypets-page { background: #F0F8F0; min-height: 100vh; padding-bottom: 60px; }
.mypets-content { padding: 0; }
.page-title {
  font-size: 18px; font-weight: 600; color: #333;
  padding: 10px 15px; margin: 0;
}

.loading-box {
  display: flex; justify-content: center; padding: 50px 0;
}

/* Pet Tabs */
.pet-tabs {
  display: flex; gap: 8px; padding: 0 15px 16px; overflow-x: auto;
}
.pet-tab {
  background: #fff; border-radius: 12px; padding: 15px;
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  min-width: 120px; flex: 1; cursor: pointer; border: 2px solid transparent;
}
.pet-tab.active { border-color: #26A65B; }
.pet-avatar { width: 50px; height: 50px; border-radius: 50%; object-fit: cover; }
.pet-name { font-size: 15px; font-weight: 600; color: #333; }
.pet-breed { font-size: 11px; color: #999; }

/* Add Pet Button */
.pet-add-btn {
  justify-content: center; background: #e8f5e9; border: 2px dashed #26A65B;
}
.add-icon {
  width: 36px; height: 36px; border-radius: 50%; background: #26A65B;
  color: #fff; font-size: 24px; display: flex;
  align-items: center; justify-content: center;
}

/* Record Card */
.record-card {
  background: #fff; margin: 0 15px 15px; border-radius: 12px; padding: 15px;
}
.record-title-row {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;
}
.record-title { font-size: 16px; font-weight: 600; color: #333; margin: 0; }
.info-section { margin-bottom: 16px; }
.info-label {
  font-size: 14px; font-weight: 500; color: #333;
  margin-bottom: 8px; padding-bottom: 6px; border-bottom: 1px solid #f0f0f0;
}
.info-grid { display: grid; gap: 6px; }
.info-row { font-size: 13px; color: #666; }
.info-key { color: #999; }

/* CRUD List */
.crud-list { display: flex; flex-direction: column; gap: 8px; }
.crud-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px; background: #F9F9F9; border-radius: 8px;
}
.crud-left { display: flex; align-items: center; gap: 10px; flex: 1; }
.crud-type-badge {
  font-size: 11px; padding: 2px 8px; border-radius: 4px; flex-shrink: 0;
}
.crud-type-badge.vaccine { background: #E3F2FD; color: #1976D2; }
.crud-type-badge.deworm { background: #FFF3E0; color: #F57C00; }
.crud-type-badge.checkup { background: #E8F5E9; color: #388E3C; }
.crud-type-badge.medication { background: #FCE4EC; color: #C62828; }
.crud-type-badge.weight { background: #F3E5F5; color: #7B1FA2; }
.crud-info { display: flex; flex-direction: column; gap: 2px; }
.crud-title { font-size: 13px; color: #333; }
.crud-date { font-size: 11px; color: #999; }
.crud-actions { display: flex; gap: 12px; flex-shrink: 0; }

/* Empty */
.empty-tip {
  display: flex; flex-direction: column; align-items: center;
  gap: 8px; padding: 20px 0; color: #999; font-size: 13px;
}

/* Reminder */
.reminder-list { display: flex; flex-direction: column; gap: 8px; }
.reminder-item {
  display: flex; justify-content: space-between; align-items: center;
  padding-bottom: 8px; border-bottom: 1px solid #f0f0f0;
}
.reminder-left { display: flex; flex-direction: column; gap: 2px; }
.reminder-name { font-size: 13px; color: #333; }
.reminder-date { font-size: 11px; color: #999; }
.reminder-status { font-size: 11px; padding: 2px 8px; border-radius: 4px; }
.reminder-status.warning { background: #FFF3E0; color: #FF9800; }
.reminder-status.done { background: #E8F5E9; color: #4CAF50; }
.reminder-status.idle { background: #F5F5F5; color: #999; }

/* Form */
.form-content { padding: 20px 16px; }
.form-actions { padding: 20px 0; }
</style>

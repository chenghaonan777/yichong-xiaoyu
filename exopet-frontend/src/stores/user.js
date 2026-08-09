import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '../utils/request.js'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const userInfo = ref({
    avatar: '',
    nickname: '',
    phone: '',
    gender: 0
  })

  const isLoggedIn = computed(() => !!token.value)

  async function fetchUserInfo() {
    if (!userId.value) return
    try {
      const data = await request.get(`/user/${userId.value}`)
      if (data) {
        userInfo.value = {
          avatar: data.avatar || '',
          nickname: data.nickname || '^-^',
          phone: data.phone || '',
          gender: data.gender || 0
        }
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      }
    } catch {
      // 从缓存读取
      const cached = localStorage.getItem('userInfo')
      if (cached) {
        try { userInfo.value = JSON.parse(cached) } catch {}
      }
    }
  }

  function setLogin(tokenVal, id) {
    token.value = tokenVal
    userId.value = id
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('userId', String(id))
  }

  function logout() {
    token.value = ''
    userId.value = 0
    userInfo.value = { avatar: '', nickname: '', phone: '', gender: 0 }
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('userInfo')
  }

  return { token, userId, userInfo, isLoggedIn, fetchUserInfo, setLogin, logout }
})
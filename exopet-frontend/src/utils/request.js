import axios from 'axios'

const request = axios.create({
  // 走 Vite 代理，根据路径自动转发到对应后端服务
  baseURL: '/api-proxy',
  timeout: 10000
})

// 请求拦截器 — 自动带 Token + userId
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  // AI 模块等接口依赖 userId header
  const userId = localStorage.getItem('userId')
  if (userId) {
    config.headers.userId = userId
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  res => {
    const body = res.data
    if (body.code === 200) {
      return body.data
    }
    // 业务异常由页面自行处理（showToast 已移除，避免重复提示）
    return Promise.reject(new Error(body.msg || '请求失败'))
  },
  err => {
    // 401 未登录 → 跳转登录页
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.hash = '/login1'
    }
    // 其他错误交由调用方 catch 自行处理，不再全局弹 Toast
    return Promise.reject(err)
  }
)

export default request

/**
 * AI 对话通用 composable
 * 支持三个接口：diagnose / mood-analysis / breed-recognize
 * 自动管理 history、multipart/form-data 传图
 *
 * @param {string} apiEndpoint - API 路径，如 '/api/ai/diagnose'
 * @param {string} userField - 用户输入字段名，diagnose 用 'symptomDesc', breed-recognize/mood 用 'description'
 */
import { ref } from 'vue'
import { showToast } from 'vant'
import request from '../utils/request.js'

export function useAiChat(apiEndpoint, userField = 'symptomDesc') {
  const messages = ref([])       // { role:'user'|'ai', content, raw?, image? }
  const history = ref([])        // [{ user, ai }] 传给后端的对话历史
  const isDone = ref(false)
  const loading = ref(false)

  /** 解析后端的 aiRawResponse JSON 字符串 */
  function parseRaw(data) {
    if (!data || !data.aiRawResponse) return null
    try { return JSON.parse(data.aiRawResponse) } catch { return null }
  }

  /** 首轮请求 */
  async function sendFirst(params) {
    const { image, ...rest } = params
    loading.value = true
    try {
      const fd = new FormData()
      if (image instanceof File) fd.append('image', image)
      Object.entries(rest).forEach(([k, v]) => {
        if (v !== undefined && v !== null && v !== '') fd.append(k, v)
      })

      const res = await request.post(apiEndpoint, fd)
      const raw = parseRaw(res)
      if (raw) {
        messages.value.push({ role: 'ai', content: raw.reply, raw })
        const userMsg = rest.description || rest.symptomDesc || ''
        if (userMsg) {
          history.value.push({ user: userMsg, ai: raw.reply })
        }
        isDone.value = !!raw.isDone
      }
      return res
    } catch (e) {
      showToast('请求失败，请稍后重试')
      return null
    } finally {
      loading.value = false
    }
  }

  /** 后续轮次对话 */
  async function sendNext(userInput, extraParams = {}) {
    const { image } = extraParams
    messages.value.push({ role: 'user', content: userInput })
    loading.value = true
    try {
      const fd = new FormData()
      if (image instanceof File) fd.append('image', image)
      // 其他额外参数（如 breedType, breedName, symptoms）
      Object.entries(extraParams).forEach(([k, v]) => {
        if (k !== 'image' && v !== undefined && v !== null && v !== '') fd.append(k, v)
      })
      fd.append('history', JSON.stringify(history.value))
      // 用户本轮输入（字段名根据 API 不同而不同）
      fd.append(userField, userInput)

      const res = await request.post(apiEndpoint, fd)
      const raw = parseRaw(res)
      if (raw) {
        messages.value.push({ role: 'ai', content: raw.reply, raw })
        history.value.push({ user: userInput, ai: raw.reply })
        isDone.value = !!raw.isDone
      }
      return res
    } catch (e) {
      showToast('请求失败，请稍后重试')
      return null
    } finally {
      loading.value = false
    }
  }

  function reset() {
    messages.value = []
    history.value = []
    isDone.value = false
    loading.value = false
  }

  return { messages, history, isDone, loading, sendFirst, sendNext, reset }
}

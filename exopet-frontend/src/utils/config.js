/**
 * 环境配置
 * 修改此处可切换后端服务地址
 */
export const CONFIG = {
  // REST API 走 Vite 代理，无需修改
  // WebSocket 直连后端
  WS_CONSULT: 'ws://localhost:9204/ws/consult',
  WS_NOTIFICATION: 'ws://localhost:9209/ws/notification',
}

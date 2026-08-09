/**
 * 高德地图配置模块
 *
 * 使用前需在 https://lbs.amap.com 注册 → 创建应用 → 添加 Key（Web端 JS API）
 * 白名单开发阶段填 127.0.0.1
 */
import AMapLoader from '@amap/amap-jsapi-loader'

// ⚠️ 请替换成你在高德开放平台申请的 Key 和安全密钥
export const AMAP_KEY = '458349d7f05b81b615166b8ae6637c85'
export const AMAP_SECURITY_KEY = 'ed20375b756839a91a14534fab30c833'

/**
 * 异步加载高德地图 SDK（单例）
 * @returns {Promise<typeof AMap>}
 */
export async function loadAMap() {
  window._AMapSecurityConfig = {
    securityJsCode: AMAP_SECURITY_KEY,
  }
  return await AMapLoader.load({
    key: AMAP_KEY,
    version: '2.0',
    plugins: ['AMap.Geolocation'],
  })
}

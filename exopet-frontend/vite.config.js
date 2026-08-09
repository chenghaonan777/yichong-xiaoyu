import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from '@vant/auto-import-resolver'

export default defineConfig({
  plugins: [
    vue(),
    Components({
      resolvers: [VantResolver()]
    })
  ],
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      // 全走 Gateway → 8080
      '/api-proxy': {
        target: 'http://localhost:8080',
        rewrite: path => path.replace('/api-proxy', ''),
        changeOrigin: true
      }
    }
  }
})

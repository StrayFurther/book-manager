import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(() => {
  // debug: confirm this file is loaded when vite starts
  console.log('Vite config loaded — proxy /api -> http://localhost:8443');

  return {
    plugins: [react()],
    server: {
      proxy: {
        '/api': {
          target: 'http://localhost:8443',
          changeOrigin: true,
          secure: false,
        }
      }
    }
  }
})

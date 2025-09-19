import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
    plugins: [react()],
    build: {
        outDir: '../public',
    },
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: process.env.VITE_API_BASE || 'http://localhost:9000',
                changeOrigin: true,
            },
        }
    },
    define: {
        __APP_VERSION__: JSON.stringify('0.0.1')
    }
})

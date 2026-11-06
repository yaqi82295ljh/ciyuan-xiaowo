import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { fileURLToPath, URL } from 'node:url';

// 后端 Spring Boot 跑在 9090，context-path 是 /api。
// 开发时走 Vite 代理，让前端和接口同源（localhost:5173），
// 这样 JSESSIONID 的 Cookie（Path=/api）能正常带上，不会再有跨站丢登录态的问题。
export default defineConfig({
	plugins: [vue()],
	resolve: {
		alias: {
			'@': fileURLToPath(new URL('./src', import.meta.url))
		}
	},
	server: {
		host: 'localhost',
		port: 5173,
		proxy: {
			'/api': {
				target: 'http://localhost:9090',
				changeOrigin: true
			},
			// 上传后的图片由后端以 /uploads/** 提供静态访问
			'/uploads': {
				target: 'http://localhost:9090',
				changeOrigin: true
			}
		}
	}
});

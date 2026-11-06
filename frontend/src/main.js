import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';

// 全局样式：设计令牌 + 布局壳 + 侧栏 / 顶栏 / 筛选区
import '@/assets/styles/index.css';

createApp(App).use(createPinia()).use(router).mount('#app');

import { defineStore } from 'pinia';
import { api } from '@/utils/api';

/** 热门标签：侧栏和首页筛选共用一份，只拉一次 */
export const useTagStore = defineStore('tags', {
	state: () => ({
		list: [],
		loaded: false
	}),
	actions: {
		async fetchTags() {
			if (this.loaded) return;
			try {
				const data = await api.get('/tag');
				this.list = Array.isArray(data) ? data : [];
			} catch (err) {
				console.warn('[tags] 标签没拉下来：', err.message);
				this.list = [];
			} finally {
				this.loaded = true;
			}
		}
	}
});

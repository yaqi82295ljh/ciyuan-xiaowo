import { defineStore } from 'pinia';
import { api } from '@/utils/api';

/** 全站登录态：用户资料 + 是否已拉取过 */
export const useAuthStore = defineStore('auth', {
	state: () => ({
		user: null,
		loaded: false
	}),
	getters: {
		isLoggedIn: (state) => !!state.user,
		/** 展示用昵称 */
		displayName: (state) => {
			if (!state.user) return '';
			return state.user.nickname || state.user.username || '';
		}
	},
	actions: {
		/** 拉当前用户；force 强制刷新（登录 / 退出 / bfcache 恢复时用） */
		async fetchMe(force = false) {
			if (this.loaded && !force) return this.user;
			try {
				this.user = await api.get('/me');
			} catch {
				this.user = null;
			} finally {
				this.loaded = true;
			}
			return this.user;
		},
		/** 退出登录并回到首页 */
		async logout() {
			try {
				await api.del('/me');
			} catch {
				// 后端没响应也当作退出成功，本地状态必须清掉
			}
			this.user = null;
		}
	}
});

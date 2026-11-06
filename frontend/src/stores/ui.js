import { defineStore } from 'pinia';

/** 跨组件的 UI 状态：上传弹窗 + 新发布作品通知 + 移动端筛选面板 */
export const useUiStore = defineStore('ui', {
	state: () => ({
		uploadOpen: false,
		/** 移动端底部滑出的筛选面板（桌面端不用，走页面内的筛选卡） */
		filterSheet: false,
		/** 最近一次上传成功的作品（完整 ImageVO + 时间戳），首页 / 个人中心监听后插入列表 */
		lastUploaded: null
	}),
	actions: {
		openUpload() {
			this.uploadOpen = true;
		},
		closeUpload() {
			this.uploadOpen = false;
		},
		openFilterSheet() {
			this.filterSheet = true;
		},
		closeFilterSheet() {
			this.filterSheet = false;
		},
		/** 上传成功后调用，通知各视图把新卡片插到列表头部 */
		notifyUploaded(work) {
			this.lastUploaded = { ...work, _at: Date.now() };
		}
	}
});

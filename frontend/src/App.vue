<template>
	<!-- 独立布局：登录页 / 图片详情 / 管理后台不带侧栏和顶栏 -->
	<router-view v-if="isPlain" />

	<div v-else id="big-conain" class="app-shell" :class="{ 'is-drawer-open': drawerOpen }">
		<!-- 移动端抽屉遮罩，点击收起 -->
		<div class="sb-mask" aria-hidden="true" @click="drawerOpen = false"></div>

		<AppSidebar @close="drawerOpen = false" />

		<main class="con">
			<AppTopbar @open-drawer="drawerOpen = true" />
			<router-view />
		</main>

		<MobileTabBar />

		<UploadDialog />
		<BackToTop />
	</div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';
import AppSidebar from '@/components/AppSidebar.vue';
import AppTopbar from '@/components/AppTopbar.vue';
import MobileTabBar from '@/components/MobileTabBar.vue';
import UploadDialog from '@/components/UploadDialog.vue';
import BackToTop from '@/components/BackToTop.vue';

const route = useRoute();
const auth = useAuthStore();
const ui = useUiStore();

const isPlain = computed(() => !!route.meta.plain);
const drawerOpen = ref(false);

// 切路由时自动收起抽屉；打开抽屉时锁住背景滚动
watch(() => route.fullPath, () => {
	drawerOpen.value = false;
	ui.closeFilterSheet();
});
watch([drawerOpen, () => ui.filterSheet], ([open, sheet]) => {
	document.body.style.overflow = (open || sheet) ? 'hidden' : '';
});

// 页面从 bfcache 恢复时登录态可能已变化（比如在别的标签页退出了），强制重拉
function onPageShow(event) {
	if (event.persisted) auth.fetchMe(true);
}

onMounted(() => {
	auth.fetchMe();
	window.addEventListener('pageshow', onPageShow);
});

onUnmounted(() => {
	document.body.style.overflow = '';
	window.removeEventListener('pageshow', onPageShow);
});
</script>

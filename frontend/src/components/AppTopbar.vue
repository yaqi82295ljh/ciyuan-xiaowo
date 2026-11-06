<template>
	<header class="topbar">
		<button class="tb-burger" type="button" aria-label="展开菜单" title="展开菜单" @click="emit('open-drawer')">
			<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
				stroke-linecap="round" stroke-linejoin="round">
				<path d="M4 7h16M4 12h16M4 17h16"/>
			</svg>
		</button>

		<!-- 手机上不放大搜索框，用页面标题占据左侧 -->
		<div class="tb-head">
			<h1 class="tb-title">{{ pageTitle }}</h1>
		</div>

		<form class="tb-search" :class="{ 'is-open': searchOpen }" role="search" @submit.prevent="onSearch">
			<button class="tb-cancel" type="button" aria-label="收起搜索" @click="closeSearch">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
					stroke-linecap="round" stroke-linejoin="round">
					<path d="m15 5-7 7 7 7"/>
				</svg>
			</button>
			<svg class="tb-search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
				stroke-linecap="round" stroke-linejoin="round">
				<circle cx="11" cy="11" r="6.2"/>
				<path d="m15.6 15.6 4 4"/>
			</svg>
			<input ref="searchRef" v-model="keyword" class="tb-input" type="search" placeholder="搜索图片、画师或标签…"/>
			<button class="tb-go" type="submit">搜索</button>
		</form>

		<div class="tb-actions">
			<button v-if="isHome" class="tb-icon tb-filter" type="button" aria-label="筛选" title="筛选"
				@click="ui.openFilterSheet()">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
					stroke-linecap="round" stroke-linejoin="round">
					<path d="M4 6.5h16M7 12h10M10 17.5h4"/>
				</svg>
			</button>

			<button class="tb-icon tb-searchbtn" type="button" aria-label="搜索" title="搜索" @click="openSearch">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
					stroke-linecap="round" stroke-linejoin="round">
					<circle cx="11" cy="11" r="6.2"/>
					<path d="m15.6 15.6 4 4"/>
				</svg>
			</button>

			<template v-if="!auth.isLoggedIn">
				<router-link class="tb-btn tb-btn--ghost" to="/login">登录</router-link>
				<router-link class="tb-btn tb-btn--primary" to="/login?mode=register">注册</router-link>
			</template>

			<template v-else>
				<button class="tb-out" type="button" @click="onLogout">退出登录</button>
				<router-link class="tb-avatar" to="/my" :title="auth.displayName">
					<img v-if="auth.user.avatar" :src="auth.user.avatar" alt="我的头像"/>
					<template v-else>{{ avatarText }}</template>
				</router-link>
			</template>
		</div>
	</header>
</template>

<script setup>
import { computed, nextTick, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';

const emit = defineEmits(['open-drawer']);

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const ui = useUiStore();

const keyword = ref('');
const searchOpen = ref(false);
const searchRef = ref(null);

const avatarText = computed(() => (auth.displayName || '?').slice(0, 1));
const pageTitle = computed(() => route.meta.title || '次元小窝');
const isHome = computed(() => route.path === '/');

function openSearch() {
	searchOpen.value = true;
	nextTick(() => searchRef.value?.focus());
}

function closeSearch() {
	searchOpen.value = false;
	keyword.value = '';
}

function onSearch() {
	const kw = keyword.value.trim();
	searchOpen.value = false;
	router.push({ path: '/', query: kw ? { keyword: kw } : {} });
}

async function onLogout() {
	await auth.logout();
	router.push('/');
}
</script>

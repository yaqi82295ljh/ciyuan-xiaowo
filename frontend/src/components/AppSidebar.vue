<template>
	<aside class="left-sidebar">
		<div class="sb-head">
			<router-link class="sb-brand" to="/" title="回到首页">
				<img class="sb-logo-img" src="/resources/logo.png" alt="次元小窝"/>
			</router-link>

			<!-- 关闭按钮，只在移动端显示 -->
			<button class="sb-close" type="button" aria-label="收起菜单" title="收起菜单" @click="emit('close')">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
					stroke-linecap="round" stroke-linejoin="round">
					<path d="m6.6 6.6 10.8 10.8M17.4 6.6 6.6 17.4"/>
				</svg>
			</button>
		</div>

		<p class="sb-sub">一个装满可爱美图的小窝</p>

		<!-- 账号区：只在移动端抽屉里出现（桌面端走顶栏按钮） -->
		<div v-if="auth.isLoggedIn" class="sb-auth">
			<router-link class="sb-user" to="/my">
				<span class="sb-user-avatar">
					<img v-if="auth.user.avatar" :src="auth.user.avatar" alt="我的头像"/>
					<template v-else>{{ avatarText }}</template>
				</span>
				<span class="sb-user-text">
					<span class="sb-user-name">{{ auth.displayName }}</span>
					<span class="sb-user-role">{{ roleText }}</span>
				</span>
			</router-link>

			<button class="sb-user-out" type="button" @click="onLogout">退出</button>
		</div>

		<div v-else class="sb-auth sb-auth--guest">
			<router-link class="sb-guest-btn" to="/login">登录</router-link>
			<router-link class="sb-guest-btn sb-guest-btn--primary" to="/login?mode=register">注册</router-link>
		</div>

		<button class="sb-upload" type="button" @click="onUpload">
			<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
				stroke-linecap="round" stroke-linejoin="round">
				<path d="M12 16V4M12 4 8 8M12 4l4 4"/>
				<path d="M4 16v2.5A1.5 1.5 0 0 0 5.5 20h13a1.5 1.5 0 0 0 1.5-1.5V16"/>
			</svg>
			上传作品
		</button>

		<nav class="sb-nav">
			<ul class="sb-group">
				<li v-for="item in mainNav" :key="item.to">
					<router-link class="sb-item" :to="item.to" :class="{ 'is-active': isActive(item) }">
						<span v-html="item.icon"></span>
						{{ item.text }}
					</router-link>
				</li>
			</ul>

			<p class="sb-group-title">画廊分区</p>
			<ul class="sb-group">
				<li v-for="item in galleryNav" :key="item.to">
					<router-link class="sb-item" :to="item.to" :class="{ 'is-active': isActive(item) }">
						<span v-html="item.icon"></span>
						{{ item.text }}
					</router-link>
				</li>
			</ul>

			<p class="sb-group-title">我的小窝</p>
			<ul class="sb-group">
				<li v-for="item in myNav" :key="item.to" :class="{ 'sb-only-desktop': item.mobileHidden }">
					<router-link class="sb-item" :to="item.to" :class="{ 'is-active': isActive(item) }">
						<span v-html="item.icon"></span>
						{{ item.text }}
					</router-link>
				</li>
			</ul>

			<p class="sb-group-title">热门标签</p>
			<div class="sb-tags">
				<span v-if="!tags.list.length" class="sb-tag">暂无标签</span>
				<router-link
					v-for="t in tags.list.slice(0, 5)"
					:key="t.name"
					class="sb-tag"
					:to="{ path: '/', query: { tags: t.name } }"
				>#{{ t.name }}</router-link>
			</div>
		</nav>
	</aside>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useTagStore } from '@/stores/tags';
import { useUiStore } from '@/stores/ui';

const emit = defineEmits(['close']);

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const tags = useTagStore();
const ui = useUiStore();

const avatarText = computed(() => (auth.displayName || '?').slice(0, 1));
const roleText = computed(() => (auth.user.role === 2 ? '管理员' : '普通用户'));

const svg = (paths) =>
	'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" ' +
	'stroke-linecap="round" stroke-linejoin="round" style="width:18px;height:18px;flex-shrink:0">' +
	paths + '</svg>';

const mainNav = [
	{
		to: '/', text: '发现首页',
		icon: svg('<path d="M4 10.5 12 4l8 6.5V19a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 4 19z"/><path d="M9.6 20.5v-5h4.8v5"/>')
	},
	{
		to: '/top', text: '热门排行',
		icon: svg('<path d="M4 19.5h16"/><path d="M7.5 19.5V11M12 19.5V5.5M16.5 19.5v-5.5"/>')
	}
];

const galleryNav = [
	{
		to: '/wall', text: '风景壁纸',
		icon: svg('<rect x="3.5" y="5" width="17" height="14" rx="2.5"/><circle cx="9" cy="10" r="1.6"/><path d="m5 17.5 4.5-4.2 3.4 3 3-2.6 3.6 3.3"/>')
	},
	{
		to: '/char', text: '人物立绘',
		icon: svg('<circle cx="12" cy="8.2" r="3.5"/><path d="M5.5 20a6.5 6.5 0 0 1 13 0"/>')
	}
];

const myNav = [
	{
		to: '/notice', text: '公告板',
		icon: svg('<path d="M4 10v3.6a1.2 1.2 0 0 0 1.2 1.2H7l4 3.6V5.6L7 9.2H5.2A1.2 1.2 0 0 0 4 10.4z"/><path d="M15 8.6a4.2 4.2 0 0 1 0 6.8"/>')
	},
	{
		to: '/mail', text: '私信箱',
		icon: svg('<rect x="3.5" y="5.5" width="17" height="13" rx="2.5"/><path d="m4.5 7.5 7.5 5.5 7.5-5.5"/>')
	},
	{
		to: '/my#fav', text: '我的收藏', nav: 'fav',
		icon: svg('<path d="M12 20.3 4.9 13.2a4.8 4.8 0 1 1 6.8-6.8l.3.3.3-.3a4.8 4.8 0 1 1 6.8 6.8z"/>')
	},
	{
		to: '/my', text: '个人中心', nav: 'my', mobileHidden: true,
		icon: svg('<rect x="3.5" y="5" width="17" height="14" rx="2.5"/><circle cx="9" cy="11" r="2.2"/><path d="M6 16.2a3.4 3.4 0 0 1 6 0"/><path d="M14.5 10h4M14.5 13.5h4"/>')
	}
];

// 高亮规则：/my 下按 hash 区分「个人中心」和「我的收藏」，其余按路径精确匹配
function isActive(item) {
	if (item.nav === 'fav') return route.path === '/my' && route.hash === '#fav';
	if (item.nav === 'my') return route.path === '/my' && route.hash !== '#fav';
	if (item.to === '/') return route.path === '/';
	return route.path === item.to;
}

function onUpload() {
	emit('close');
	ui.openUpload();
}

async function onLogout() {
	await auth.logout();
	emit('close');
	router.push('/');
}

onMounted(() => { tags.fetchTags(); });
</script>

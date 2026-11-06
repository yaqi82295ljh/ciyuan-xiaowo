<template>
	<nav class="mtab" aria-label="底部导航">
		<router-link
			v-for="t in tabs" :key="t.path"
			class="mtab__item" :class="{ 'is-on': isActive(t.path) }"
			:to="t.path"
		>
			<span class="mtab__icon" v-html="t.icon"></span>
			<span class="mtab__text">{{ t.text }}</span>
		</router-link>
	</nav>
</template>

<script setup>
import { useRoute } from 'vue-router';

const route = useRoute();

const tabs = [
	{
		path: '/', text: '发现',
		icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3.5 10.6 12 3.8l8.5 6.8V20a1 1 0 0 1-1 1h-4.6v-6.2H9.1V21H4.5a1 1 0 0 1-1-1z"/></svg>'
	},
	{
		path: '/top', text: '排行',
		icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/></svg>'
	},
	{
		path: '/wall', text: '分区',
		icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3.5" y="3.5" width="7" height="7" rx="1.6"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.6"/><rect x="3.5" y="13.5" width="7" height="7" rx="1.6"/><rect x="13.5" y="13.5" width="7" height="7" rx="1.6"/></svg>'
	},
	{
		path: '/my', text: '我的',
		icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8.2" r="3.6"/><path d="M5.2 20a6.8 6.8 0 0 1 13.6 0"/></svg>'
	}
];

function isActive(path) {
	if (path === '/') return route.path === '/';
	if (path === '/wall') return route.path === '/wall' || route.path === '/char';
	return route.path.startsWith(path);
}
</script>

<style scoped>
.mtab{ display: none; }

@media (max-width: 760px){
	.mtab{
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 50;
		display: flex;
		height: calc(56px + env(safe-area-inset-bottom, 0px));
		padding-bottom: env(safe-area-inset-bottom, 0px);
		background-color: rgba(255, 255, 255, 0.94);
		backdrop-filter: saturate(180%) blur(12px);
		border-top: 1px solid var(--line);
	}

	.mtab__item{
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 3px;
		text-decoration: none;
		color: var(--ink-400);
		-webkit-tap-highlight-color: transparent;
	}

	.mtab__item.is-on{ color: var(--blue-700); }

	.mtab__icon{ display: block; width: 23px; height: 23px; }
	.mtab__icon :deep(svg){ width: 100%; height: 100%; }

	.mtab__text{
		font-size: 10.5px;
		letter-spacing: 0.04em;
		line-height: 1;
	}

	.mtab__item.is-on .mtab__text{ font-weight: 600; }
}
</style>

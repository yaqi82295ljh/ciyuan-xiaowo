<template>
	<button
		type="button"
		class="back-to-top"
		:class="{ show: visible }"
		aria-label="回到顶部"
		@click="toTop"
	>↑</button>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue';

const visible = ref(false);
let ticking = false;

const OFFSET_MIN = 60;
const OFFSET_MAX = 300;

function onScroll() {
	if (ticking) return;
	ticking = true;
	requestAnimationFrame(() => {
		const maxScroll = document.documentElement.scrollHeight - window.innerHeight;
		const offset = maxScroll <= 0
			? Infinity
			: Math.min(OFFSET_MAX, Math.max(OFFSET_MIN, maxScroll * 0.4));
		visible.value = window.scrollY > offset;
		ticking = false;
	});
}

function toTop() {
	window.scrollTo({ top: 0, behavior: 'smooth' });
}

onMounted(() => {
	window.addEventListener('scroll', onScroll, { passive: true });
	onScroll();
});

onUnmounted(() => {
	window.removeEventListener('scroll', onScroll);
});
</script>

<style scoped>
.back-to-top{
	position: fixed;
	right: 30px;
	bottom: 60px;
	z-index: 30;
	width: 48px;
	height: 48px;
	padding: 0;
	border: 0;
	border-radius: var(--r-full);
	display: grid;
	place-items: center;
	background-color: var(--blue-400);
	color: var(--ink-900);
	font-family: inherit;
	font-size: 20px;
	line-height: 1;
	cursor: pointer;
	box-shadow: var(--sh-2);

	opacity: 0;
	visibility: hidden;
	transform: translateY(12px) scale(0.8);
	transition:
		opacity var(--dur) var(--ease),
		transform var(--dur) var(--ease-q),
		background-color var(--dur-fast) var(--ease),
		box-shadow var(--dur-fast) var(--ease),
		visibility 0s linear var(--dur);
}

.back-to-top.show{
	opacity: 1;
	visibility: visible;
	transform: translateY(0) scale(1);
	transition:
		opacity var(--dur) var(--ease),
		transform var(--dur) var(--ease-q),
		background-color var(--dur-fast) var(--ease),
		box-shadow var(--dur-fast) var(--ease),
		visibility 0s;
}

.back-to-top:hover{
	background-color: var(--blue-500);
	box-shadow: var(--sh-3);
}

.back-to-top:active{
	transform: translateY(0) scale(0.88);
	transition: transform var(--dur-press) var(--ease);
}

/* 移动端：缩小并贴近右下，避开系统手势条，别挡住图片 */
@media (max-width: 760px){
	.back-to-top{
		right: 14px;
		bottom: calc(18px + env(safe-area-inset-bottom, 0px));
		width: 38px;
		height: 38px;
		font-size: 16px;
	}
}
</style>

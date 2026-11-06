<template>
	<header class="pg-head">
		<h1 class="pg-title">热门排行</h1>
	</header>

	<section class="rank" aria-label="热门排行榜单">
		<p v-if="!list.length" class="rank-loading">榜单还空着，等第一张作品上榜~</p>

		<article v-for="(item, i) in list" :key="item.id" class="rank-card">
			<span class="rank-no" :class="'rank-no--' + (i + 1)">{{ i + 1 }}</span>
			<router-link class="rank-thumb" :to="'/image/' + item.id">
				<img :src="item.img" :alt="item.title" referrerpolicy="no-referrer"/>
			</router-link>
			<div class="rank-info">
				<div class="rank-head">
					<h3 class="rank-title" :title="item.title">{{ item.title }}</h3>
					<span class="rank-heat" title="综合热度">{{ item.heat.toFixed(1) }}</span>
				</div>
				<div class="rank-stats">
					<span class="rank-stat" title="点赞">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="M12 20.3 4.9 13.2a4.8 4.8 0 1 1 6.8-6.8l.3.3.3-.3a4.8 4.8 0 1 1 6.8 6.8z"/>
						</svg>
						<b>{{ fmtNum(item.likes) }}</b>
					</span>
					<span class="rank-stat" title="收藏">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="m12 4.3 2.5 5 5.5.8-4 3.9.9 5.5-4.9-2.6-4.9 2.6.9-5.5-4-3.9 5.5-.8z"/>
						</svg>
						<b>{{ fmtNum(item.favorites) }}</b>
					</span>
					<span class="rank-stat" title="浏览">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="M2.6 12S6.2 6 12 6s9.4 6 9.4 6-3.6 6-9.4 6-9.4-6-9.4-6z"/>
							<circle cx="12" cy="12" r="2.7"/>
						</svg>
						<b>{{ fmtNum(item.views) }}</b>
					</span>
				</div>
			</div>
		</article>
	</section>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { api } from '@/utils/api';
import { fmtNum, toWork } from '@/utils/format';

const TOP_N = 50;

const list = ref([]);

// 热度公式：点赞 : 收藏 : 浏览 = 3 : 5 : 2，各自除以榜内最大值归一化
function buildList(works) {
	let maxLikes = 0;
	let maxFavs = 0;
	let maxViews = 0;
	works.forEach((w) => {
		maxLikes = Math.max(maxLikes, w.likes);
		maxFavs = Math.max(maxFavs, w.favorites);
		maxViews = Math.max(maxViews, w.views);
	});

	// 全 0 的数据（新站）直接按最新排，不除 0
	if (!maxLikes && !maxFavs && !maxViews) return works.slice(0, TOP_N);

	return works
		.map((w) => ({
			...w,
			heat: 3 * (w.likes / (maxLikes || 1)) +
				5 * (w.favorites / (maxFavs || 1)) +
				2 * (w.views / (maxViews || 1))
		}))
		.sort((a, b) => b.heat - a.heat)
		.slice(0, TOP_N);
}

onMounted(async () => {
	try {
		const data = await api.get('/img?page=1&size=50');
		list.value = buildList((data.list || []).map(toWork));
	} catch (err) {
		console.warn('[top] 榜单没拉下来：', err.message);
	}
});
</script>

<style scoped src="@/assets/styles/top.css"></style>

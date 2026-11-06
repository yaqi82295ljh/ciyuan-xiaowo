<template>
	<header class="pg-head">
		<h1 class="pg-title">{{ pageTitle }}</h1>
	</header>

	<!-- 手机上侧栏是抽屉，两个分区之间需要个直接切换的入口 -->
	<nav class="cat-seg" aria-label="分区切换">
		<router-link class="cat-seg-item" :class="{ 'is-on': cat === 1 }" to="/wall">风景壁纸</router-link>
		<router-link class="cat-seg-item" :class="{ 'is-on': cat === 2 }" to="/char">人物立绘</router-link>
	</nav>

	<WaterfallGrid :items="works" empty-text="这个分区还空着，等第一张作品来~"/>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { api } from '@/utils/api';
import { toWork } from '@/utils/format';
import { useUiStore } from '@/stores/ui';
import WaterfallGrid from '@/components/WaterfallGrid.vue';

const route = useRoute();
const ui = useUiStore();

const all = ref([]);

const cat = computed(() => Number(route.meta.cat) || 1);

const CAT_TITLE = { 1: '风景壁纸', 2: '人物立绘' };
const pageTitle = computed(() => CAT_TITLE[cat.value] || CAT_TITLE[1]);

const works = computed(() =>
	all.value
		.filter((w) => w.categoryId === cat.value)
		.sort((a, b) => (a.createdAt < b.createdAt ? 1 : a.createdAt > b.createdAt ? -1 : b.id - a.id))
);

async function loadGallery() {
	try {
		const data = await api.get('/img?page=1&size=50');
		all.value = (data.list || []).map(toWork);
	} catch (err) {
		console.warn('[gallery] 作品没拉下来：', err.message);
		all.value = [];
	}
}

onMounted(loadGallery);

// 上传成功后刷新分区列表
watch(() => ui.lastUploaded, loadGallery);
</script>

<template>
	<header class="pg-head">
		<h1 class="pg-title">发现首页</h1>
	</header>

	<!-- 手机端：筛选面板从底部滑出，点遮罩收起 -->
	<div v-if="ui.filterSheet" class="filter-mask" @click="ui.closeFilterSheet()"></div>

	<section class="filter" :class="{ 'is-collapsed': !filterOpen, 'is-open': ui.filterSheet }" aria-label="筛选作品">
		<div class="fl-sheet-head">
			<span class="fl-sheet-title">筛选作品</span>
			<button class="fl-sheet-x" type="button" aria-label="关闭筛选" @click="ui.closeFilterSheet()">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
					stroke-linecap="round" stroke-linejoin="round">
					<path d="M6 6l12 12M18 6 6 18"/>
				</svg>
			</button>
		</div>

		<button type="button" class="fl-fold" @click="filterOpen = !filterOpen">
			<span>{{ filterOpen ? '收起筛选' : '筛选作品（关键词 / 标签 / 排序）' }}</span>
			<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"
				stroke-linecap="round" stroke-linejoin="round">
				<path d="m6 9.5 6 6 6-6"/>
			</svg>
		</button>

		<form @submit.prevent="applyFilter">
			<!-- 第一行：关键词 + 排序 + 是否 AI -->
			<div class="fl-row">
				<div class="fl-search">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
						stroke-linecap="round" stroke-linejoin="round">
						<circle cx="11" cy="11" r="6.2"/>
						<path d="m15.6 15.6 4 4"/>
					</svg>
					<input v-model="keyword" class="fl-input" type="text" name="keyword"
						placeholder="和标签一起，点击保存…"/>
				</div>

				<div class="fl-right">
					<div class="fl-group">
						<span class="fl-label">排序</span>
						<div class="fl-chips">
							<label v-for="s in sortOptions" :key="s.value" class="fl-chip">
								<input v-model="sort" class="fl-chip-input" type="radio" name="sort" :value="s.value"/>
								<span>{{ s.text }}</span>
							</label>
						</div>
					</div>

					<div class="fl-group">
						<span class="fl-label">AI 作品</span>
						<div class="fl-chips">
							<label v-for="o in aiOptions" :key="o.value" class="fl-chip">
								<input v-model="ai" class="fl-chip-input" type="radio" name="ai" :value="o.value"/>
								<span>{{ o.text }}</span>
							</label>
						</div>
					</div>
				</div>
			</div>

			<!-- 第二行：标签筛选 -->
			<div class="fl-row">
				<span class="fl-label">标签</span>
				<input v-model="tagsInput" class="fl-input fl-input--tag" type="text" name="tags"
					placeholder="也可手输，空格隔开"/>

				<span class="fl-label">热门</span>
				<div class="fl-hot" :class="{ 'is-open': moreOpen }">
					<label
						v-for="(t, i) in tags.list"
						:key="t.name"
						class="fl-chip"
						:class="{ 'fl-chip--extra': i >= 4 }"
					>
						<input
							class="fl-chip-input"
							type="checkbox"
							name="tags"
							:value="t.name"
							:checked="selectedTags.includes(t.name)"
							@change="toggleTag(t.name)"
						/>
						<span>#{{ t.name }}</span>
					</label>

					<button type="button" class="fl-more" @click="toggleMore">
						<span class="fl-more-more">展开更多</span>
						<span class="fl-more-less">收起</span>
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="m6 9.5 6 6 6-6"/>
						</svg>
					</button>
				</div>

				<div class="fl-actions">
					<button class="fl-btn fl-btn--ghost" type="button" @click="resetFilter">清空</button>
					<button class="fl-btn" type="submit">保存</button>
				</div>
			</div>
		</form>
	</section>

	<WaterfallGrid :items="works" :new-id="newId"/>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api } from '@/utils/api';
import { splitTags, toWork } from '@/utils/format';
import { useTagStore } from '@/stores/tags';
import { useUiStore } from '@/stores/ui';
import WaterfallGrid from '@/components/WaterfallGrid.vue';

const PAGE_SIZE = 20;

const route = useRoute();
const router = useRouter();
const tags = useTagStore();
const ui = useUiStore();

const sortOptions = [
	{ value: 'new', text: '最新' },
	{ value: 'like', text: '最多点赞' },
	{ value: 'fav', text: '最多收藏' }
];
const aiOptions = [
	{ value: 'all', text: '全部' },
	{ value: 'hide', text: '隐藏 AI' },
	{ value: 'only', text: '仅 AI' }
];

const works = ref([]);
const newId = ref(null);

// 筛选条件：先按 URL 回显，保存时再推回 URL
const keyword = ref('');
const tagsInput = ref('');
const sort = ref('new');
const ai = ref('all');

// 移动端默认收起筛选，桌面端展开
const filterOpen = ref(!window.matchMedia('(max-width: 760px)').matches);
const moreOpen = ref(false);

const selectedTags = computed(() => splitTags(tagsInput.value));

// 手输框和热门标签是双向同步的：点标签改输入框，改输入框勾标签
function toggleTag(name) {
	const list = selectedTags.value;
	const i = list.indexOf(name);
	if (i >= 0) list.splice(i, 1);
	else list.push(name);
	tagsInput.value = list.join(' ');
}

function toggleMore() {
	moreOpen.value = !moreOpen.value;
	try {
		localStorage.setItem('flHotOpen', moreOpen.value ? '1' : '0');
	} catch { /* 存不下就算了 */ }
}

function syncFromQuery() {
	keyword.value = String(route.query.keyword || '');
	tagsInput.value = String(route.query.tags || '').replace(/[,，、]+/g, ' ');
	sort.value = String(route.query.sort || 'new');
	ai.value = String(route.query.ai || 'all');
	// 选中的是折叠起来的标签时，自动展开
	const extras = tags.list.slice(4).map((t) => t.name);
	if (selectedTags.value.some((t) => extras.includes(t))) moreOpen.value = true;
}

function applyFilter() {
	const query = {};
	if (keyword.value.trim()) query.keyword = keyword.value.trim();
	if (selectedTags.value.length) query.tags = selectedTags.value.join(',');
	if (sort.value !== 'new') query.sort = sort.value;
	if (ai.value !== 'all') query.ai = ai.value;
	ui.closeFilterSheet();
	router.push({ path: '/', query });
}

function resetFilter() {
	ui.closeFilterSheet();
	router.push({ path: '/' });
}

async function loadFeed() {
	const params = new URLSearchParams({
		page: '1',
		size: String(PAGE_SIZE),
		ai: ai.value,
		sort: sort.value
	});
	if (keyword.value.trim()) params.set('keyword', keyword.value.trim());
	if (selectedTags.value.length) params.set('tags', selectedTags.value.join(','));

	try {
		const data = await api.get('/img?' + params.toString());
		works.value = (data.list || []).map(toWork);
	} catch (err) {
		console.warn('[home] 作品没拉下来：', err.message);
		works.value = [];
	}
}

onMounted(() => {
	tags.fetchTags().then(syncFromQuery);
	syncFromQuery();
	loadFeed();
	try {
		moreOpen.value = moreOpen.value || localStorage.getItem('flHotOpen') === '1';
	} catch { /* 默认收起 */ }
});

// 顶栏搜索 / 侧栏标签点进来都是改 query，统一在这里响应
watch(() => route.query, () => {
	syncFromQuery();
	loadFeed();
});

// 上传成功后刷新，让新作品出现在最前面
watch(() => ui.lastUploaded, () => {
	loadFeed();
});
</script>

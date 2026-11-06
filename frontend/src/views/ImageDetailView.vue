<template>
	<div class="dt-page">
		<header class="dt-top">
			<router-link class="dt-back" to="/">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
					stroke-linecap="round" stroke-linejoin="round">
					<path d="M20 12H5M11 6l-6 6 6 6"/>
				</svg>
				返回
			</router-link>

			<router-link class="dt-brand" to="/">
				<img class="dt-logo" src="/resources/logo.png" alt="次元小窝"/>
			</router-link>
		</header>

		<main v-if="detail" class="dt-wrap">
			<nav class="dt-crumb" aria-label="面包屑">
				<router-link to="/">发现首页</router-link>
				<span class="dt-crumb-sep">/</span>
				<router-link :to="catPath">{{ catName(image.categoryId) }}</router-link>
				<span class="dt-crumb-sep">/</span>
				<span class="dt-crumb-now">{{ image.title }}</span>
			</nav>

			<article class="dt">
				<!-- 左：大图 -->
				<section class="dt-stage">
					<div class="dt-frame">
						<img class="dt-img" :src="image.url || image.thumbUrl" :alt="image.title" @load="imgLoaded = true"/>
						<div v-if="!imgLoaded && !imgError" class="dt-loading">加载中…</div>
						<div v-if="imgError" class="dt-loading">图片加载失败</div>
					</div>

					<div class="dt-pager">
						<!-- 到头了就没有目标路由，必须渲染成不可点的 span；
						     给 router-link 传空字符串会解析成"当前页"，看起来禁用其实还能点 -->
						<router-link v-if="prevId" class="dt-pager-btn" :to="'/image/' + prevId">← 上一张</router-link>
						<span v-else class="dt-pager-btn is-off" aria-disabled="true">← 上一张</span>

						<span class="dt-pager-tip">按 ← → 键也能翻</span>

						<router-link v-if="nextId" class="dt-pager-btn" :to="'/image/' + nextId">下一张 →</router-link>
						<span v-else class="dt-pager-btn is-off" aria-disabled="true">下一张 →</span>
					</div>
				</section>

				<aside class="dt-side">
					<p class="dt-eyebrow">{{ image.isAi ? 'AI ' + catEyebrow : catEyebrow }}</p>

					<h1 class="dt-title">{{ image.title }}</h1>

					<div class="dt-author">
						<span class="dt-avatar">
							<img v-if="authorAvatar" :src="authorAvatar" :alt="authorName" @error="avatarDead = true"/>
							<template v-else>{{ (authorName || '?').slice(0, 1) }}</template>
						</span>
						<div class="dt-author-text">
							<p class="dt-author-name">{{ authorName }}</p>
							<p class="dt-author-time">{{ fmtTime(image.createdAt) }}</p>
						</div>
					</div>

					<div class="dt-tags">
						<router-link v-for="t in image.tags || []" :key="t" class="dt-tag" :to="{ path: '/', query: { tags: t } }">
							#{{ t }}
						</router-link>
					</div>

					<div class="dt-stats">
						<div class="dt-stat"><b>{{ image.views || 0 }}</b><span>浏览</span></div>
						<div class="dt-stat"><b>{{ image.likeCount || 0 }}</b><span>点赞</span></div>
						<div class="dt-stat"><b>{{ image.favCount || 0 }}</b><span>收藏</span></div>
						<div class="dt-stat"><b>{{ image.commentCount || 0 }}</b><span>评论</span></div>
					</div>

					<div class="dt-acts">
						<button class="dt-btn dt-btn--like" :class="{ 'is-on': liked }" type="button" :disabled="acting" @click="toggleLike">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
								stroke-linecap="round" stroke-linejoin="round">
								<path d="M12 20.3 4.9 13.2a4.8 4.8 0 1 1 6.8-6.8l.3.3.3-.3a4.8 4.8 0 1 1 6.8 6.8z"/>
							</svg>
							<span>{{ liked ? '已赞' : '点赞' }}</span>
						</button>

						<button class="dt-btn" :class="{ 'is-fav': faved }" type="button" :disabled="acting" @click="toggleFav">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
								stroke-linecap="round" stroke-linejoin="round">
								<path d="m12 4.6 2.3 4.9 5.2.7-3.8 3.7.9 5.3-4.6-2.5-4.6 2.5.9-5.3L4.5 10.2l5.2-.7z"/>
							</svg>
							<span>{{ faved ? '已收藏' : '收藏' }}</span>
						</button>

						<button class="dt-btn dt-btn--ghost" type="button" @click="download">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
								stroke-linecap="round" stroke-linejoin="round">
								<path d="M12 4v11M12 15l-4-4M12 15l4-4"/>
								<path d="M4.5 19.5h15"/>
							</svg>
							原图
						</button>
					</div>

					<dl class="dt-meta">
						<div><dt>尺寸</dt><dd>{{ image.width || '—' }} × {{ image.height || '—' }}</dd></div>
						<div><dt>分区</dt><dd>{{ catName(image.categoryId) }}</dd></div>
						<div><dt>是否AI</dt><dd>{{ image.isAi ? 'AI 生成' : '手绘 / 实拍' }}</dd></div>
						<div><dt>编号</dt><dd>#{{ image.id }}</dd></div>
					</dl>
				</aside>
			</article>

			<!-- 评论区 -->
			<section class="dt-comments" aria-label="评论">
				<div class="dt-sec-head">
					<h2 class="dt-sec-title">评论</h2>
					<span class="dt-sec-note">{{ comments.length }} 条</span>
				</div>

				<form class="dt-cform" novalidate @submit.prevent="sendComment">
					<textarea
						v-model="commentText"
						class="dt-cinput"
						rows="3"
						maxlength="300"
						placeholder="说点什么…（登录后才能发表）"
						@input="commentCount = commentText.length"
					></textarea>

					<div class="dt-cform-foot">
						<span class="dt-ccount">{{ commentCount }} / 300</span>
						<button class="dt-btn dt-btn--primary" type="submit">发表</button>
					</div>
				</form>

				<ul v-if="comments.length" class="dt-clist">
					<li v-for="c in comments" :key="c.id" class="dt-comment">
						<div class="dt-cavatar">
							<img v-if="c.avatar && !c.avatarDead" :src="c.avatar" :alt="c.name"
								@error="c.avatarDead = true"/>
							<template v-else>{{ (c.name || '?').slice(0, 1) }}</template>
						</div>
						<div class="dt-cbody">
							<div class="dt-chead">
								<span class="dt-cname">{{ c.name }}</span>
								<span class="dt-ctime">{{ c.time }}</span>
							</div>
							<p class="dt-ctext">{{ c.text }}</p>
						</div>
					</li>
				</ul>

				<div v-else class="dt-empty">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
						stroke-linecap="round" stroke-linejoin="round">
						<path d="M20.5 12a7.5 7.5 0 0 1-7.5 7.5H8L4 22v-4.6A7.5 7.5 0 0 1 4 5.4h9A7.5 7.5 0 0 1 20.5 12z"/>
					</svg>
					<p>还没有评论，来坐第一排 ~</p>
				</div>
			</section>

			<section class="dt-related" aria-label="同分区推荐">
				<div class="dt-sec-head">
					<h2 class="dt-sec-title">同分区推荐</h2>
					<router-link class="dt-sec-more" :to="catPath">查看更多 →</router-link>
				</div>
				<div v-if="related.length" class="dt-rgrid">
					<router-link v-for="it in related" :key="it.id" class="dt-rcard" :to="'/image/' + it.id">
						<img class="dt-rthumb" :src="it.thumbUrl || it.url" :alt="it.title" loading="lazy"/>
						<p class="dt-rtitle">{{ it.title }}</p>
					</router-link>
				</div>
				<p v-else class="dt-empty">这个分区还只有这一张 ~</p>
			</section>
		</main>

		<main v-else class="dt-wrap">
			<p class="dt-loading" style="position:static;padding:60px 0;text-align:center;">
				{{ loadError || '加载中…' }}
			</p>
		</main>

		<!-- 空态不占位：.dt-toast 自带白底+边框+圆角，没有文字时会露成一个小白胶囊 -->
		<div v-if="toastText" class="dt-toast"><span>{{ toastText }}</span></div>
	</div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api, ApiError } from '@/utils/api';
import { catName, fmtTime } from '@/utils/format';

const route = useRoute();
const router = useRouter();
const imageId = computed(() => Number(route.params.id));

const detail = ref(null);
const loadError = ref('');
const imgLoaded = ref(false);
const imgError = ref(false);
const avatarDead = ref(false);
const allImages = ref([]);
const comments = ref([]);
const commentText = ref('');
const commentCount = ref(0);
const acting = ref(false);
const toastText = ref('');
let toastTimer = null;

const image = computed(() => detail.value?.image || {});
const authorName = computed(() =>
	(detail.value?.author && (detail.value.author.nickname || detail.value.author.username)) ||
	'用户' + (image.value.userId || '?')
);
const authorAvatar = computed(() =>
	(!avatarDead.value && detail.value?.author && detail.value.author.avatar) || ''
);
const catPath = computed(() => (image.value.categoryId === 2 ? '/char' : '/wall'));
const catEyebrow = computed(() => (image.value.categoryId === 1 ? 'SCENERY' : 'ILLUSTRATION'));
const related = computed(() => {
	if (!image.value.categoryId) return [];
	return allImages.value
		.filter((it) => it.categoryId === image.value.categoryId && it.id !== image.value.id)
		.slice(0, 6);
});
const prevId = computed(() => {
	const idx = allImages.value.findIndex((it) => it.id === image.value.id);
	return idx > 0 ? allImages.value[idx - 1].id : null;
});
const nextId = computed(() => {
	const idx = allImages.value.findIndex((it) => it.id === image.value.id);
	return idx >= 0 && idx < allImages.value.length - 1 ? allImages.value[idx + 1].id : null;
});

const liked = computed(() => !!detail.value?.liked);
const faved = computed(() => !!detail.value?.faved);

function toast(msg) {
	toastText.value = msg;
	if (toastTimer) clearTimeout(toastTimer);
	toastTimer = setTimeout(() => { toastText.value = ''; }, 1800);
}

function notLoginTip(err) {
	if (err instanceof ApiError && err.code === 401) {
		toast('请先登录再进行操作');
		return true;
	}
	toast(err.message);
	return false;
}

async function loadDetail() {
	detail.value = null;
	loadError.value = '';
	imgLoaded.value = false;
	imgError.value = false;
	avatarDead.value = false;
	try {
		const [d, list, cs] = await Promise.all([
			api.get('/img/' + imageId.value),
			api.get('/img?page=1&size=200'),
			api.get('/img/' + imageId.value + '/comments')
		]);
		detail.value = d;
		allImages.value = (list && list.list) || [];
		comments.value = (cs || []).map((c) => ({
			id: c.id,
			name: c.nickname || '用户' + c.userId,
			avatar: c.avatar,
			time: fmtTime(c.createdAt),
			text: c.content
		}));
		document.title = (d.image.title || '图片') + ' · 次元小窝';
	} catch (err) {
		loadError.value = '加载失败：' + err.message;
	}
}

async function toggleLike() {
	if (acting.value) return;
	acting.value = true;
	try {
		const data = await api.post('/img/' + imageId.value + '/like');
		detail.value.liked = data.liked;
		detail.value.image.likeCount = data.likeCount;
		toast(data.liked ? '已点赞' : '已取消点赞');
	} catch (err) { notLoginTip(err); }
	finally { acting.value = false; }
}

async function toggleFav() {
	if (acting.value) return;
	acting.value = true;
	try {
		const data = await api.post('/img/' + imageId.value + '/favorite');
		detail.value.faved = data.faved;
		detail.value.image.favCount = data.favCount;
		toast(data.faved ? '已收藏' : '已取消收藏');
	} catch (err) { notLoginTip(err); }
	finally { acting.value = false; }
}

async function sendComment() {
	const text = commentText.value.trim();
	if (!text) return;
	try {
		await api.post('/img/' + imageId.value + '/comments', { content: text });
		commentText.value = '';
		commentCount.value = 0;
		toast('评论成功');
		const cs = await api.get('/img/' + imageId.value + '/comments');
		comments.value = cs.map((c) => ({
			id: c.id,
			name: c.nickname || '用户' + c.userId,
			avatar: c.avatar,
			time: fmtTime(c.createdAt),
			text: c.content
		}));
		detail.value.image.commentCount = comments.value.length;
	} catch (err) { notLoginTip(err); }
}

// 原图下载：走 blob 拿一下再触发下载，避免直接点链接被代理缓存成预览
async function download() {
	const url = image.value.url || image.value.thumbUrl;
	const ext = (url.split('.').pop() || 'jpg').toLowerCase();
	const name = (image.value.title || 'image').replace(/[\\/:*?"<>|]/g, '_') + '.' + ext;
	try {
		const res = await fetch(url);
		if (!res.ok) throw new Error('HTTP ' + res.status);
		const blob = await res.blob();
		const tmpUrl = URL.createObjectURL(blob);
		const a = document.createElement('a');
		a.href = tmpUrl;
		a.download = name;
		document.body.appendChild(a);
		a.click();
		a.remove();
		setTimeout(() => URL.revokeObjectURL(tmpUrl), 1000);
	} catch {
		toast('下载失败，请稍后再试');
	}
}

// ← → 翻页
function onKeydown(e) {
	const t = e.target;
	if (t && (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA' || t.isContentEditable)) return;
	if (e.key === 'ArrowLeft' && prevId.value) router.push('/image/' + prevId.value);
	else if (e.key === 'ArrowRight' && nextId.value) router.push('/image/' + nextId.value);
}

onMounted(() => {
	loadDetail();
	window.addEventListener('keydown', onKeydown);
});

onBeforeUnmount(() => {
	window.removeEventListener('keydown', onKeydown);
});

// 上一张 / 下一张是同一组件换参数，watch 参数重新加载
watch(() => route.params.id, () => {
	if (route.params.id) loadDetail();
});
</script>

<style scoped src="@/assets/styles/detail.css"></style>

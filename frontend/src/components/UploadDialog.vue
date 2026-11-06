<template>
	<Teleport to="body">
		<div v-if="ui.uploadOpen" class="up-mask" @mousedown.self="close">
			<div class="up-panel" role="dialog" aria-modal="true" aria-labelledby="upTitle">
				<header class="up-head">
					<div class="up-head-text">
						<p class="up-eyebrow">UPLOAD</p>
						<h2 class="up-title" id="upTitle">上传作品</h2>
					</div>
					<button class="up-close" type="button" aria-label="关闭" @click="close">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="m6.6 6.6 10.8 10.8M17.4 6.6 6.6 17.4"/>
						</svg>
					</button>
				</header>

				<!-- 表单 -->
				<div v-show="!done" class="up-body">
					<div v-if="alertMsg" class="up-alert" role="alert">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
							stroke-linecap="round" stroke-linejoin="round">
							<circle cx="12" cy="12" r="8.5"/><path d="M12 7.8v5M12 16.2h.01"/>
						</svg>
						<span>{{ alertMsg }}</span>
					</div>

					<div
						ref="dropRef"
						class="up-drop"
						:class="{ 'has-file': !!file, 'is-over': dragOver }"
						role="button"
						tabindex="0"
						aria-label="选择图片"
						@click="pickFile"
						@keydown.enter.prevent="pickFile"
						@keydown.space.prevent="pickFile"
						@dragenter.prevent="dragOver = true"
						@dragover.prevent="dragOver = true"
						@dragleave.prevent="dragOver = false"
						@drop.prevent="onDrop"
					>
						<input ref="fileRef" class="up-file" type="file" accept="image/*" @change="onPick"/>
						<div v-show="!file" class="up-drop-hints">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
								stroke-linecap="round" stroke-linejoin="round">
								<path d="M12 16V4M12 4 8 8M12 4l4 4"/>
								<path d="M4 16v2.5A1.5 1.5 0 0 0 5.5 20h13a1.5 1.5 0 0 0 1.5-1.5V16"/>
							</svg>
							<p class="up-drop-main">把图片拖到这里，或点击选择</p>
							<p class="up-drop-hint">JPG / PNG / GIF / WebP，单张不超过 30MB</p>
						</div>
						<div v-show="file" class="up-preview">
							<div class="up-preview-frame">
								<img v-if="previewUrl" :src="previewUrl" alt="待上传图片的预览" @load="onPreviewLoad"/>
								<div class="up-preview-meta">
									<span class="up-preview-name">{{ fileMeta }}</span>
								</div>
							</div>
						</div>
					</div>

					<div class="up-row">
						<label class="up-row-label" for="upTitleInput">标题 <span class="up-req"></span></label>
						<input
							id="upTitleInput"
							v-model="title"
							class="up-input"
							type="text"
							maxlength="40"
							placeholder="给作品起个名字"
							@input="alertMsg = ''"
						/>
					</div>

					<div class="up-row">
						<span class="up-row-label">分区 <span class="up-req">*</span>
							<span class="up-row-hint">{{ catHint }}</span>
						</span>
						<div class="up-cats">
							<template v-for="c in cats" :key="c.key">
								<input
									:id="'upCat_' + c.key"
									v-model="cat"
									class="up-cat-input"
									type="radio"
									:value="c.key"
									:disabled="catLocked && pageCatKey !== c.key"
								/>
								<label class="up-cat" :for="'upCat_' + c.key">{{ c.name }}</label>
							</template>
						</div>
					</div>

					<div class="up-row">
						<label class="up-row-label" for="upTags">标签
							<span class="up-row-hint">用逗号或空格分开</span>
						</label>
						<input id="upTags" v-model="tagsRaw" class="up-input" type="text" placeholder="星空, 少女"/>
					</div>

					<div class="up-cats">
						<input id="upAi" v-model="ai" class="up-cat-input" type="checkbox"/>
						<label class="up-cat" for="upAi">这张是 AI 生成</label>
					</div>
				</div>

				<!-- 发布成功 -->
				<div v-if="done" class="up-done">
					<div class="up-done-icon">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="m5 12.5 4.6 4.5L19 7.5"/>
						</svg>
					</div>
					<p class="up-done-title">发布成功</p>
					<p class="up-done-text">作品已经放进小窝啦，去首页和分区都能看到它 ~</p>
					<button class="up-btn up-btn--primary" type="button" @click="close">完成</button>
				</div>

				<footer v-show="!done" class="up-foot">
					<div v-if="file" class="up-foot-file">
						<button class="up-link" type="button" @click.stop="pickFile">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
								stroke-linecap="round" stroke-linejoin="round">
								<path d="M4.5 12a7.5 7.5 0 1 1 2.2 5.3"/><path d="M4.5 8v4h4"/>
							</svg>
							重选
						</button>
						<button class="up-link up-link--danger" type="button" @click.stop="clearFile">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"
								stroke-linecap="round" stroke-linejoin="round">
								<path d="M4 7h16"/>
								<path d="M9.5 7V5.2A1.2 1.2 0 0 1 10.7 4h2.6a1.2 1.2 0 0 1 1.2 1.2V7"/>
								<path d="M6.5 7l.9 12.1a1.5 1.5 0 0 0 1.5 1.4h6.2a1.5 1.5 0 0 0 1.5-1.4L17.5 7"/>
								<path d="M10.5 11v5.5M13.5 11v5.5"/>
							</svg>
							移除
						</button>
					</div>
					<button class="up-btn up-btn--ghost" type="button" @click="close">取消</button>
					<button class="up-btn up-btn--primary" type="button" :disabled="submitting" @click="submit">
						{{ submitting ? '发布中…' : '发布' }}
					</button>
				</footer>
			</div>
		</div>
	</Teleport>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUiStore } from '@/stores/ui';
import { api } from '@/utils/api';
import { humanSize, splitTags } from '@/utils/format';

const MAX_SIZE = 30 * 1024 * 1024;

const route = useRoute();
const router = useRouter();
const ui = useUiStore();

const fileRef = ref(null);
const dropRef = ref(null);

const file = ref(null);
const previewUrl = ref('');
const fileMeta = ref('');
const title = ref('');
const cat = ref('wall');
const tagsRaw = ref('');
const ai = ref(false);
const alertMsg = ref('');
const dragOver = ref(false);
const submitting = ref(false);
const done = ref(false);

let imgW = 0;
let imgH = 0;
let prevOverflow = '';
let prevPadRight = '';

const cats = [
	{ key: 'wall', name: '风景壁纸' },
	{ key: 'char', name: '人物立绘' }
];

// 分区页里只允许传本分区的作品（路由 meta.cat：1=wall 2=char）
const pageCatKey = computed(() => (route.meta.cat === 2 ? 'char' : route.meta.cat === 1 ? 'wall' : ''));
const catLocked = computed(() => !!pageCatKey.value);
const catHint = computed(() => {
	if (!pageCatKey.value) return '';
	const c = cats.find((c) => c.key === pageCatKey.value);
	return '本页只收「' + c.name + '」的作品';
});

watch(() => ui.uploadOpen, (open) => {
	if (open) {
		resetForm();
		lockScroll();
	} else {
		unlockScroll();
	}
});

// 切路由时如果弹窗还开着，分区默认值要跟着变
watch(pageCatKey, () => { if (ui.uploadOpen) cat.value = pageCatKey.value || 'wall'; });

function resetForm() {
	clearFile();
	title.value = '';
	tagsRaw.value = '';
	ai.value = false;
	alertMsg.value = '';
	dragOver.value = false;
	submitting.value = false;
	done.value = false;
	cat.value = pageCatKey.value || 'wall';
}

function lockScroll() {
	const gap = window.innerWidth - document.documentElement.clientWidth;
	prevOverflow = document.body.style.overflow;
	prevPadRight = document.body.style.paddingRight;
	const base = parseFloat(getComputedStyle(document.body).paddingRight) || 0;
	if (gap > 0) document.body.style.paddingRight = base + gap + 'px';
	document.body.style.overflow = 'hidden';
}

function unlockScroll() {
	document.body.style.overflow = prevOverflow;
	document.body.style.paddingRight = prevPadRight;
}

function close() {
	ui.closeUpload();
}

function onKeydown(e) {
	if (e.key === 'Escape') close();
}

function pickFile() {
	fileRef.value && fileRef.value.click();
}

function onPick(e) {
	const f = e.target.files && e.target.files[0];
	if (f) setFile(f);
	e.target.value = '';
}

function onDrop(e) {
	dragOver.value = false;
	const f = e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files[0];
	if (f) setFile(f);
}

// 阻止把图片拖到弹窗外时被浏览器直接打开
function preventWindowDrop(e) { e.preventDefault(); }

function setFile(f) {
	if (!f.type || !f.type.startsWith('image/')) {
		clearFile();
		alertMsg.value = '只能上传图片文件（现在这个是「' + (f.type || '未知类型') + '」）。';
		return;
	}
	if (f.size > MAX_SIZE) {
		clearFile();
		alertMsg.value = '图片太大了（' + humanSize(f.size) + '），最大 30MB 哦 ~';
		return;
	}

	alertMsg.value = '';
	revokePreview();
	file.value = f;
	previewUrl.value = URL.createObjectURL(f);
	imgW = 0;
	imgH = 0;
	fileMeta.value = humanSize(f.size);
}

function onPreviewLoad(e) {
	imgW = e.target.naturalWidth;
	imgH = e.target.naturalHeight;
	if (file.value) {
		fileMeta.value = imgW + '×' + imgH + ' · ' + humanSize(file.value.size);
	}
}

function clearFile() {
	revokePreview();
	file.value = null;
	fileMeta.value = '';
	imgW = 0;
	imgH = 0;
}

function revokePreview() {
	if (previewUrl.value) {
		URL.revokeObjectURL(previewUrl.value);
		previewUrl.value = '';
	}
}

async function submit() {
	if (!file.value) {
		alertMsg.value = '先选一张图片。';
		dropRef.value && dropRef.value.focus();
		return;
	}

	const fd = new FormData();
	fd.append('file', file.value);
	fd.append('title', title.value.trim() || '无标题');
	fd.append('cat', cat.value);
	fd.append('width', imgW);
	fd.append('height', imgH);
	fd.append('ai', ai.value ? 'true' : 'false');
	fd.append('tags', splitTags(tagsRaw.value).join(','));

	submitting.value = true;
	alertMsg.value = '';
	try {
		const url = await api.postForm('/imgUpload', fd);
		done.value = true;
		// 通知首页 / 个人中心刷新列表
		ui.notifyUploaded({ url, title: title.value.trim() || '无标题', cat: cat.value });
		revokePreview();
		file.value = null;
	} catch (err) {
		if (err.code === 401) {
			alertMsg.value = '登录已过期，正在跳转到登录页…';
			setTimeout(() => {
				close();
				router.push({ path: '/login', query: { redirect: route.fullPath } });
			}, 1200);
		} else {
			alertMsg.value = '发布失败：' + err.message;
		}
	} finally {
		submitting.value = false;
	}
}

onMounted(() => {
	document.addEventListener('keydown', onKeydown);
	window.addEventListener('dragover', preventWindowDrop);
	window.addEventListener('drop', preventWindowDrop);
});

onBeforeUnmount(() => {
	document.removeEventListener('keydown', onKeydown);
	window.removeEventListener('dragover', preventWindowDrop);
	window.removeEventListener('drop', preventWindowDrop);
	unlockScroll();
	revokePreview();
});
</script>

<style scoped src="@/assets/styles/upload.css"></style>

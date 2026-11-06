<template>
	<div class="my-view">
		<section class="profile">
			<div class="avatar">
				<svg v-if="!auth.user.avatar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"
					stroke-linecap="round" stroke-linejoin="round">
					<circle cx="12" cy="8.2" r="3.5"/>
					<path d="M5.5 20a6.5 6.5 0 0 1 13 0"/>
				</svg>
				<img v-else :src="auth.user.avatar" alt="我的头像" @error="auth.user.avatar = ''"/>
			</div>

			<div class="profile-info">
				<h2 class="profile-name">
					<span>{{ auth.displayName }}</span>
					<span class="badge" :class="roleCls">{{ roleText }}</span>
					<span v-if="auth.user.status === 1" class="badge badge--banned">已封禁</span>
				</h2>

				<div class="profile-meta">
					<span>
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
							stroke-linecap="round" stroke-linejoin="round">
							<circle cx="12" cy="8.2" r="3.5"/>
							<path d="M5.5 20a6.5 6.5 0 0 1 13 0"/>
						</svg>
						@{{ auth.user.username }}
					</span>
					<span>
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
							stroke-linecap="round" stroke-linejoin="round">
							<rect x="3.5" y="5.5" width="17" height="14" rx="2.5"/>
							<path d="M8 3.5v4M16 3.5v4M3.5 10h17"/>
						</svg>
						{{ fmtDate(auth.user.createdAt) }} 加入
					</span>
					<span>
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="M20.5 14.8A8.5 8.5 0 1 1 9.2 3.5a6.8 6.8 0 0 0 11.3 11.3z"/>
						</svg>
						{{ form.darkMode ? '暗色模式' : '亮色模式' }}
					</span>
				</div>
			</div>

			<div class="profile-stats">
				<div class="stat"><b>{{ works.length }}</b><span>作品</span></div>
				<div class="stat"><b>0</b><span>收藏</span></div>
				<div class="stat"><b>0</b><span>获赞</span></div>
			</div>
		</section>

		<div class="panel-wrap">
			<nav class="tabs" aria-label="个人中心分区">
				<button
					v-for="t in TABS" :key="t.name"
					class="tab" :class="{ 'is-active': tab === t.name }"
					type="button"
					@click="setTab(t.name)"
				>
					<span v-html="t.icon"></span>
					{{ t.text }}
					<span v-if="t.count" class="tab-count">{{ t.count }}</span>
				</button>
			</nav>

			<!-- 我的作品 -->
			<section v-show="tab === 'images'" class="panel">
				<div class="panel-inner">
					<WaterfallGrid :items="works" caption empty-text="还没有上传过作品"/>
				</div>
			</section>

			<!-- 我的收藏（接口未上线，先空态） -->
			<section v-show="tab === 'fav'" class="panel">
				<div class="panel-inner">
					<div class="empty">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="M12 20.3 4.9 13.2a4.8 4.8 0 1 1 6.8-6.8l.3.3.3-.3a4.8 4.8 0 1 1 6.8 6.8z"/>
						</svg>
						<p>收藏夹还是空的</p>
						<router-link to="/">去发现好图 →</router-link>
					</div>
				</div>
			</section>

			<!-- 我的评论（接口未上线，先空态） -->
			<section v-show="tab === 'comments'" class="panel">
				<div class="panel-inner">
					<div class="empty">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"
							stroke-linecap="round" stroke-linejoin="round">
							<path d="M20.5 12a7.5 7.5 0 0 1-7.5 7.5H8L4 22v-4.6A7.5 7.5 0 0 1 4 5.4h9A7.5 7.5 0 0 1 20.5 12z"/>
						</svg>
						<p>还没有发表过评论</p>
					</div>
				</div>
			</section>

			<!-- 账号设置 -->
			<section v-show="tab === 'settings'" class="panel">
				<div class="panel-inner">
					<div class="toast" :class="{ 'toast--warn': toastWarn }" :hidden="!toastMsg">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
							stroke-linecap="round" stroke-linejoin="round">
							<circle cx="12" cy="12" r="9"/>
							<path d="M12 7.5v5.5M12 16.4v.1"/>
						</svg>
						<span>{{ toastMsg }}</span>
					</div>

					<form class="form" novalidate @submit.prevent="submit">
						<div class="field">
							<label class="field-label" for="fUsername">
								用户名
								<span class="field-hint">登录用，不可修改</span>
							</label>
							<input class="input" id="fUsername" type="text" :value="auth.user.username" readonly/>
						</div>

						<div class="field">
							<label class="field-label" for="fNickname">
								昵称
								<span class="field-hint">最多 50 字</span>
							</label>
							<input id="fNickname" v-model="form.nickname" class="input" type="text"
								maxlength="50" placeholder="想让大家怎么称呼你？" required/>
						</div>

						<div class="field">
							<span class="field-label">头像</span>

							<div class="avatar-pick">
								<span class="avatar-pick__preview">
									<svg v-if="!form.avatar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
										stroke-linecap="round" stroke-linejoin="round">
										<circle cx="12" cy="12" r="9"/>
										<circle cx="12" cy="10" r="2.6"/>
										<path d="M6.8 18.6a6 6 0 0 1 10.4 0"/>
									</svg>
									<img v-else :src="form.avatar" alt="头像预览" @error="form.avatar = ''"/>
								</span>

								<div class="avatar-pick__btns">
									<input id="fAvatarFile" ref="avatarFileRef" class="avatar-file" type="file"
										accept="image/png,image/jpeg,image/gif,image/webp" @change="onPickAvatar"/>
									<label class="btn btn--ghost btn--sm" for="fAvatarFile">选择图片</label>
									<button class="btn btn--quiet btn--sm" type="button" @click="clearAvatar">恢复默认</button>
								</div>

								<span class="avatar-pick__note">{{ avatarNote }}</span>
							</div>

							<AvatarCropper ref="cropperRef" @cropped="onCropped"/>
						</div>

						<div class="field">
							<span class="field-label">暗色模式</span>
							<label class="switch">
								<input v-model="form.darkMode" class="switch-input" type="checkbox"/>
								<span class="switch-track"></span>
								<span>{{ form.darkMode ? '已开启' : '关闭' }}</span>
							</label>
						</div>

						<div class="form-actions">
							<button class="btn btn--primary" type="submit">保存修改</button>
							<button class="btn btn--ghost" type="button" @click="resetForm">重置</button>
						</div>
					</form>
				</div>
			</section>
		</div>
	</div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';
import { api } from '@/utils/api';
import { fmtDate, toWork } from '@/utils/format';
import AvatarCropper from '@/components/AvatarCropper.vue';
import WaterfallGrid from '@/components/WaterfallGrid.vue';

const ROLE = {
	0: { text: '普通用户', cls: 'badge--user' },
	1: { text: '萌主', cls: 'badge--owner' },
	2: { text: '站长', cls: 'badge--admin' }
};

const route = useRoute();
const auth = useAuthStore();
const ui = useUiStore();

const works = ref([]);
const tab = ref('images');
const toastMsg = ref('');
const toastWarn = ref(false);
const avatarNote = ref('未选择图片');
const avatarFileRef = ref(null);
const cropperRef = ref(null);

const form = reactive({ nickname: '', avatar: '', darkMode: false });
let origin = { nickname: '', avatar: '', darkMode: false };

const svg = (paths) =>
	'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" ' +
	'stroke-linecap="round" stroke-linejoin="round" style="width:15px;height:15px;opacity:0.75">' +
	paths + '</svg>';

const TABS = computed(() => [
	{ name: 'images', text: '我的作品', icon: svg('<rect x="3.5" y="5" width="17" height="14" rx="2.5"/><circle cx="9" cy="10" r="1.6"/><path d="m5 17.5 4.5-4.2 3.4 3 3-2.6 3.6 3.3"/>'), count: works.value.length },
	{ name: 'fav', text: '我的收藏', icon: svg('<path d="M12 20.3 4.9 13.2a4.8 4.8 0 1 1 6.8-6.8l.3.3.3-.3a4.8 4.8 0 1 1 6.8 6.8z"/>'), count: 0 },
	{ name: 'comments', text: '我的评论', icon: svg('<path d="M20.5 12a7.5 7.5 0 0 1-7.5 7.5H8L4 22v-4.6A7.5 7.5 0 0 1 4 5.4h9A7.5 7.5 0 0 1 20.5 12z"/>'), count: 0 },
	{ name: 'settings', text: '账号设置', icon: svg('<circle cx="12" cy="12" r="3"/><path d="M12 3v2.2M12 18.8V21M3 12h2.2M18.8 12H21M5.6 5.6l1.6 1.6M16.8 16.8l1.6 1.6M18.4 5.6l-1.6 1.6M7.2 16.8l-1.6 1.6"/>') }
]);

const roleText = computed(() => (ROLE[auth.user.role] || ROLE[0]).text);
const roleCls = computed(() => (ROLE[auth.user.role] || ROLE[0]).cls);

function showToast(msg, warn = false) {
	toastMsg.value = msg;
	toastWarn.value = warn;
}

function setTab(name) {
	tab.value = name;
	// 同步到 hash：侧栏「我的收藏」靠它定位
	history.replaceState(null, '', '/my' + (name === 'images' ? '' : '#' + name));
}

function initForm() {
	form.nickname = auth.user.nickname || '';
	form.avatar = auth.user.avatar || '';
	form.darkMode = auth.user.darkMode === true;
	origin = { ...form };
	avatarNote.value = form.avatar ? '已恢复为当前头像' : '未选择图片';
}

async function loadMyWorks() {
	try {
		const list = await api.get('/me/myImg');
		works.value = Array.isArray(list) ? list.map(toWork) : [];
	} catch (err) {
		console.warn('[my] 作品没拉下来：', err.message);
	}
}

// 选头像文件 → 进裁剪器
function onPickAvatar(e) {
	const file = e.target.files && e.target.files[0];
	e.target.value = '';
	if (!file) return;

	if (!/^image\//.test(file.type)) { showToast('只能选图片文件（png / jpg / gif / webp）。', true); return; }
	if (file.size > 5 * 1024 * 1024) { showToast('图片太大了，最大 5MB。', true); return; }

	const reader = new FileReader();
	reader.onerror = () => showToast('图片读取失败，请重试。', true);
	reader.onload = () => {
		const img = new Image();
		img.onerror = () => showToast('这个文件不是有效的图片。', true);
		img.onload = () => {
			avatarNote.value = '待裁剪：' + file.name;
			cropperRef.value.open(img, reader.result);
		};
		img.src = reader.result;
	};
	reader.readAsDataURL(file);
}

// 裁剪完成 → 上传拿到 URL → 填进表单
async function onCropped(blob) {
	avatarNote.value = '正在上传…';
	const fd = new FormData();
	fd.append('file', blob, 'avatar.jpg');
	try {
		const url = await api.postForm('/upload', fd);
		form.avatar = url;
		cropperRef.value.close();
		avatarNote.value = '已上传：200×200，保存后生效';
	} catch (err) {
		avatarNote.value = '上传失败';
		showToast(err.message, true);
	}
}

function clearAvatar() {
	form.avatar = '';
	avatarNote.value = '未选择图片';
	cropperRef.value.close();
	showToast('已恢复为默认头像（保存后生效）。');
}

function resetForm() {
	form.nickname = origin.nickname;
	form.avatar = origin.avatar;
	form.darkMode = origin.darkMode;
	avatarNote.value = form.avatar ? '已恢复为当前头像' : '未选择图片';
	cropperRef.value.close();
	toastMsg.value = '';
}

async function submit() {
	showToast('');

	if (cropperRef.value && cropperRef.value.isOpen()) {
		// 还有没确认裁剪的图，先导出
		cropperRef.value.cropNow(onCropped, (msg) => showToast(msg, true));
		return;
	}

	const nickname = form.nickname.trim();
	const avatar = form.avatar.trim();
	if (!nickname) return showToast('昵称不能为空。', true);
	if (nickname.length > 50) return showToast('昵称最多 50 个字。', true);
	if (avatar && !/^https?:\/\//i.test(avatar) && avatar.charAt(0) !== '/') {
		return showToast('头像格式不对，请重新选择图片。', true);
	}
	if (nickname === origin.nickname && avatar === origin.avatar && form.darkMode === origin.darkMode) {
		return showToast('没有任何改动。');
	}

	showToast('正在保存…');
	try {
		await api.post('/me', { nickname, avatar, darkMode: form.darkMode });
		showToast('保存成功。');
		await auth.fetchMe(true);
		initForm();
	} catch (err) {
		showToast('保存失败：' + err.message, true);
	}
}

function onPageShow(e) { if (e.persisted) loadMyWorks(); }

onMounted(async () => {
	// 路由守卫已保证登录，但资料可能不是最新，强制拉一次
	await auth.fetchMe(true);
	initForm();
	loadMyWorks();
	document.title = auth.displayName + ' · 个人中心';

	// 侧栏「我的收藏」进来是 /my#fav
	if (route.hash === '#fav') tab.value = 'fav';

	// bfcache 恢复时作品可能旧了
	window.addEventListener('pageshow', onPageShow);
});

onBeforeUnmount(() => {
	window.removeEventListener('pageshow', onPageShow);
});

// 上传作品后刷新「我的作品」
watch(() => ui.lastUploaded, loadMyWorks);
</script>

<style scoped src="@/assets/styles/my.css"></style>

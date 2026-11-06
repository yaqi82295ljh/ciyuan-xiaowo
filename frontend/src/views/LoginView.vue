<template>
	<main class="page">
		<!-- 左：品牌区 -->
		<section class="brand">
			<router-link to="/" title="回到首页">
				<img class="brand-logo" src="/resources/logo.png" alt="次元小窝"/>
			</router-link>

			<div class="brand-slogan">
				<h2>{{ isRegister ? '欢迎加入 ~' : '欢迎回来 ~' }}</h2>
				<p>登录之后，收藏、点赞和上传记录才会跟着你的账号走，<br/>换台设备也还是你的小窝。</p>
			</div>

			<ul class="brand-points">
				<li>收藏与点赞云端同步</li>
				<li>关注喜欢的画师更新</li>
				<li>上传自己的作品参展</li>
			</ul>
		</section>

		<!-- 右：表单区 -->
		<section class="form-side">
			<div class="card">
				<router-link class="back-home" to="/">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
						stroke-linecap="round" stroke-linejoin="round">
						<path d="m14 6-6 6 6 6"/>
					</svg>
					返回首页
				</router-link>

				<header class="card-head">
					<p class="card-eyebrow">{{ isRegister ? 'SIGN UP' : 'SIGN IN' }}</p>
					<h1 class="card-title">{{ isRegister ? '加入次元小窝' : '登录次元小窝' }}</h1>
					<p class="card-sub">
						{{ isRegister ? '取个昵称，就能开始收藏和上传啦。' : '用你的账号继续逛，收藏和点赞都会记着。' }}
					</p>
				</header>

				<form class="form" novalidate @submit.prevent="submit">
					<div v-if="alertMsg" class="form-alert" :class="{ 'form-alert--ok': alertOk }">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
							stroke-linecap="round" stroke-linejoin="round">
							<circle cx="12" cy="12" r="9"/>
							<path d="M12 7.5v5.5M12 16.4v.1"/>
						</svg>
						<span>{{ alertMsg }}</span>
					</div>

					<div v-if="isRegister" class="field">
						<label class="field-label" for="nickname">昵称</label>
						<div class="field-wrap">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
								stroke-linecap="round" stroke-linejoin="round">
								<path d="M4 20c1.6-3.4 4.4-5 8-5s6.4 1.6 8 5"/>
								<circle cx="12" cy="8" r="3.4"/>
							</svg>
							<input id="nickname" v-model="nickname" class="field-input" type="text"
								placeholder="现在可以不填，登录后随时可改" maxlength="50"/>
						</div>
					</div>

					<div class="field">
						<label class="field-label" for="username">账号</label>
						<div class="field-wrap">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
								stroke-linecap="round" stroke-linejoin="round">
								<circle cx="12" cy="8.2" r="3.5"/>
								<path d="M5.5 20a6.5 6.5 0 0 1 13 0"/>
							</svg>
							<input id="username" v-model="username" class="field-input" type="text"
								placeholder="用户名 / 邮箱" autocomplete="username" required/>
						</div>
					</div>

					<div class="field">
						<label class="field-label" for="password">密码</label>
						<div class="field-wrap">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
								stroke-linecap="round" stroke-linejoin="round">
								<rect x="4.5" y="10" width="15" height="10" rx="2.5"/>
								<path d="M8 10V7.5a4 4 0 0 1 8 0V10"/>
								<path d="M12 14v2.5"/>
							</svg>
							<input
								id="password"
								v-model="password"
								class="field-input"
								:type="showPw ? 'text' : 'password'"
								:placeholder="isRegister ? '设置密码（至少 6 位）' : '请输入密码'"
								:autocomplete="isRegister ? 'new-password' : 'current-password'"
								required
							/>
							<button
								class="field-eye"
								type="button"
								:aria-label="showPw ? '隐藏密码' : '显示密码'"
								:title="showPw ? '隐藏密码' : '显示密码'"
								@click="showPw = !showPw"
							>
								<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
									stroke-linecap="round" stroke-linejoin="round">
									<path d="M2.5 12S6 5.8 12 5.8 21.5 12 21.5 12 18 18.2 12 18.2 2.5 12 2.5 12z"/>
									<circle cx="12" cy="12" r="3.1"/>
								</svg>
							</button>
						</div>
					</div>

					<div class="form-row">
						<label class="check">
							<input class="check-input" type="checkbox" name="remember" value="1" checked/>
							<span class="check-box"></span>
							记住我
						</label>
					</div>

					<button class="btn btn--primary" :class="{ 'is-loading': submitting }" type="submit">
						<span class="btn-spinner" aria-hidden="true"></span>
						<span>{{ isRegister ? '注册' : '登录' }}</span>
					</button>
				</form>

				<p class="card-foot">
					<span>{{ isRegister ? '已经有账号了？' : '还没有账号？' }}</span>
					<router-link :to="isRegister ? '/login' : '/login?mode=register'">
						{{ isRegister ? '去登录' : '立即注册' }}
					</router-link>
				</p>
			</div>
		</section>
	</main>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { api } from '@/utils/api';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const username = ref('');
const password = ref('');
const nickname = ref('');
const showPw = ref(false);
const submitting = ref(false);
const alertMsg = ref('');
const alertOk = ref(false);

const isRegister = computed(() => route.query.mode === 'register');

watch(isRegister, () => {
	alertMsg.value = '';
	alertOk.value = false;
	document.title = (isRegister.value ? '注册' : '登录') + ' · 次元小窝';
}, { immediate: true });

// 注册页带 ?registered=1 回来时提示一次
if (route.query.registered === '1') {
	alertMsg.value = '注册成功，用刚才的账号登录吧~';
	alertOk.value = true;
}

async function submit() {
	alertMsg.value = '';
	alertOk.value = false;

	if (!username.value.trim()) { alertMsg.value = '请先填写账号。'; return; }
	if (!password.value) { alertMsg.value = '请先填写密码。'; return; }
	if (isRegister.value && password.value.length < 6) { alertMsg.value = '密码至少 6 位。'; return; }

	const data = new URLSearchParams();
	data.append('username', username.value.trim());
	data.append('password', password.value);
	if (isRegister.value) data.append('nickname', nickname.value.trim());

	submitting.value = true;
	const startAt = Date.now();
	try {
		if (isRegister.value) {
			await api.postForm('/register', data);
			router.push({ path: '/login', query: { registered: '1' } });
			// push 不刷新页面，提示在这里直接给出
			alertMsg.value = '注册成功，用刚才的账号登录吧~';
			alertOk.value = true;
			password.value = '';
		} else {
			await api.postForm('/login', data);
			await auth.fetchMe(true);
			const redirect = String(route.query.redirect || '/');
			router.push(redirect.startsWith('/') ? redirect : '/');
		}
	} catch (err) {
		alertMsg.value = err.code === 0
			? '连不上服务器，稍后再试试~'
			: err.message || (isRegister.value ? '注册失败，请稍后再试。' : '账号或密码不正确，请重试。');
	} finally {
		// 至少转 400ms 的圈，避免闪一下看不清状态
		const wait = Math.max(0, 400 - (Date.now() - startAt));
		setTimeout(() => { submitting.value = false; }, wait);
	}
}
</script>

<style scoped src="@/assets/styles/login.css"></style>

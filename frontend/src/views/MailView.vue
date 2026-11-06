<template>
	<section class="mailbox" :class="{ 'is-detail': !!activeMail }" aria-label="私信箱">
		<!-- 左栏：会话列表 -->
		<div class="mb-list">
			<div class="mb-list-head">
				<h3 class="mb-list-title">收件箱</h3>
				<span class="mb-list-count">{{ mails.length }} 条 · {{ unreadCount }} 未读</span>
			</div>

			<div class="mb-items" role="listbox" aria-label="会话列表">
				<div v-if="!mails.length" class="mb-empty">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
						stroke-linecap="round" stroke-linejoin="round">
						<rect x="3.5" y="5.5" width="17" height="13" rx="2.5"/>
						<path d="m4.5 7.5 7.5 5.5 7.5-5.5"/>
					</svg>
					<p>还没有收到私信</p>
					<span>有人回复或给你留言时，会话会出现在这里</span>
				</div>
				<button
					v-for="m in mails"
					:key="m.id"
					class="mb-item"
					:class="{ 'is-unread': m.unread, 'is-active': activeMail && activeMail.id === m.id }"
					type="button"
					role="option"
					:aria-selected="activeMail && activeMail.id === m.id"
					@click="selectMail(m)"
				>
					<span class="mb-avatar" aria-hidden="true">{{ m.initial }}</span>
					<span class="mb-item-body">
						<span class="mb-item-head">
							<span class="mb-item-name">{{ m.name }}</span>
							<time class="mb-item-time">{{ m.time }}</time>
						</span>
						<span class="mb-item-preview">{{ m.preview }}</span>
					</span>
				</button>
			</div>
		</div>

		<!-- 右栏：正文 -->
		<div class="mb-view">
			<header class="mb-view-head">
				<button class="mb-back" type="button" aria-label="返回列表" @click="activeMail = null">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
						stroke-linecap="round" stroke-linejoin="round">
						<path d="m14 6-6 6 6 6"/>
					</svg>
					列表
				</button>

				<span class="mb-avatar" aria-hidden="true">{{ activeMail ? activeMail.initial : '—' }}</span>
				<div class="mb-view-info">
					<h3 class="mb-view-name">{{ activeMail ? '@' + activeMail.name : '选择一条私信' }}</h3>
					<p class="mb-view-meta">
						{{ activeMail ? '共 ' + activeMail.messages.length + ' 条消息' : '点左边列表里的任意一条，正文会显示在这里。' }}
					</p>
				</div>
				<time class="mb-view-time">{{ activeMail ? activeMail.time : '' }}</time>
			</header>

			<div ref="bodyRef" class="mb-view-body">
				<p v-if="!activeMail" class="mb-view-empty">还没有选中任何私信。</p>
				<template v-else>
					<template v-for="msg in activeMail.messages" :key="msg.id">
						<div class="mb-bubble" :class="msg.direction === 'out' ? 'mb-bubble--out' : 'mb-bubble--in'">
							{{ msg.text }}
							<time class="mb-bubble-time">{{ msg.time }}</time>
						</div>
					</template>
				</template>
			</div>

			<!-- 没选中会话时不显示回复框，避免「能打字却发不出去」的假交互 -->
			<form v-if="activeMail" class="mb-reply" @submit.prevent="sendReply">
				<textarea
					ref="replyRef"
					v-model="replyText"
					class="mb-reply-input"
					rows="1"
					placeholder="回复…"
					@input="growReply"
					@keydown.enter.exact.prevent="sendReply"
				></textarea>

				<button
					class="mb-reply-clear"
					type="button"
					aria-label="清空输入框"
					title="清空输入框"
					:disabled="!replyText"
					@click="clearReply"
				>
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
						stroke-linecap="round" stroke-linejoin="round">
						<circle cx="12" cy="12" r="8.5"/>
						<path d="m9.2 9.2 5.6 5.6M14.8 9.2l-5.6 5.6"/>
					</svg>
				</button>

				<button class="mb-reply-send" type="submit">发送</button>
			</form>
		</div>
	</section>
</template>

<script setup>
import { computed, nextTick, ref } from 'vue';

// 私信后端接口还没做（MailContr 是空壳），这里先保留完整的会话界面与空态；
// 路由上已经挂了登录守卫，未登录进不来。
const mails = ref([]);
const activeMail = ref(null);
const replyText = ref('');
const replyRef = ref(null);
const bodyRef = ref(null);

const unreadCount = computed(() => mails.value.filter((m) => m.unread).length);

function selectMail(m) {
	m.unread = false;
	activeMail.value = m;
}

function growReply() {
	const el = replyRef.value;
	if (!el) return;
	el.style.height = 'auto';
	el.style.height = Math.min(el.scrollHeight, 220) + 'px';
}

function clearReply() {
	replyText.value = '';
	growReply();
	replyRef.value && replyRef.value.focus();
}

async function sendReply() {
	const text = replyText.value.trim();
	if (!text || !activeMail.value) return;

	const d = new Date();
	const pad = (n) => String(n).padStart(2, '0');
	const now = pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes());

	activeMail.value.messages.push({ id: Date.now(), direction: 'out', text, time: now });
	replyText.value = '';
	growReply();
	await nextTick();
	bodyRef.value && (bodyRef.value.scrollTop = bodyRef.value.scrollHeight);
}
</script>

<style scoped src="@/assets/styles/mail.css"></style>

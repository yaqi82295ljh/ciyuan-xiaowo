<template>
	<div class="ad-layout">
		<aside class="ad-side">
			<div class="ad-brand">
				<router-link to="/" title="回到前台">
					<img src="/resources/logo.png" alt="次元小窝"/>
				</router-link>
				<span class="ad-brand-tag">管理后台</span>
			</div>

			<nav class="ad-nav">
				<p class="ad-nav-title">总览</p>

				<a
					v-for="v in VIEWS" :key="v.name"
					class="ad-nav-item"
					:class="{ 'is-active': view === v.name }"
					href="#"
					@click.prevent="view = v.name"
				>
					<span v-html="v.icon"></span>
					{{ v.title }}
				</a>
			</nav>

			<div class="ad-side-foot">
				<router-link class="ad-nav-item" to="/">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
						stroke-linecap="round" stroke-linejoin="round">
						<path d="M10 5.5H6A1.5 1.5 0 0 0 4.5 7v10A1.5 1.5 0 0 0 6 18.5h4"/>
						<path d="M9.5 12h10M15.5 8l4 4-4 4"/>
					</svg>
					返回前台
				</router-link>
			</div>
		</aside>

		<main class="ad-main">
			<header class="ad-top">
				<h1 class="ad-top-title">{{ currentView.title }}</h1>
				<div class="ad-top-right">
					<span class="ad-who">站长 · <b>{{ auth.user.username }}</b></span>
					<button class="ad-btn ad-btn--ghost ad-btn--sm" type="button" @click="logout">退出</button>
				</div>
			</header>

			<div class="ad-body">
				<!-- 系统统计 -->
				<section v-if="view === 'dashboard'" class="view">
					<div class="ad-stats">
						<div class="ad-stat">
							<span class="ad-stat-label">总图片数</span>
							<b class="ad-stat-value">{{ stats.images }}</b>
							<span class="ad-stat-sub">今日 +{{ stats.imagesToday }}</span>
						</div>
						<div class="ad-stat">
							<span class="ad-stat-label">总用户数</span>
							<b class="ad-stat-value">0</b>
							<span class="ad-stat-sub">接口未上线</span>
						</div>
						<div class="ad-stat">
							<span class="ad-stat-label">今日新增评论</span>
							<b class="ad-stat-value">0</b>
							<span class="ad-stat-sub">接口未上线</span>
						</div>
						<div class="ad-stat">
							<span class="ad-stat-label">待处理举报</span>
							<b class="ad-stat-value">0</b>
							<span class="ad-stat-sub">接口未上线</span>
						</div>
					</div>

					<div class="ad-grid-2">
						<section class="ad-card">
							<div class="ad-card-head">
								<h2 class="ad-card-title">热门标签</h2>
								<span class="ad-card-note">按被引用次数</span>
							</div>
							<div class="ad-card-body">
								<ol v-if="tags.length" class="ad-rank">
									<li v-for="(t, i) in tags" :key="t.name">
										<span class="ad-rank-no">{{ i + 1 }}</span>
										<span class="ad-rank-name">#{{ t.name }}</span>
										<span class="ad-rank-count">{{ t.count }}</span>
									</li>
								</ol>
								<div v-else class="ad-empty"><p>还没有数据</p></div>
							</div>
						</section>

						<section class="ad-card">
							<div class="ad-card-head">
								<h2 class="ad-card-title">最近上传</h2>
								<span class="ad-card-note">最新 5 张</span>
							</div>
							<div class="ad-card-body">
								<div class="ad-table-wrap">
									<table class="ad-table">
										<thead>
											<tr><th>图</th><th>标题</th><th>上传者</th><th class="num">时间</th></tr>
										</thead>
										<tbody>
											<tr v-for="it in recent" :key="it.id">
												<td><img class="ad-thumb" :src="it.thumbUrl || it.url" :alt="it.title"/></td>
												<td>{{ it.title }}</td>
												<td>用户 {{ it.userId }}</td>
												<td class="num">{{ fmtDate(it.createdAt) }}</td>
											</tr>
											<tr v-if="!recent.length">
												<td colspan="4" style="color: var(--ink-400)">还没有作品</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</section>
					</div>
				</section>

				<!-- 其余模块：接口未上线，先保留规划说明 -->
				<section v-for="v in planViews" v-else-if="view === v.name" :key="v.name" class="view">
					<section class="ad-card">
						<div class="ad-card-head">
							<h2 class="ad-card-title">{{ v.title }}</h2>
							<span class="ad-card-note">接口未上线</span>
						</div>
						<div class="ad-empty">
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6"
								stroke-linecap="round" stroke-linejoin="round" v-html="v.icon"></svg>
							<p>{{ v.plan }}</p>
						</div>
					</section>
				</section>
			</div>
		</main>
	</div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useTagStore } from '@/stores/tags';
import { api } from '@/utils/api';
import { fmtDate } from '@/utils/format';

const router = useRouter();
const auth = useAuthStore();
const tagStore = useTagStore();

const view = ref('dashboard');
const stats = ref({ images: 0, imagesToday: 0 });
const recent = ref([]);

const svg = (paths, size = 17) =>
	'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" ' +
	'stroke-linecap="round" stroke-linejoin="round" style="width:' + size + 'px;height:' + size + 'px">' +
	paths + '</svg>';

const ICONS = {
	dashboard: svg('<path d="M4 19.5h16"/><path d="M7.5 19.5V11M12 19.5V5.5M16.5 19.5v-5.5"/>'),
	zones: svg('<rect x="3.5" y="4.5" width="7" height="7" rx="2"/><rect x="13.5" y="4.5" width="7" height="7" rx="2"/><rect x="3.5" y="14.5" width="7" height="5" rx="2"/><rect x="13.5" y="14.5" width="7" height="5" rx="2"/>'),
	users: svg('<circle cx="9" cy="8.4" r="3.4"/><path d="M3 19.5a6 6 0 0 1 12 0"/><path d="M16 6.2a3.2 3.2 0 0 1 0 6M17.6 19.5a5.4 5.4 0 0 0-1.2-3.4"/>'),
	content: svg('<rect x="3.5" y="5" width="17" height="14" rx="2.5"/><circle cx="9" cy="10" r="1.6"/><path d="m5 17.5 4.5-4.2 3.4 3 3-2.6 3.6 3.3"/>'),
	appearance: svg('<path d="M12 3.5a8.5 8.5 0 1 0 0 17h1.4a2 2 0 0 0 0-4h-.9a1.6 1.6 0 0 1 0-3.2h3.1a4 4 0 0 0 4-4A5.8 5.8 0 0 0 14 3.5z"/>'),
	notices: svg('<path d="M4 10v3.6a1.2 1.2 0 0 0 1.2 1.2H7l4 3.6V5.6L7 9.2H5.2A1.2 1.2 0 0 0 4 10.4z"/><path d="M15 8.6a4.2 4.2 0 0 1 0 6.8"/>')
};

const VIEWS = [
	{ name: 'dashboard', title: '系统统计', icon: ICONS.dashboard },
	{ name: 'zones', title: '分区管理', icon: ICONS.zones },
	{ name: 'users', title: '用户管理', icon: ICONS.users },
	{ name: 'content', title: '内容管理', icon: ICONS.content },
	{ name: 'appearance', title: '站点外观', icon: ICONS.appearance },
	{ name: 'notices', title: '公告系统', icon: ICONS.notices }
];

const planViews = [
	{ name: 'zones', title: '分区管理', icon: ICONS.zones, plan: '计划：分区列表（增 / 改 / 删，删前必须先清空或转移里面的图）+ 每个分区的标签管理' },
	{ name: 'users', title: '用户管理', icon: ICONS.users, plan: '计划：用户列表（按角色筛选）+ 封禁 / 解禁 + 重置密码' },
	{ name: 'content', title: '内容管理', icon: ICONS.content, plan: '计划：图片管理（删违规图 / 批量操作）· 评论管理（删违规评论）· 举报记录（查看 + 处理）' },
	{ name: 'appearance', title: '站点外观', icon: ICONS.appearance, plan: '计划：背景图（透明度 / 模糊、实时预览）· 全局主题主色选择器 · Logo 上传' },
	{ name: 'notices', title: '公告系统', icon: ICONS.notices, plan: '计划：多条公告（增删改）+ 启用 / 置顶开关 + 前台首次进入弹窗（仅一次）' }
];

const currentView = computed(() => VIEWS.find((v) => v.name === view.value) || VIEWS[0]);

async function logout() {
	await auth.logout();
	router.push('/');
}

onMounted(async () => {
	document.title = '后台管理 · 次元小窝';
	tagStore.fetchTags();
	try {
		const data = await api.get('/img?page=1&size=50');
		const list = data.list || [];
		stats.value.images = data.total || 0;
		const today = fmtDate(new Date().toISOString());
		stats.value.imagesToday = list.filter((it) => fmtDate(it.createdAt) === today).length;
		recent.value = list.slice(0, 5);
	} catch (err) {
		console.warn('[admin] 统计没拉下来：', err.message);
	}
});
</script>

<style scoped src="@/assets/styles/admin.css"></style>

import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const routes = [
	{ path: '/', name: 'home', component: () => import('@/views/HomeView.vue'), meta: { title: '发现首页' } },
	{ path: '/top', name: 'top', component: () => import('@/views/TopView.vue'), meta: { title: '热门排行' } },
	// 两个分区复用同一个视图，靠 meta.cat 区分
	{ path: '/wall', name: 'wall', component: () => import('@/views/GalleryView.vue'), meta: { title: '风景壁纸', cat: 1 } },
	{ path: '/char', name: 'char', component: () => import('@/views/GalleryView.vue'), meta: { title: '人物立绘', cat: 2 } },
	{ path: '/notice', name: 'notice', component: () => import('@/views/NoticeView.vue'), meta: { title: '公告板' } },
	{
		path: '/image/:id(\\d+)',
		name: 'image',
		component: () => import('@/views/ImageDetailView.vue'),
		meta: { title: '图片详情', plain: true }
	},
	{ path: '/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { title: '登录', plain: true } },
	{
		path: '/mail',
		name: 'mail',
		component: () => import('@/views/MailView.vue'),
		meta: { title: '私信箱', requiresAuth: true }
	},
	{
		path: '/my',
		name: 'my',
		component: () => import('@/views/MyView.vue'),
		meta: { title: '个人中心', requiresAuth: true }
	},
	{
		path: '/admin',
		name: 'admin',
		component: () => import('@/views/admin/AdminView.vue'),
		meta: { title: '管理后台', requiresAuth: true, plain: true }
	},
	{ path: '/:pathMatch(.*)*', redirect: '/' }
];

const router = createRouter({
	history: createWebHistory(),
	routes,
	scrollBehavior(to, from, savedPosition) {
		// 前进后退保留滚动位置，普通导航回顶部；带 hash 的交给页面内的 tab 逻辑
		if (savedPosition) return savedPosition;
		if (to.hash) return {};
		return { top: 0 };
	}
});

// 登录守卫：私信箱 / 个人中心 / 后台都要先登录。
// 未登录用 replace 跳登录页，不把「被拦下的页面」留在历史里，
// 避免登录后点返回又跳回空壳页。
router.beforeEach(async (to) => {
	const auth = useAuthStore();
	if (to.meta.requiresAuth) {
		const user = await auth.fetchMe();
		if (!user) {
			return { path: '/login', query: { redirect: to.fullPath }, replace: true };
		}
	}
	document.title = to.meta.title ? to.meta.title + ' · 次元小窝' : '次元小窝';
	return true;
});

export default router;

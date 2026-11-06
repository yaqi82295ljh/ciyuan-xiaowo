/** 日期与时间、数字的展示格式化工具 */

/** '2026-11-06T12:00:00' → '2026-11-06' */
export function fmtDate(s) {
	return s ? String(s).slice(0, 10) : '—';
}

/** '2026-11-06T12:00:00' → '2026-11-06 12:00' */
export function fmtTime(s) {
	return s ? String(s).replace('T', ' ').slice(0, 16) : '—';
}

/** 数字缩写：12345 → 1.2万，1500 → 1.5k */
export function fmtNum(n) {
	if (n >= 10000) return (n / 10000).toFixed(1).replace(/\.0$/, '') + '万';
	if (n >= 1000) return (n / 1000).toFixed(1).replace(/\.0$/, '') + 'k';
	return String(n);
}

/** 字节数 → 人类可读大小 */
export function humanSize(bytes) {
	if (bytes >= 1024 * 1024) return (bytes / 1024 / 1024).toFixed(1) + ' MB';
	if (bytes < 1024) return bytes + ' B';
	return Math.round(bytes / 1024) + ' KB';
}

/** 把逗号 / 空格 / 顿号分隔的标签串拆成去重数组 */
export function splitTags(raw) {
	const out = [];
	String(raw || '').split(/[,，、\s]+/).forEach((t) => {
		t = t.trim();
		if (t && !out.includes(t)) out.push(t);
	});
	return out;
}

/** 分区 id ↔ 标识 ↔ 中文名 */
export const CAT = {
	1: { key: 'wall', name: '风景壁纸' },
	2: { key: 'char', name: '人物立绘' }
};

export function catName(categoryId) {
	return (CAT[categoryId] || {}).name || '其他';
}

/** 把后端 ImageVO 规整成卡片用的字段，顺便兜住 null 数组 / null 计数 */
export function toWork(it) {
	return {
		id: it.id,
		title: it.title || '无标题',
		categoryId: it.categoryId,
		tags: Array.isArray(it.tags) ? it.tags : [],
		ai: !!it.isAi,
		likes: it.likeCount || 0,
		favorites: it.favCount || 0,
		views: it.views || 0,
		createdAt: fmtDate(it.createdAt),
		// 列表卡片优先用缩略图：原图动辄 3~4 MB，手机上首屏几十 MB 直接卡死
		img: it.thumbUrl || it.url,
		// 原图尺寸，用来给卡片预留宽高比，避免图片加载前后跳动
		width: it.width || 0,
		height: it.height || 0
	};
}

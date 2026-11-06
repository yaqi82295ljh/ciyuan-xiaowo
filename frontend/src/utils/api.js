/**
 * 统一 API 请求封装。
 * 后端 Result 结构：{ code, message, data }，code === 200 为成功。
 * 所有请求带 credentials，登录态走 JSESSIONID Cookie（开发环境由 Vite 代理同源转发）。
 */

/** 带业务码的错误，code 为后端 Result.code（如 401 未登录） */
export class ApiError extends Error {
	constructor(message, code) {
		super(message);
		this.name = 'ApiError';
		this.code = code;
	}
}

/** 未登录错误统一以「未登录」开头，方便调用方识别 */
function toError(json) {
	if (json.code === 401) return new ApiError('未登录或登录已过期', 401);
	return new ApiError(json.message || '操作失败', json.code);
}

/**
 * 发请求，成功返回 data，失败抛 ApiError。
 * @param {string} path 接口路径（不含 /api 前缀）
 * @param {object} [options]
 * @param {string} [options.method] HTTP 方法
 * @param {object} [options.body] JSON 请求体（自动加 Content-Type）
 * @param {URLSearchParams|FormData} [options.form] 表单 / multipart 请求体
 */
export async function request(path, { method = 'GET', body, form } = {}) {
	const opts = { method, credentials: 'include' };
	if (form) {
		opts.body = form;
	} else if (body !== undefined) {
		opts.headers = { 'Content-Type': 'application/json' };
		opts.body = JSON.stringify(body);
	}

	let res;
	try {
		res = await fetch('/api' + path, opts);
	} catch {
		throw new ApiError('连不上服务器，稍后再试试~', 0);
	}

	if (res.status === 413) throw new ApiError('文件太大了，服务器拒收。', 413);

	const json = await res.json().catch(() => null);
	if (!json) throw new ApiError('HTTP ' + res.status, res.status);
	if (json.code !== 200) throw toError(json);
	return json.data;
}

export const api = {
	get: (path) => request(path),
	post: (path, body) => request(path, { method: 'POST', body }),
	postForm: (path, form) => request(path, { method: 'POST', form }),
	del: (path) => request(path, { method: 'DELETE' })
};

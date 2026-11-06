<template>
	<div v-if="open" class="cropper">
		<div ref="scrollerRef" class="cropper__scroller" @wheel.prevent="onWheel">
			<div
				ref="stageRef"
				class="cropper__stage"
				@pointerdown.prevent="onPanStart"
				@pointermove="onPointerMove"
				@pointerup="endDrag"
				@pointercancel="endDrag"
			>
				<img
					ref="imgRef"
					class="cropper__img"
					:src="imgSrc"
					:style="{ transform: 'translate(' + panX + 'px,' + panY + 'px) scale(' + scale + ')' }"
					alt="待裁剪的图片"
					@load="onImgLoad"
				/>

				<span class="cropmask" :style="mask.top"></span>
				<span class="cropmask" :style="mask.bottom"></span>
				<span class="cropmask" :style="mask.left"></span>
				<span class="cropmask" :style="mask.right"></span>

				<!-- 正方形选框 -->
				<div
					ref="boxRef"
					class="cropper__box"
					:style="boxStyle"
					@pointerdown.prevent="onBoxStart"
				>
					<span class="cropgrid cropgrid--v1"></span>
					<span class="cropgrid cropgrid--v2"></span>
					<span class="cropgrid cropgrid--h1"></span>
					<span class="cropgrid cropgrid--h2"></span>

					<span class="crophandle crophandle--tl"></span>
					<span class="crophandle crophandle--tr"></span>
					<span class="crophandle crophandle--bl"></span>
					<span class="crophandle crophandle--br"></span>

					<span class="crophit crophit--tl" data-corner="tl"></span>
					<span class="crophit crophit--tr" data-corner="tr"></span>
					<span class="crophit crophit--bl" data-corner="bl"></span>
					<span class="crophit crophit--br" data-corner="br"></span>
				</div>
			</div>

			<!-- 手机上没有滚轮，靠滑块缩放 -->
			<div class="cropper__tools">
				<span class="cropper__hint">拖动图片或选框调整，滑块缩放</span>
				<input
					class="cropper__zoom"
					type="range"
					min="60"
					max="500"
					step="5"
					:value="Math.round(st.scale * 100)"
					aria-label="缩放图片"
					@input="onZoomInput"
				/>
			</div>
		</div>
	</div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue';

const OUT_SIZE = 200;        // 导出的正方形边长
const BOX_MIN = 40;
const BOX_MAX = 400;
const SCALE_MIN = 0.6;
const SCALE_MAX = 5;
const MIN_VISIBLE = 40;      // 平移时至少留出多少像素可见

const emit = defineEmits(['cropped', 'cancel']);

const open = ref(false);
const imgSrc = ref('');
const scrollerRef = ref(null);
const stageRef = ref(null);
const imgRef = ref(null);

// 裁剪状态。disp* 是图片缩放平移后的实际显示位置，由 applyScale 计算
const st = reactive({
	naturalW: 0,
	naturalH: 0,
	baseW: 0,
	baseH: 0,          // 图片未变换时的显示尺寸（只量一次）
	baseCx: 0,
	baseCy: 0,          // 图片零位移中心（相对容器）
	scale: 1,
	panX: 0,
	panY: 0,
	dispW: 0,
	dispH: 0,
	dispX: 0,
	dispY: 0,
	boxSize: 140,
	boxX: 0,
	boxY: 0,
	mode: null,        // null | 'move' | 'resize' | 'pan'
	startX: 0,
	startY: 0,
	startBoxX: 0,
	startBoxY: 0,
	startBoxSize: 0,
	startPanX: 0,
	startPanY: 0,
	corner: null
});

let imgEl = null;      // 内存里的原始 Image 对象（导出 canvas 用）

const boxStyle = computed(() => ({
	width: st.boxSize + 'px',
	height: st.boxSize + 'px',
	left: st.boxX + 'px',
	top: st.boxY + 'px'
}));

// 四块遮罩盖住选框外的图片区域
const mask = computed(() => {
	const x = st.dispX, y = st.dispY, w = st.dispW, h = st.dispH;
	const bx = st.boxX, by = st.boxY, bs = st.boxSize;
	return {
		top: { left: x + 'px', top: y + 'px', width: w + 'px', height: Math.max(0, by - y) + 'px' },
		bottom: { left: x + 'px', top: (by + bs) + 'px', width: w + 'px', height: Math.max(0, y + h - (by + bs)) + 'px' },
		left: { left: x + 'px', top: by + 'px', width: Math.max(0, bx - x) + 'px', height: bs + 'px' },
		right: { left: (bx + bs) + 'px', top: by + 'px', width: Math.max(0, x + w - (bx + bs)) + 'px', height: bs + 'px' }
	};
});

function applyScale() {
	const s = st.scale;
	st.dispW = st.baseW * s;
	st.dispH = st.baseH * s;
	const cx = st.baseCx + st.panX;
	const cy = st.baseCy + st.panY;
	st.dispX = cx - st.dispW / 2;
	st.dispY = cy - st.dispH / 2;
}

function measureBase() {
	imgRef.value.style.transform = '';
	const stage = stageRef.value.getBoundingClientRect();
	const img = imgRef.value.getBoundingClientRect();
	st.baseW = img.width;
	st.baseH = img.height;
	st.baseCx = img.left - stage.left + img.width / 2;
	st.baseCy = img.top - stage.top + img.height / 2;
	st.scale = 1;
	st.panX = 0;
	st.panY = 0;
	applyScale();
}

function clampBox() {
	const maxSize = Math.min(st.dispW, st.dispH);
	if (st.boxSize > maxSize) st.boxSize = Math.max(1, maxSize);

	const minX = st.dispX;
	const minY = st.dispY;
	const maxX = st.dispX + st.dispW - st.boxSize;
	const maxY = st.dispY + st.dispH - st.boxSize;
	if (st.boxX < minX) st.boxX = minX;
	if (st.boxX > maxX) st.boxX = maxX;
	if (st.boxY < minY) st.boxY = minY;
	if (st.boxY > maxY) st.boxY = maxY;
}

function resetBox() {
	measureBase();
	const base = Math.min(st.dispW, st.dispH) * 0.5;
	st.boxSize = Math.max(BOX_MIN, Math.min(BOX_MAX, base));
	st.boxX = st.dispX + (st.dispW - st.boxSize) / 2;
	st.boxY = st.dispY + (st.dispH - st.boxSize) / 2;
	clampBox();
}

function openCropper(img, src) {
	imgEl = img;
	st.naturalW = img.naturalWidth;
	st.naturalH = img.naturalHeight;
	imgSrc.value = src;
	open.value = true;
	// 等浏览器完成布局再量尺寸
	requestAnimationFrame(() => {
		if (imgRef.value.complete) requestAnimationFrame(resetBox);
		// 没加载完的话 onImgLoad 里会调
	});
}

function closeCropper() {
	open.value = false;
	imgEl = null;
	st.mode = null;
	st.scale = 1;
	st.panX = 0;
	st.panY = 0;
}

function onImgLoad() {
	if (open.value) resetBox();
}

function stagePoint(e) {
	const r = scrollerRef.value.getBoundingClientRect();
	return { x: e.clientX - r.left, y: e.clientY - r.top };
}

/** 把指针捕获到舞台：手指滑出元素边界后仍能继续收到 move/up */
function capturePointer(e) {
	try {
		stageRef.value.setPointerCapture(e.pointerId);
	} catch {
		/* 某些旧浏览器不支持，忽略 */
	}
}

function onBoxStart(e) {
	if (!imgEl || (e.button != null && e.button !== 0)) return;
	e.stopPropagation();
	capturePointer(e);
	const p = stagePoint(e);
	const hit = e.target.closest ? e.target.closest('.crophit') : null;
	if (hit) {
		st.mode = 'resize';
		st.corner = hit.dataset.corner;
		st.startBoxSize = st.boxSize;
	} else {
		st.mode = 'move';
	}
	st.startX = p.x;
	st.startY = p.y;
	st.startBoxX = st.boxX;
	st.startBoxY = st.boxY;
}

function onPanStart(e) {
	if (!imgEl || (e.button != null && e.button !== 0)) return;
	capturePointer(e);
	const p = stagePoint(e);
	st.mode = 'pan';
	st.startX = p.x;
	st.startY = p.y;
	st.startPanX = st.panX;
	st.startPanY = st.panY;
}

function resizeByCorner(p) {
	const dx = p.x - st.startX;
	const dy = p.y - st.startY;

	let d;
	if (st.corner === 'br') d = Math.max(dx, dy);
	else if (st.corner === 'tl') d = Math.max(-dx, -dy);
	else if (st.corner === 'tr') d = Math.max(dx, -dy);
	else d = Math.max(-dx, dy);

	let size = st.startBoxSize + d;
	if (size < BOX_MIN) size = BOX_MIN;
	const cap = Math.min(BOX_MAX, Math.min(st.dispW, st.dispH));
	if (size > cap) size = cap;

	const s0 = st.startBoxSize;
	if (st.corner === 'tl') { st.boxX = st.startBoxX + s0 - size; st.boxY = st.startBoxY + s0 - size; }
	else if (st.corner === 'tr') { st.boxX = st.startBoxX; st.boxY = st.startBoxY + s0 - size; }
	else if (st.corner === 'bl') { st.boxX = st.startBoxX + s0 - size; st.boxY = st.startBoxY; }
	else { st.boxX = st.startBoxX; st.boxY = st.startBoxY; }

	st.boxSize = size;
	clampBox();
}

function clampPan() {
	const sc = scrollerRef.value;
	const scW = sc.clientWidth;
	const scH = sc.clientHeight;

	if (st.dispW <= scW) st.panX = 0;
	else {
		const minPanX = MIN_VISIBLE - st.baseCx - st.dispW / 2;
		const maxPanX = scW - MIN_VISIBLE - st.baseCx + st.dispW / 2;
		if (st.panX < minPanX) st.panX = minPanX;
		if (st.panX > maxPanX) st.panX = maxPanX;
	}

	if (st.dispH <= scH) st.panY = 0;
	else {
		const minPanY = MIN_VISIBLE - st.baseCy - st.dispH / 2;
		const maxPanY = scH - MIN_VISIBLE - st.baseCy + st.dispH / 2;
		if (st.panY < minPanY) st.panY = minPanY;
		if (st.panY > maxPanY) st.panY = maxPanY;
	}
	applyScale();
}

function onPointerMove(e) {
	if (!imgEl || !st.mode) return;
	const p = stagePoint(e);

	if (st.mode === 'move') {
		st.boxX = st.startBoxX + (p.x - st.startX);
		st.boxY = st.startBoxY + (p.y - st.startY);
		clampBox();
		return;
	}

	if (st.mode === 'resize') {
		resizeByCorner(p);
		return;
	}

	st.panX = st.startPanX + (p.x - st.startX);
	st.panY = st.startPanY + (p.y - st.startY);
	applyScale();
	clampPan();
	clampBox();
}

function endDrag() {
	st.mode = null;
	st.corner = null;
}

function zoomAt(targetScale, anchor) {
	targetScale = Math.max(SCALE_MIN, Math.min(SCALE_MAX, targetScale));

	// 以鼠标位置为锚点缩放
	const ax = (anchor.x - st.dispX) / st.dispW;
	const ay = (anchor.y - st.dispY) / st.dispH;
	st.scale = targetScale;
	applyScale();
	const targetCx = anchor.x + (0.5 - ax) * st.dispW;
	const targetCy = anchor.y + (0.5 - ay) * st.dispH;
	st.panX = targetCx - st.baseCx;
	st.panY = targetCy - st.baseCy;
	applyScale();

	clampPan();
	clampBox();
}

function onWheel(e) {
	if (!imgEl) return;
	const step = e.deltaY < 0 ? 1.08 : 1 / 1.08;
	zoomAt(st.scale * step, stagePoint(e));
}

/** 手机上的缩放滑块：以可视区中心为锚点 */
function onZoomInput(e) {
	if (!imgEl) return;
	const sc = scrollerRef.value;
	zoomAt(Number(e.target.value) / 100, { x: sc.clientWidth / 2, y: sc.clientHeight / 2 });
}

// 导出选区：在隐藏 canvas 上把选框区域画成 200×200 的 JPEG
function cropNow(onDone, onFail) {
	if (!imgEl) return onFail('没有正在裁剪的图片。');

	const out = document.createElement('canvas');
	out.width = OUT_SIZE;
	out.height = OUT_SIZE;
	const ctx = out.getContext('2d');
	ctx.fillStyle = '#ffffff';
	ctx.fillRect(0, 0, OUT_SIZE, OUT_SIZE);

	const relX = st.boxX - st.dispX;
	const relY = st.boxY - st.dispY;
	const ratio = st.naturalW / st.dispW;   // 显示尺寸 → 原始像素

	let sx = Math.max(0, Math.round(relX * ratio));
	let sy = Math.max(0, Math.round(relY * ratio));
	let sw = Math.max(1, Math.min(Math.round(st.boxSize * ratio), st.naturalW - sx, st.naturalH - sy));
	sw = Math.min(sw, st.naturalW, st.naturalH);

	ctx.drawImage(imgEl, sx, sy, sw, sw, 0, 0, OUT_SIZE, OUT_SIZE);

	out.toBlob((blob) => {
		if (!blob) return onFail('裁剪失败，请重试。');
		onDone(blob);
	}, 'image/jpeg', 0.9);
}

function onResize() {
	if (!open.value) return;
	measureBase();
	clampBox();
}

onMounted(() => window.addEventListener('resize', onResize));
onBeforeUnmount(() => window.removeEventListener('resize', onResize));

defineExpose({
	open: openCropper,
	close: closeCropper,
	isOpen: () => open.value,
	cropNow
});
</script>

<style scoped src="@/assets/styles/cropper.css"></style>

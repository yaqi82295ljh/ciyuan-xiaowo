import { nextTick, onBeforeUnmount, onMounted } from 'vue';

const GAP_DESKTOP = 18;
const GAP_TABLET = 14;
const GAP_PHONE = 10;

/** 按容器宽度取间距：手机屏上 18px 太散，图片之间要贴得紧一点 */
function gapFor(width) {
	if (width < 520) return GAP_PHONE;
	if (width < 900) return GAP_TABLET;
	return GAP_DESKTOP;
}

/**
 * 瀑布流布局：把容器里的 .item-show 卡片按「当前最短列」绝对定位。
 * 列数：<520px 两列，<900px 三列，否则四列。
 * @param {Ref<HTMLElement|null>} containerRef 容器引用
 * @returns {{ relayout: () => Promise<void> }} 数据变化后调用 relayout 重排
 */
export function useWaterfall(containerRef) {
	let pending = false;

	function layout() {
		const container = containerRef.value;
		if (!container) return;

		const items = container.getElementsByClassName('item-show');
		if (!items.length) {
			container.style.height = '';
			return;
		}

		const containerWidth = container.clientWidth;
		if (!containerWidth) return;

		const GAP = gapFor(containerWidth);
		const columnCount = containerWidth < 520 ? 2 : containerWidth < 900 ? 3 : 4;
		const colWidth = (containerWidth - GAP * (columnCount - 1)) / columnCount;

		// hrr[i] = 第 i 列当前的累计高度
		const hrr = [];

		for (let i = 0; i < items.length; i++) {
			items[i].style.width = colWidth + 'px';
			items[i].style.setProperty('--wf-i', i);

			if (i < columnCount) {
				items[i].style.top = '0px';
				items[i].style.left = i * (colWidth + GAP) + 'px';
				hrr.push(items[i].offsetHeight + GAP);
			} else {
				const minHeight = Math.min(...hrr);
				const index = hrr.indexOf(minHeight);
				items[i].style.top = minHeight + 'px';
				items[i].style.left = index * (colWidth + GAP) + 'px';
				hrr[index] += items[i].offsetHeight + GAP;
			}
		}

		container.style.height = Math.max(...hrr) - GAP + 'px';

		// 图片加载完成后高度会变，需要重排
		for (let k = 0; k < items.length; k++) {
			const img = items[k].querySelector('img');
			if (img && !img.__wfBound) {
				img.__wfBound = true;
				img.addEventListener('load', layoutSoon);
				img.addEventListener('error', layoutSoon);
			}
		}
	}

	function layoutSoon() {
		if (pending) return;
		pending = true;
		requestAnimationFrame(() => {
			pending = false;
			layout();
		});
	}

	/** 等 DOM 更新后重排 */
	async function relayout() {
		await nextTick();
		layoutSoon();
	}

	onMounted(() => {
		layoutSoon();
		window.addEventListener('resize', layoutSoon);
	});

	onBeforeUnmount(() => {
		window.removeEventListener('resize', layoutSoon);
	});

	return { relayout };
}

<template>
	<router-link class="item-show" :class="{ 'is-new': isNew }" :to="'/image/' + work.id">
		<picture class="img-show">
			<img
				:src="work.img"
				:alt="work.title"
				:width="work.width || undefined"
				:height="work.height || undefined"
				referrerpolicy="no-referrer"
				:class="{ 'is-dead': dead }"
				@error="dead = true"
			/>
		</picture>

		<!-- 浮在图片左下角的标签 -->
		<div v-if="!caption && allTags.length" class="tag-show">
			<span v-for="t in allTags" :key="t" class="tagOnShow">{{ t }}</span>
		</div>

		<!-- 个人中心的作品卡带文字说明 -->
		<template v-if="caption">
			<p class="wf-cap">{{ work.title }}</p>
			<p class="wf-cap-sub">{{ captionSub }}</p>
		</template>
	</router-link>
</template>

<script setup>
import { computed, ref } from 'vue';
import { catName } from '@/utils/format';

const props = defineProps({
	work: { type: Object, required: true },
	caption: { type: Boolean, default: false },
	isNew: { type: Boolean, default: false }
});

const dead = ref(false);

const allTags = computed(() => {
	const tags = props.work.ai ? ['AI 生成'] : [];
	return tags.concat(props.work.tags || []);
});

const captionSub = computed(() => {
	const w = props.work;
	let sub = catName(w.categoryId) + (w.ai ? ' · AI 生成' : '');
	if (w.tags && w.tags.length) sub += ' · ' + w.tags.join(' ');
	return sub;
});
</script>

<style scoped>
/* 卡片本体：位置由 useWaterfall 计算后内联写入 */
.item-show{
	position: absolute;
	cursor: pointer;
	color: inherit;
	text-decoration: none;
	animation: card-in var(--dur-slow) var(--ease-q) backwards;
	animation-delay: min(calc(var(--wf-i, 0) * 26ms), 300ms);
}

.item-show,
.item-show *{
	user-select: none;
	-webkit-user-select: none;
}

.item-show:hover .img-show,
.item-show:hover .tag-show{
	transform: translateY(-4px);
}

@keyframes card-in{
	from{ opacity: 0; transform: translateY(14px); }
	to  { opacity: 1; transform: translateY(0); }
}

.img-show{
	display: block;
	overflow: hidden;
	border-radius: var(--r-md);
	/* 不留 min-height：横图会被撑出底部空白，卡片高度交给图片自己决定 */
	background-color: var(--blue-50);
	box-shadow: 0 1px 2px rgba(43, 58, 69, 0.06);
	transition: transform var(--dur) var(--ease-q);
}

.item-show:hover .img-show{
	box-shadow: 0 8px 22px -10px rgba(29, 107, 166, 0.5), 0 1px 2px rgba(43, 58, 69, 0.06);
}

.item-show img{
	width: 100%;
	height: auto;
	display: block;
}

.img-show img.is-dead{
	opacity: 0;
}

.tag-show{
	position: absolute;
	left: 12px;
	bottom: 12px;
	display: flex;
	flex-wrap: wrap;
	gap: 5px;
	pointer-events: none;
	transition: transform var(--dur) var(--ease-q);
}

.tagOnShow{
	display: inline-flex;
	align-items: center;
	height: 22px;
	padding: 0 10px;
	border-radius: var(--r-full);
	font-size: var(--fs-xs);
	font-weight: 500;
	line-height: 1;
	letter-spacing: 0.02em;
	color: var(--ink-900);
	background-color: rgba(255, 255, 255, 0.94);
	border: 1px solid rgba(43, 58, 69, 0.10);
	box-shadow: 0 1px 3px rgba(43, 58, 69, 0.20);
}

/* 刚发布的卡片闪一下 */
.item-show.is-new .img-show{
	animation: up-flash 1.4s var(--ease) 2;
}
@keyframes up-flash{
	0%, 100%{ box-shadow: 0 0 0 0 rgba(29, 107, 166, 0); }
	50%     { box-shadow: 0 0 0 4px rgba(29, 107, 166, 0.5); }
}

/* 卡片下方的文字说明 */
.wf-cap{
	margin: 8px 2px 0;
	font-size: 13px;
	color: var(--ink-900);
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
.wf-cap-sub{
	margin: 2px 2px 0;
	font-size: 11px;
	color: var(--ink-400);
}

/* 手机上把卡片压紧一点：圆角更小、标签更小，避免整页显得松散 */
@media (max-width: 760px){
	.img-show{ border-radius: var(--r-sm); }
	.tag-show{ left: 8px; bottom: 8px; gap: 4px; }
	.tagOnShow{ height: 20px; padding: 0 8px; font-size: 11px; }
	.wf-cap{ margin-top: 6px; font-size: 12px; }
}
</style>

<template>
	<section ref="box" class="wf-container" :class="{ 'has-items': items.length }">
		<p v-if="!items.length" class="wf-empty">{{ emptyText }}</p>
		<ImageCard
			v-for="w in items"
			:key="w.id"
			:work="w"
			:caption="caption"
			:is-new="w.id === newId"
		/>
	</section>
</template>

<script setup>
import { ref, watch } from 'vue';
import ImageCard from '@/components/ImageCard.vue';
import { useWaterfall } from '@/composables/useWaterfall';

const props = defineProps({
	items: { type: Array, default: () => [] },
	emptyText: { type: String, default: '这个条件下还没有作品，换个标签试试~' },
	caption: { type: Boolean, default: false },
	newId: { type: [Number, String], default: null }
});

const box = ref(null);
const { relayout } = useWaterfall(box);

// 列表变化后重排（图片 onload 触发的重排在 composable 内部处理）
watch(() => props.items, relayout);

defineExpose({ relayout });
</script>

<style scoped>
.wf-container{
	min-height: 240px;
	border-radius: var(--r-lg);
	position: relative;
	border: 1px dashed var(--line-blue);
	background-color: rgba(255, 255, 255, 0.55);
	/* 空态时让提示文字在虚线框里垂直居中，不要贴在顶部 */
	display: flex;
	align-items: center;
	justify-content: center;
}

.wf-container.has-items{
	border-color: transparent;
	background-color: transparent;
	display: block;
}

.wf-empty{
	margin: 0;
	padding: var(--sp-6) var(--sp-4);
	text-align: center;
	font-size: var(--fs-md);
	color: var(--ink-400);
}

@media (max-width: 760px){
	.wf-container{ min-height: 160px; border-radius: var(--r-md); }
	.wf-empty{ padding: var(--sp-5) var(--sp-3); font-size: var(--fs-base); }
}
</style>

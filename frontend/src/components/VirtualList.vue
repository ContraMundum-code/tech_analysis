<template>
  <div ref="containerRef" class="virtual-list-container" :style="{ height: height + 'px', overflow: 'auto' }">
    <div :style="{ height: totalHeight + 'px', position: 'relative' }">
      <div
        v-for="item in visibleItems"
        :key="item._index"
        :style="{
          position: 'absolute',
          top: item._top + 'px',
          left: 0,
          right: 0,
          height: itemHeight + 'px'
        }"
      >
        <slot :item="item.data" :index="item._index" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

interface Props {
  /** 列表数据 */
  items: any[]
  /** 每项高度（px） */
  itemHeight: number
  /** 容器高度（px） */
  height: number
  /** 缓冲区行数（默认 5） */
  buffer?: number
}

const props = withDefaults(defineProps<Props>(), {
  buffer: 5
})

const containerRef = ref<HTMLElement | null>(null)
const scrollTop = ref(0)

const totalHeight = computed(() => props.items.length * props.itemHeight)

const visibleItems = computed(() => {
  const start = Math.max(0, Math.floor(scrollTop.value / props.itemHeight) - props.buffer)
  const visibleCount = Math.ceil(props.height / props.itemHeight) + props.buffer * 2
  const end = Math.min(props.items.length, start + visibleCount)

  const result: { _index: number; _top: number; data: any }[] = []
  for (let i = start; i < end; i++) {
    result.push({
      _index: i,
      _top: i * props.itemHeight,
      data: props.items[i]
    })
  }
  return result
})

function onScroll() {
  if (containerRef.value) {
    scrollTop.value = containerRef.value.scrollTop
  }
}

onMounted(() => {
  containerRef.value?.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  containerRef.value?.removeEventListener('scroll', onScroll)
})

watch(() => props.items, () => {
  // 数据变化时重置滚动位置（可选）
})
</script>

<style scoped>
.virtual-list-container {
  -webkit-overflow-scrolling: touch;
}
</style>

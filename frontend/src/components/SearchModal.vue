<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-50 flex items-start justify-center pt-20" @click.self="close">
      <!-- 背景遮罩 -->
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm"></div>
      
      <!-- 搜索框 -->
      <div class="relative w-full max-w-2xl mx-4 bg-gray-800 rounded-xl shadow-2xl border border-gray-700 overflow-hidden">
        <!-- 搜索输入 -->
        <div class="flex items-center gap-3 p-4 border-b border-gray-700">
          <span class="text-gray-400 text-xl">🔍</span>
          <input
            ref="inputRef"
            v-model="keyword"
            type="text"
            placeholder="搜索专利、论文、项目..."
            class="flex-1 bg-transparent text-white text-lg outline-none placeholder-gray-500"
            @input="handleSearch"
            @keydown.esc="close"
          />
          <span v-if="loading" class="text-gray-400">⏳</span>
          <kbd class="px-2 py-1 text-xs text-gray-400 bg-gray-700 rounded">ESC</kbd>
        </div>
        
        <!-- 搜索结果 -->
        <div class="max-h-96 overflow-y-auto">
          <!-- 无结果 -->
          <div v-if="keyword && !loading && totalCount === 0" class="p-8 text-center text-gray-400">
            <span class="text-4xl block mb-2">🔍</span>
            <p>未找到相关结果</p>
          </div>
          
          <!-- 专利结果 -->
          <div v-if="results.patents.length > 0">
            <div class="px-4 py-2 text-xs text-gray-500 uppercase tracking-wider bg-gray-900/50">
              专利 ({{ results.patents.length }})
            </div>
            <div v-for="item in results.patents" :key="'patent-' + item.id" 
                 class="px-4 py-3 hover:bg-gray-700/50 cursor-pointer transition-colors border-b border-gray-700/50"
                 @click="viewDetail(item)">
              <div class="flex items-start gap-3">
                <span class="text-blue-400 text-lg">📜</span>
                <div class="flex-1 min-w-0">
                  <p class="text-white text-sm font-medium truncate">{{ item.title }}</p>
                  <p class="text-gray-400 text-xs mt-1">
                    {{ item.country }} · {{ item.year }}年
                    <span v-if="item.keywords" class="ml-2 text-blue-400">{{ truncateKeywords(item.keywords) }}</span>
                  </p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 论文结果 -->
          <div v-if="results.papers.length > 0">
            <div class="px-4 py-2 text-xs text-gray-500 uppercase tracking-wider bg-gray-900/50">
              论文 ({{ results.papers.length }})
            </div>
            <div v-for="item in results.papers" :key="'paper-' + item.id"
                 class="px-4 py-3 hover:bg-gray-700/50 cursor-pointer transition-colors border-b border-gray-700/50"
                 @click="viewDetail(item)">
              <div class="flex items-start gap-3">
                <span class="text-green-400 text-lg">📄</span>
                <div class="flex-1 min-w-0">
                  <p class="text-white text-sm font-medium truncate">{{ item.title }}</p>
                  <p class="text-gray-400 text-xs mt-1">
                    {{ item.country }} · {{ item.year }}年
                    <span v-if="item.keywords" class="ml-2 text-green-400">{{ truncateKeywords(item.keywords) }}</span>
                  </p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 项目结果 -->
          <div v-if="results.projects.length > 0">
            <div class="px-4 py-2 text-xs text-gray-500 uppercase tracking-wider bg-gray-900/50">
              项目 ({{ results.projects.length }})
            </div>
            <div v-for="item in results.projects" :key="'project-' + item.id"
                 class="px-4 py-3 hover:bg-gray-700/50 cursor-pointer transition-colors border-b border-gray-700/50"
                 @click="viewDetail(item)">
              <div class="flex items-start gap-3">
                <span class="text-purple-400 text-lg">📁</span>
                <div class="flex-1 min-w-0">
                  <p class="text-white text-sm font-medium truncate">{{ item.title }}</p>
                  <p class="text-gray-400 text-xs mt-1">
                    {{ item.country }} · {{ item.year }}年
                    <span v-if="item.keywords" class="ml-2 text-purple-400">{{ truncateKeywords(item.keywords) }}</span>
                  </p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 提示 -->
          <div v-if="!keyword" class="p-6 text-center text-gray-500">
            <p class="text-sm">输入关键词搜索专利、论文、项目</p>
            <p class="text-xs mt-2">支持标题、关键词、国家/机构搜索</p>
          </div>
        </div>
        
        <!-- 底部提示 -->
        <div v-if="totalCount > 0" class="px-4 py-2 border-t border-gray-700 bg-gray-900/50">
          <p class="text-xs text-gray-500">共找到 {{ totalCount }} 条结果</p>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, computed } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import { searchApi, type SearchItem, type SearchResult } from '@/api/search'

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'select', item: SearchItem): void
}>()

const inputRef = ref<HTMLInputElement | null>(null)
const keyword = ref('')
const loading = ref(false)
const results = ref<SearchResult>({
  patents: [],
  papers: [],
  projects: [],
  totalCount: 0
})

const totalCount = computed(() => results.value.totalCount)

watch(() => props.visible, (val) => {
  if (val) {
    nextTick(() => {
      inputRef.value?.focus()
    })
  } else {
    keyword.value = ''
    results.value = { patents: [], papers: [], projects: [], totalCount: 0 }
  }
})

function close() {
  emit('update:visible', false)
}

// 使用 VueUse 防抖：300ms 内多次输入只触发一次请求
const debouncedSearch = useDebounceFn(async () => {
  if (!keyword.value.trim()) {
    results.value = { patents: [], papers: [], projects: [], totalCount: 0 }
    return
  }
  loading.value = true
  try {
    const res = await searchApi.search(keyword.value.trim())
    results.value = res.data || { patents: [], papers: [], projects: [], totalCount: 0 }
  } catch (e) {
    console.error('搜索失败', e)
  } finally {
    loading.value = false
  }
}, 300)

function handleSearch() {
  debouncedSearch()
}

function truncateKeywords(keywords: string): string {
  if (!keywords) return ''
  const arr = keywords.split(/[,;，；]/).slice(0, 3)
  return arr.join(', ') + (keywords.split(/[,;，；]/).length > 3 ? '...' : '')
}

function viewDetail(item: SearchItem) {
  emit('select', item)
  close()
}
</script>

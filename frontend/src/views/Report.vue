<template>
  <div class="space-y-6">
    <!-- 当前选择的技术方向 -->
    <div class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-gray-700/50">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <span class="text-3xl">{{ categoryIcon }}</span>
          <div>
            <h2 class="text-xl font-bold text-white">{{ categoryName }}</h2>
            <p class="text-gray-400 text-sm mt-1">{{ categoryCode }} · 技术分析报告</p>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <button 
            @click="generateReport" 
            :disabled="generating"
            class="py-2 px-4 rounded-lg bg-gradient-to-r from-purple-500 to-indigo-500 text-white font-medium hover:from-purple-600 hover:to-indigo-600 transition-all text-sm flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="generating" class="w-4 h-4 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            {{ generating ? '生成中...' : '生成报告' }}
          </button>
          <router-link 
            to="/classification" 
            class="py-2 px-4 rounded-lg bg-gray-700 text-gray-300 hover:bg-gray-600 transition-all text-sm"
          >
            重新选择
          </router-link>
        </div>
      </div>
      <!-- 报告生成动画 -->
      <div v-if="generating" class="mt-6 flex flex-col items-center">
        <div class="report-loader">
          <div class="page page-1">
            <div class="line"></div>
            <div class="line"></div>
            <div class="line short"></div>
          </div>
          <div class="page page-2">
            <div class="line"></div>
            <div class="line short"></div>
            <div class="line"></div>
          </div>
          <div class="page page-3">
            <div class="line short"></div>
            <div class="line"></div>
            <div class="line"></div>
          </div>
          <div class="pen">✍️</div>
        </div>
        <p class="text-gray-400 text-sm mt-4 animate-pulse">
          正在生成5000-7000字的深度分析报告，预计需要1-2分钟...
        </p>
      </div>
    </div>
    
    <!-- 报告预览 -->
    <div class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-gray-700/50">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-white flex items-center">
          <svg class="w-5 h-5 mr-2 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          报告预览
        </h3>
        <span v-if="reportContent" class="text-gray-400 text-sm">
          约 {{ wordCount }} 字
        </span>
      </div>
      
      <div v-if="reportContent" 
           class="report-content bg-gray-900/50 p-6 rounded-lg max-h-[600px] overflow-y-auto"
           v-html="renderedContent">
      </div>
      <div v-else class="text-gray-500 text-center py-24 bg-gray-900/30 rounded-lg border border-dashed border-gray-700">
        <svg class="w-16 h-16 mx-auto mb-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <p class="text-lg">暂无报告</p>
        <p class="text-sm mt-2">{{ categoryName }} 领域的分析报告</p>
      </div>
      
      <div v-if="reportContent" class="mt-4 flex gap-3">
        <button @click="saveReport" class="flex-1 py-2 px-4 rounded-lg bg-gradient-to-r from-green-500 to-emerald-500 text-white font-medium hover:from-green-600 hover:to-emerald-600 transition-all flex items-center justify-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" />
          </svg>
          保存报告
        </button>
        <button @click="downloadReport" class="flex-1 py-2 px-4 rounded-lg bg-gray-700 text-gray-200 font-medium hover:bg-gray-600 transition-all flex items-center justify-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          下载报告
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { reportApi } from '@/api/report'
import { useCategoryStore } from '@/stores/category'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const categoryStore = useCategoryStore()

// 分类图标映射
const iconMap: Record<string, string> = {
  'TP18': '🤖',
  'TP181': '👁️',
  'TP182': '💬',
  'TP183': '🧠',
  'TP184': '💡',
  'TP3': '💻',
  'TP2': '⚙️',
  'TN': '⚡',
  'TN91': '📡'
}

// 优先从 categoryStore 获取分类信息，其次从 URL
const categoryCode = computed(() => categoryStore.currentCode || (route.query.code as string) || 'TP18')
const categoryName = computed(() => categoryStore.currentName || (route.query.name as string) || '人工智能')
const categoryIcon = computed(() => iconMap[categoryCode.value] || categoryStore.icon || '📊')

const reportContent = ref('')
const generating = ref(false)

// 如果没有选择分类，跳转到分类页面
onMounted(() => {
  if (!categoryStore.currentCode && !route.query.code) {
    router.push('/classification')
  }
})

// 计算字数
const wordCount = computed(() => {
  return reportContent.value.replace(/[#*\-\n\s]/g, '').length
})

// 简单的Markdown渲染
const renderedContent = computed(() => {
  if (!reportContent.value) return ''
  let html = reportContent.value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/^### (.+)$/gm, '<h3 class="text-lg font-semibold text-gray-200 mt-4 mb-2">$1</h3>')
    .replace(/^## (.+)$/gm, '<h2 class="text-xl font-bold text-gray-100 mt-6 mb-3">$1</h2>')
    .replace(/^# (.+)$/gm, '<h1 class="text-2xl font-bold text-white mt-6 mb-4">$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong class="text-white">$1</strong>')
    .replace(/^- (.+)$/gm, '<li class="ml-4 text-gray-300">$1</li>')
    .replace(/^(\d+)\. (.+)$/gm, '<li class="ml-4 text-gray-300">$1. $2</li>')
    .replace(/^> (.+)$/gm, '<blockquote class="border-l-4 border-blue-500 pl-4 text-gray-400 italic my-2">$1</blockquote>')
    .replace(/^---$/gm, '<hr class="border-gray-600 my-4">')
    .replace(/\n\n/g, '</p><p class="text-gray-300 leading-relaxed mb-3">')
  return '<p class="text-gray-300 leading-relaxed mb-3">' + html + '</p>'
})

async function generateReport() {
  generating.value = true
  reportContent.value = ''
  
  try {
    const res = await reportApi.generate(
      categoryName.value,
      `请生成一份关于"${categoryCode.value} ${categoryName.value}"技术领域的深度分析报告`
    )
    reportContent.value = res.data?.content || ''
    if (reportContent.value) {
      ElMessage.success('报告生成成功')
    }
  } catch (e: any) {
    ElMessage.error('报告生成失败: ' + (e.message || '未知错误'))
  } finally {
    generating.value = false
  }
}

async function saveReport() {
  try {
    await reportApi.save({ 
      title: `${categoryName.value}技术分析报告`, 
      content: reportContent.value 
    })
    ElMessage.success('报告已保存')
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

function downloadReport() {
  const blob = new Blob([reportContent.value], { type: 'text/markdown' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${categoryName.value}_技术分析报告.md`
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.report-content :deep(h1),
.report-content :deep(h2),
.report-content :deep(h3) {
  margin-top: 1rem;
}

.report-content :deep(li) {
  list-style-type: disc;
}

/* 报告生成主题加载动画 */
.report-loader {
  position: relative;
  width: 80px;
  height: 100px;
}

.page {
  position: absolute;
  width: 50px;
  height: 65px;
  background: linear-gradient(135deg, #374151 0%, #1f2937 100%);
  border: 1px solid #4b5563;
  border-radius: 4px;
  padding: 8px;
  transform-origin: bottom left;
}

.page-1 {
  z-index: 3;
  left: 15px;
  animation: page-flip 2s ease-in-out infinite;
}

.page-2 {
  z-index: 2;
  left: 10px;
  opacity: 0.7;
  animation: page-flip 2s ease-in-out infinite 0.2s;
}

.page-3 {
  z-index: 1;
  left: 5px;
  opacity: 0.4;
  animation: page-flip 2s ease-in-out infinite 0.4s;
}

.page .line {
  height: 4px;
  background: linear-gradient(90deg, #6366f1 0%, #8b5cf6 50%, transparent 100%);
  border-radius: 2px;
  margin-bottom: 5px;
  animation: line-write 1.5s ease-in-out infinite;
}

.page .line.short {
  width: 60%;
}

.pen {
  position: absolute;
  font-size: 1.5rem;
  right: 0;
  top: 10px;
  animation: pen-write 1s ease-in-out infinite;
}

@keyframes page-flip {
  0%, 100% { transform: rotateY(0deg); }
  50% { transform: rotateY(-5deg); }
}

@keyframes line-write {
  0% { width: 0; opacity: 0; }
  50% { width: 100%; opacity: 1; }
  100% { width: 100%; opacity: 0.6; }
}

@keyframes pen-write {
  0%, 100% { transform: translateY(0) rotate(-15deg); }
  50% { transform: translateY(5px) rotate(-25deg); }
}
</style>

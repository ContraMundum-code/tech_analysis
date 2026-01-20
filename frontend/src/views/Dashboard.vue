<template>
  <div class="space-y-6">
    <!-- 当前分类提示 -->
    <div v-if="categoryStore.hasCategory" class="bg-gradient-to-r from-blue-500/20 to-purple-500/20 backdrop-blur-sm p-6 rounded-xl border border-blue-500/30">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <span class="text-4xl">{{ categoryStore.icon }}</span>
          <div>
            <h2 class="text-xl font-bold text-white">{{ categoryStore.currentName }}</h2>
            <p class="text-gray-400 text-sm mt-1">{{ categoryStore.currentCode }} · 当前正在分析该技术领域</p>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <router-link 
            to="/techmap" 
            class="py-2 px-4 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-all text-sm flex items-center gap-2"
          >
            🗺️ 技术图谱
          </router-link>
          <router-link 
            to="/trend" 
            class="py-2 px-4 rounded-lg bg-green-600 text-white hover:bg-green-700 transition-all text-sm flex items-center gap-2"
          >
            📈 趋势分析
          </router-link>
          <router-link 
            to="/network" 
            class="py-2 px-4 rounded-lg bg-purple-600 text-white hover:bg-purple-700 transition-all text-sm flex items-center gap-2"
          >
            🔗 关联网络
          </router-link>
          <router-link 
            to="/report" 
            class="py-2 px-4 rounded-lg bg-orange-600 text-white hover:bg-orange-700 transition-all text-sm flex items-center gap-2"
          >
            📝 分析报告
          </router-link>
        </div>
      </div>
    </div>

    <!-- 选择分类提示（未选择时显示） -->
    <div v-else class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-gray-700/50">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <span class="text-4xl">🔬</span>
          <div>
            <h2 class="text-xl font-bold text-white">开始技术分析</h2>
            <p class="text-gray-400 text-sm mt-1">选择一个技术领域，进行深入的专利、项目和论文分析</p>
          </div>
        </div>
        <router-link 
          to="/classification" 
          class="py-3 px-6 rounded-lg bg-gradient-to-r from-blue-500 to-purple-600 text-white font-medium hover:from-blue-600 hover:to-purple-700 transition-all flex items-center gap-2"
        >
          选择技术分类 →
        </router-link>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="stat-card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">专利总数</p>
            <p class="text-3xl font-bold text-white mt-1">{{ stats.patentCount.toLocaleString() }}</p>
          </div>
          <div class="w-12 h-12 rounded-lg bg-blue-500/20 flex items-center justify-center">
            <span class="text-2xl">📄</span>
          </div>
        </div>
        <div class="mt-3 flex items-center text-sm">
          <span class="text-green-400">↑ 12.5%</span>
          <span class="text-gray-500 ml-2">较上月</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">项目总数</p>
            <p class="text-3xl font-bold text-white mt-1">{{ stats.projectCount.toLocaleString() }}</p>
          </div>
          <div class="w-12 h-12 rounded-lg bg-green-500/20 flex items-center justify-center">
            <span class="text-2xl">📁</span>
          </div>
        </div>
        <div class="mt-3 flex items-center text-sm">
          <span class="text-green-400">↑ 8.3%</span>
          <span class="text-gray-500 ml-2">较上月</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">论文总数</p>
            <p class="text-3xl font-bold text-white mt-1">{{ stats.paperCount.toLocaleString() }}</p>
          </div>
          <div class="w-12 h-12 rounded-lg bg-purple-500/20 flex items-center justify-center">
            <span class="text-2xl">📚</span>
          </div>
        </div>
        <div class="mt-3 flex items-center text-sm">
          <span class="text-green-400">↑ 15.2%</span>
          <span class="text-gray-500 ml-2">较上月</span>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-sm">覆盖国家/机构</p>
            <p class="text-3xl font-bold text-white mt-1">{{ stats.countryCount }}</p>
          </div>
          <div class="w-12 h-12 rounded-lg bg-orange-500/20 flex items-center justify-center">
            <span class="text-2xl">🌍</span>
          </div>
        </div>
        <div class="mt-3 flex items-center text-sm">
          <span class="text-blue-400">→ 稳定</span>
          <span class="text-gray-500 ml-2">较上月</span>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="chart-container p-6">
        <h3 class="text-lg font-semibold text-white mb-4">各国/机构专利分布</h3>
        <div ref="pieChartRef" class="h-80"></div>
      </div>
      
      <div class="chart-container p-6">
        <h3 class="text-lg font-semibold text-white mb-4">年度趋势</h3>
        <div ref="lineChartRef" class="h-80"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/analysis'
import { useCategoryStore } from '@/stores/category'

const categoryStore = useCategoryStore()
const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()

const stats = ref({
  patentCount: 0,
  projectCount: 0,
  paperCount: 0,
  countryCount: 0
})

// 深色主题配置
const darkTheme = {
  backgroundColor: 'transparent',
  textStyle: { color: '#9ca3af' },
  title: { textStyle: { color: '#ffffff' } },
  legend: { textStyle: { color: '#9ca3af' } },
  tooltip: {
    backgroundColor: 'rgba(31, 41, 55, 0.9)',
    borderColor: '#374151',
    textStyle: { color: '#ffffff' }
  }
}

onMounted(async () => {
  try {
    const code = categoryStore.currentCode || undefined
    
    // 优先使用预加载的缓存数据
    const cache = categoryStore.preloadedData
    let patentCountries: any[] = []
    let projectCountries: any[] = []
    let paperCountries: any[] = []
    let yearlyData: any[] = []
    
    if (cache && cache.techMapPatent && cache.techMapPaper && cache.techMapProject && cache.trend) {
      // 使用缓存数据
      console.log('Dashboard: 使用预加载缓存数据')
      patentCountries = cache.techMapPatent?.countries || []
      projectCountries = cache.techMapProject?.countries || []
      paperCountries = cache.techMapPaper?.countries || []
      yearlyData = cache.trend?.yearlyData || []
    } else {
      // 重新请求数据
      console.log('Dashboard: 重新请求数据')
      const [patentRes, projectRes, paperRes, trendRes] = await Promise.all([
        analysisApi.getTechMap('patent', code),
        analysisApi.getTechMap('project', code),
        analysisApi.getTechMap('paper', code),
        analysisApi.getTrend(undefined, undefined, undefined, code)
      ])
      
      patentCountries = patentRes.data?.countries || []
      projectCountries = projectRes.data?.countries || []
      paperCountries = paperRes.data?.countries || []
      yearlyData = trendRes.data?.yearlyData || []
    }
    
    stats.value.patentCount = patentCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    stats.value.projectCount = projectCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    stats.value.paperCount = paperCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    
    const allCountries = new Set([
      ...patentCountries.map((c: any) => c.country),
      ...projectCountries.map((c: any) => c.country),
      ...paperCountries.map((c: any) => c.country)
    ])
    stats.value.countryCount = allCountries.size
    
    // 饼图
    if (pieChartRef.value && patentCountries.length > 0) {
      const pieChart = echarts.init(pieChartRef.value)
      pieChart.setOption({
        ...darkTheme,
        tooltip: { 
          ...darkTheme.tooltip,
          trigger: 'item', 
          formatter: '{b}: {c} ({d}%)' 
        },
        legend: {
          ...darkTheme.legend,
          orient: 'vertical',
          left: 'left',
          top: 'middle'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 4,
            borderColor: '#1f2937',
            borderWidth: 2
          },
          label: {
            show: true,
            color: '#9ca3af',
            formatter: (params: any) => {
              const name = params.name.length > 6 ? params.name.substring(0, 6) + '...' : params.name
              return `${name}: ${params.percent}%`
            }
          },
          labelLayout: { hideOverlap: true },
          data: patentCountries.slice(0, 10).map((c: any) => ({ name: c.country, value: c.count }))
        }]
      })
      window.addEventListener('resize', () => pieChart.resize())
    }
    
    // 折线图
    if (lineChartRef.value && yearlyData.length > 0) {
      const lineChart = echarts.init(lineChartRef.value)
      lineChart.setOption({
        ...darkTheme,
        tooltip: { 
          ...darkTheme.tooltip,
          trigger: 'axis' 
        },
        legend: { 
          ...darkTheme.legend,
          data: ['专利', '项目', '论文'],
          top: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: { 
          type: 'category', 
          data: yearlyData.map((d: any) => d.year),
          axisLine: { lineStyle: { color: '#374151' } },
          axisLabel: { color: '#9ca3af' }
        },
        yAxis: { 
          type: 'value',
          axisLine: { lineStyle: { color: '#374151' } },
          axisLabel: { color: '#9ca3af' },
          splitLine: { lineStyle: { color: '#374151', type: 'dashed' } }
        },
        series: [
          { 
            name: '专利', 
            type: 'line', 
            smooth: true,
            data: yearlyData.map((d: any) => d.patentCount),
            itemStyle: { color: '#3b82f6' },
            areaStyle: { color: 'rgba(59, 130, 246, 0.1)' }
          },
          { 
            name: '项目', 
            type: 'line', 
            smooth: true,
            data: yearlyData.map((d: any) => d.projectCount),
            itemStyle: { color: '#10b981' },
            areaStyle: { color: 'rgba(16, 185, 129, 0.1)' }
          },
          { 
            name: '论文', 
            type: 'line', 
            smooth: true,
            data: yearlyData.map((d: any) => d.paperCount),
            itemStyle: { color: '#8b5cf6' },
            areaStyle: { color: 'rgba(139, 92, 246, 0.1)' }
          }
        ]
      })
      window.addEventListener('resize', () => lineChart.resize())
    }
  } catch (e) {
    console.error('加载数据失败', e)
  }
})
</script>

<style scoped>
.stat-card {
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  border: 1px solid #374151;
  border-radius: 0.75rem;
  padding: 1.5rem;
}

.chart-container {
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  border: 1px solid #374151;
  border-radius: 0.75rem;
}
</style>

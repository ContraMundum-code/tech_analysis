<template>
  <div class="space-y-6">
    <!-- 页面标题和操作按钮 -->
    <div class="flex flex-wrap justify-between items-start gap-4">
      <div class="flex flex-col gap-2">
        <h1 class="text-white text-3xl font-bold leading-tight">趋势分析</h1>
        <p class="text-gray-400 text-base font-normal">分析技术发展趋势、热门技术排行和成熟度分布</p>
      </div>
      <div class="flex items-center gap-2">
        <button @click="goToReport" class="flex items-center gap-2 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-all text-sm font-medium">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          生成报告
        </button>
      </div>
    </div>

    <!-- 控制面板 -->
    <div class="chart-container p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-white mb-2">国家/地区</label>
          <el-select v-model="country" placeholder="选择国家" clearable @change="loadData" class="w-full">
            <el-option label="全部" value="" />
            <el-option v-for="c in countryList" :key="c" :label="c" :value="c" />
          </el-select>
        </div>
        <div>
          <label class="block text-sm font-medium text-white mb-2">时间范围</label>
          <el-date-picker v-model="yearRange" type="yearrange" start-placeholder="开始年份" end-placeholder="结束年份" @change="loadData" class="w-full" />
        </div>
        <div>
          <label class="block text-sm font-medium text-white mb-2">分析指标</label>
          <div class="flex flex-wrap gap-2">
            <label class="flex items-center gap-2 text-sm text-gray-300">
              <input type="checkbox" v-model="showPatent" class="rounded bg-gray-700 border-gray-600 text-blue-500">
              专利
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-300">
              <input type="checkbox" v-model="showProject" class="rounded bg-gray-700 border-gray-600 text-green-500">
              项目
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-300">
              <input type="checkbox" v-model="showPaper" class="rounded bg-gray-700 border-gray-600 text-purple-500">
              论文
            </label>
          </div>
        </div>
        <div class="flex items-end">
          <button @click="resetFilters" class="w-full h-10 bg-gray-700 text-white font-medium text-sm rounded-lg hover:bg-gray-600 transition-colors">
            重置
          </button>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="chart-container p-6">
        <h3 class="text-lg font-semibold text-white mb-4">年度趋势</h3>
        <div ref="trendChartRef" class="h-80"></div>
      </div>
      <div class="chart-container p-6">
        <h3 class="text-lg font-semibold text-white mb-4">国家/机构对比</h3>
        <div ref="compareChartRef" class="h-80"></div>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="stat-card">
        <h4 class="text-white font-semibold mb-3">热门技术排行</h4>
        <div class="space-y-2" v-if="hotTechData.length > 0">
          <div v-for="(item, index) in hotTechData.slice(0, 5)" :key="index" class="flex items-center justify-between text-sm">
            <span class="text-gray-400 truncate max-w-[150px]" :title="item.name">{{ item.name }}</span>
            <span :class="item.growthRate >= 0 ? 'text-green-400' : 'text-red-400'">
              {{ item.growthRate >= 0 ? '+' : '' }}{{ item.growthRate }}%
            </span>
          </div>
        </div>
        <div v-else class="text-gray-500 text-sm">暂无数据</div>
      </div>
      
      <div class="stat-card">
        <h4 class="text-white font-semibold mb-3">技术成熟度</h4>
        <div class="space-y-3">
          <div>
            <div class="flex justify-between text-sm text-gray-400 mb-1">
              <span>新兴技术</span>
              <span>{{ maturityData.emergingPercent }}%</span>
            </div>
            <div class="w-full bg-gray-700 rounded-full h-2">
              <div class="bg-blue-500 h-2 rounded-full transition-all" :style="{ width: maturityData.emergingPercent + '%' }"></div>
            </div>
          </div>
          <div>
            <div class="flex justify-between text-sm text-gray-400 mb-1">
              <span>成长技术</span>
              <span>{{ maturityData.growingPercent }}%</span>
            </div>
            <div class="w-full bg-gray-700 rounded-full h-2">
              <div class="bg-green-500 h-2 rounded-full transition-all" :style="{ width: maturityData.growingPercent + '%' }"></div>
            </div>
          </div>
          <div>
            <div class="flex justify-between text-sm text-gray-400 mb-1">
              <span>成熟技术</span>
              <span>{{ maturityData.maturePercent }}%</span>
            </div>
            <div class="w-full bg-gray-700 rounded-full h-2">
              <div class="bg-purple-500 h-2 rounded-full transition-all" :style="{ width: maturityData.maturePercent + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <h4 class="text-white font-semibold mb-3">区域分布</h4>
        <div class="space-y-2" v-if="regionData.length > 0">
          <div v-for="(item, index) in regionData" :key="index" class="flex items-center justify-between text-sm">
            <span class="text-gray-400">{{ item.region }}</span>
            <span class="text-blue-400">{{ item.percent }}%</span>
          </div>
        </div>
        <div v-else class="text-gray-500 text-sm">暂无数据</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/analysis'
import { useCategoryStore } from '@/stores/category'

const router = useRouter()
const categoryStore = useCategoryStore()
const trendChartRef = ref<HTMLElement>()
const compareChartRef = ref<HTMLElement>()
const country = ref('')
const yearRange = ref<[Date, Date] | null>(null)
const countryList = ref<string[]>([])
const showPatent = ref(true)
const showProject = ref(true)
const showPaper = ref(true)

// 跳转到报告页面
function goToReport() {
  router.push('/report')
}

// 新增数据
const hotTechData = ref<{name: string, count: number, growthRate: number}[]>([])
const maturityData = ref({ emergingPercent: 0, growingPercent: 0, maturePercent: 0 })
const regionData = ref<{region: string, count: number, percent: number}[]>([])

let trendChart: echarts.ECharts
let compareChart: echarts.ECharts

const darkTheme = {
  backgroundColor: 'transparent',
  textStyle: { color: '#9ca3af' },
  tooltip: {
    backgroundColor: 'rgba(31, 41, 55, 0.95)',
    borderColor: '#374151',
    textStyle: { color: '#ffffff' }
  }
}

function resetFilters() {
  country.value = ''
  yearRange.value = null
  showPatent.value = true
  showProject.value = true
  showPaper.value = true
  loadData()
}

async function loadCountries() {
  try {
    // 优先使用缓存
    const cache = categoryStore.preloadedData
    let countries: any[] = []
    
    if (cache && cache.techMapPatent) {
      console.log('TrendAnalysis: 使用缓存加载国家列表')
      countries = cache.techMapPatent?.countries || []
    } else {
      const res = await analysisApi.getTechMap('patent', categoryStore.currentCode || undefined)
      countries = res.data?.countries || []
    }
    
    countryList.value = countries.map((c: any) => c.country).filter((c: string) => c)
  } catch (e) {
    console.error('加载国家列表失败', e)
  }
}

async function loadStatCards() {
  try {
    const code = categoryStore.currentCode || undefined
    const cache = categoryStore.preloadedData
    
    // 优先使用缓存数据
    if (cache && cache.hotTech && cache.maturity && cache.region) {
      console.log('TrendAnalysis: 使用缓存加载统计卡片')
      hotTechData.value = cache.hotTech?.rankings || []
      maturityData.value = cache.maturity || { emergingPercent: 0, growingPercent: 0, maturePercent: 0 }
      regionData.value = cache.region?.regions || []
    } else {
      const [hotRes, maturityRes, regionRes] = await Promise.all([
        analysisApi.getHotTech(code),
        analysisApi.getMaturity(code),
        analysisApi.getRegion(code)
      ])
      
      hotTechData.value = hotRes.data?.rankings || []
      maturityData.value = maturityRes.data || { emergingPercent: 0, growingPercent: 0, maturePercent: 0 }
      regionData.value = regionRes.data?.regions || []
    }
  } catch (e) {
    console.error('加载统计卡片数据失败', e)
  }
}

async function loadData() {
  const startYear = yearRange.value?.[0]?.getFullYear()
  const endYear = yearRange.value?.[1]?.getFullYear()
  const code = categoryStore.currentCode || undefined
  
  try {
    // 优先使用缓存数据（仅当没有筛选条件时）
    const cache = categoryStore.preloadedData
    let yearlyData: any[] = []
    let competitionData: any = null
    
    if (cache && cache.trend && !country.value && !startYear && !endYear) {
      console.log('TrendAnalysis: 使用缓存加载趋势数据')
      yearlyData = cache.trend?.yearlyData || []
      // 竞争力分析仍需请求
      const competitionRes = await analysisApi.getCompetition(code)
      competitionData = competitionRes.data
    } else {
      const [trendRes, competitionRes] = await Promise.all([
        analysisApi.getTrend(country.value || undefined, startYear, endYear, code),
        analysisApi.getCompetition(code)
      ])
      
      yearlyData = trendRes.data?.yearlyData || []
      competitionData = competitionRes.data
    }
    
    if (trendChart && yearlyData.length > 0) {
      const series: any[] = []
      if (showPatent.value) {
        series.push({ 
          name: '专利', type: 'line', smooth: true, 
          data: yearlyData.map((d: any) => d.patentCount),
          itemStyle: { color: '#3b82f6' },
          areaStyle: { color: 'rgba(59, 130, 246, 0.1)' }
        })
      }
      if (showProject.value) {
        series.push({ 
          name: '项目', type: 'line', smooth: true, 
          data: yearlyData.map((d: any) => d.projectCount),
          itemStyle: { color: '#10b981' },
          areaStyle: { color: 'rgba(16, 185, 129, 0.1)' }
        })
      }
      if (showPaper.value) {
        series.push({ 
          name: '论文', type: 'line', smooth: true, 
          data: yearlyData.map((d: any) => d.paperCount),
          itemStyle: { color: '#8b5cf6' },
          areaStyle: { color: 'rgba(139, 92, 246, 0.1)' }
        })
      }
      
      trendChart.setOption({
        ...darkTheme,
        tooltip: { ...darkTheme.tooltip, trigger: 'axis' },
        legend: { data: ['专利', '项目', '论文'], textStyle: { color: '#9ca3af' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
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
        series
      }, true)
    }
    
    const competitions = competitionData?.competitions || []
    if (compareChart && competitions.length > 0) {
      compareChart.setOption({
        ...darkTheme,
        tooltip: { ...darkTheme.tooltip, trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['专利', '论文'], textStyle: { color: '#9ca3af' } },
        grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
        xAxis: { 
          type: 'category', 
          data: competitions.map((c: any) => c.country), 
          axisLabel: { rotate: 30, color: '#9ca3af' },
          axisLine: { lineStyle: { color: '#374151' } }
        },
        yAxis: { 
          type: 'value',
          axisLine: { lineStyle: { color: '#374151' } },
          axisLabel: { color: '#9ca3af' },
          splitLine: { lineStyle: { color: '#374151', type: 'dashed' } }
        },
        series: [
          { name: '专利', type: 'bar', data: competitions.map((c: any) => c.patentCount), itemStyle: { color: '#3b82f6' } },
          { name: '论文', type: 'bar', data: competitions.map((c: any) => c.paperCount), itemStyle: { color: '#8b5cf6' } }
        ]
      }, true)
    }
  } catch (e) {
    console.error('加载数据失败', e)
  }
}

watch([showPatent, showProject, showPaper], () => loadData())

onMounted(async () => {
  if (trendChartRef.value) trendChart = echarts.init(trendChartRef.value)
  if (compareChartRef.value) compareChart = echarts.init(compareChartRef.value)
  await loadCountries()
  loadData()
  loadStatCards()
  
  window.addEventListener('resize', () => {
    trendChart?.resize()
    compareChart?.resize()
  })
})
</script>

<style scoped>
.chart-container {
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  border: 1px solid #374151;
  border-radius: 0.75rem;
}

.stat-card {
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  border: 1px solid #374151;
  border-radius: 0.75rem;
  padding: 1.5rem;
}
</style>

<template>
  <div class="space-y-6">
    <!-- 页面标题和操作按钮 -->
    <div class="flex flex-wrap justify-between items-start gap-4">
      <div class="flex flex-col gap-2">
        <h1 class="text-white text-3xl font-bold leading-tight">技术图谱分析</h1>
        <p class="text-gray-400 text-base font-normal">交互式探索成熟技术、前沿技术和区域特色技术网络</p>
      </div>
      <div class="flex items-center gap-2">
        <button @click="toggleFullscreen" class="flex items-center justify-center p-2 h-10 w-10 text-gray-400 border border-gray-700 rounded-lg hover:bg-gray-800 transition-all" title="全屏显示">
          ⛶
        </button>
      </div>
    </div>

    <!-- 技术图谱主区域 -->
    <div class="grid grid-cols-12 gap-6">
      <!-- 左侧主图表 -->
      <div class="col-span-12 lg:col-span-9">
        <div class="chart-container p-6" ref="mainContainerRef">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold text-white">全球技术分布排行</h3>
            <div class="flex items-center gap-3">
              <!-- 图表类型切换按钮 -->
              <div class="flex bg-gray-800 rounded-lg p-1">
                <button @click="chartMode = 'bar'" 
                        :class="['px-3 py-1.5 text-sm rounded-md transition-all', chartMode === 'bar' ? 'bg-blue-600 text-white' : 'text-gray-400 hover:text-white']">
                  📊 柱状图
                </button>
                <button @click="chartMode = 'map'" 
                        :class="['px-3 py-1.5 text-sm rounded-md transition-all', chartMode === 'map' ? 'bg-blue-600 text-white' : 'text-gray-400 hover:text-white']">
                  🗺️ 地图
                </button>
              </div>
              <select v-model="dataType" @change="loadData" 
                      class="bg-gray-800 border border-gray-600 text-white rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                <option value="patent">专利数量</option>
                <option value="project">项目数量</option>
                <option value="paper">论文数量</option>
              </select>
            </div>
          </div>
          <div v-if="loading" class="flex items-center justify-center h-[500px]">
            <div class="flex flex-col items-center gap-4">
              <div class="globe-loader">
                <div class="globe">🌍</div>
                <div class="ring ring-1"></div>
                <div class="ring ring-2"></div>
                <div class="ring ring-3"></div>
                <div class="pulse-dot"></div>
                <div class="pulse-dot delay-1"></div>
                <div class="pulse-dot delay-2"></div>
              </div>
              <span class="text-gray-400 animate-pulse">正在加载全球技术分布数据...</span>
            </div>
          </div>
          <div v-show="!loading" ref="mainChartRef" style="width: 100%; height: 500px;"></div>
        </div>
      </div>

      <!-- 右侧分析面板 -->
      <div class="col-span-12 lg:col-span-3 space-y-4">
        <!-- 国家技术分析 -->
        <div class="chart-container p-4">
          <div class="mb-4">
            <h4 class="font-bold text-white">国家技术分析</h4>
            <p class="text-xs text-gray-400 mt-1">点击左侧图表查看详细数据</p>
          </div>
          <select v-model="countryChartType" @change="updateCountryChart" 
                  class="w-full bg-gray-800 border border-gray-600 text-white rounded-lg px-3 py-2 text-sm mb-4 focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            <option value="count">数量分布</option>
            <option value="quality">质量指标</option>
          </select>
          <div v-if="!selectedCountry" class="flex flex-col items-center justify-center h-[250px] text-gray-400">
            <span class="text-4xl mb-2">📊</span>
            <p>选择国家查看详细数据</p>
          </div>
          <div v-show="selectedCountry" ref="countryChartRef" style="width: 100%; height: 250px;"></div>
        </div>

        <!-- 技术统计信息 -->
        <div class="chart-container p-4">
          <h4 class="font-bold text-white mb-3">技术统计</h4>
          <div class="space-y-3">
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-400">总专利数</span>
              <span class="font-semibold text-white">{{ formatNumber(stats.totalPatent) }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-400">总论文数</span>
              <span class="font-semibold text-white">{{ formatNumber(stats.totalPaper) }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-400">总项目数</span>
              <span class="font-semibold text-white">{{ formatNumber(stats.totalProject) }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-400">国家/地区</span>
              <span class="font-semibold text-white">{{ stats.countryCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="chart-container p-6">
      <h3 class="text-lg font-semibold text-white mb-4">
        {{ dataType === 'project' ? '各资助机构' : '各国/地区' }}数据统计（共 {{ tableData.length }} 个）
      </h3>
      <el-table :data="tableData" stripe style="width: 100%" max-height="400"
                :header-cell-style="{ background: 'rgba(31, 41, 55, 0.8)', color: '#fff' }"
                :cell-style="{ background: 'transparent', color: '#9ca3af' }">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="country" :label="dataType === 'project' ? '资助机构' : '国家/地区'" width="200" />
        <el-table-column prop="count" label="数量" sortable />
        <el-table-column prop="percentage" label="占比">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <div class="flex-1 bg-gray-700 rounded-full h-2">
                <div class="bg-blue-500 h-2 rounded-full transition-all" :style="{ width: row.percentage }"></div>
              </div>
              <span class="text-sm w-16 text-gray-400">{{ row.percentage }}</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/analysis'
import { useCategoryStore } from '@/stores/category'

const categoryStore = useCategoryStore()
const mainChartRef = ref<HTMLElement | null>(null)
const countryChartRef = ref<HTMLElement | null>(null)
const mainContainerRef = ref<HTMLElement | null>(null)
const dataType = ref('patent')
const countryChartType = ref('count')
const chartMode = ref<'bar' | 'map'>('bar')
const tableData = ref<any[]>([])
const loading = ref(false)
const isFullscreen = ref(false)
const selectedCountry = ref<any>(null)
const countriesData = ref<any[]>([])
const geoJsonLoaded = ref(false)

let mainChart: echarts.ECharts | null = null
let countryChart: echarts.ECharts | null = null

const stats = reactive({
  totalPatent: 0,
  totalPaper: 0,
  totalProject: 0,
  countryCount: 0
})

// 中文国家名到英文的映射
const countryNameMap: Record<string, string> = {
  '中国': 'China', '美国': 'United States', '日本': 'Japan', '德国': 'Germany',
  '韩国': 'Korea', '英国': 'United Kingdom', '法国': 'France', '加拿大': 'Canada',
  '澳大利亚': 'Australia', '印度': 'India', '意大利': 'Italy', '西班牙': 'Spain',
  '荷兰': 'Netherlands', '瑞士': 'Switzerland', '瑞典': 'Sweden', '俄罗斯': 'Russia',
  '巴西': 'Brazil', '新加坡': 'Singapore', '以色列': 'Israel', '奥地利': 'Austria',
  '比利时': 'Belgium', '丹麦': 'Denmark', '芬兰': 'Finland', '挪威': 'Norway',
  '波兰': 'Poland', '土耳其': 'Turkey', '墨西哥': 'Mexico', '南非': 'South Africa',
  '阿根廷': 'Argentina', '智利': 'Chile', '新西兰': 'New Zealand', '爱尔兰': 'Ireland',
  '葡萄牙': 'Portugal', '希腊': 'Greece', '捷克': 'Czech Rep.', '匈牙利': 'Hungary',
  '泰国': 'Thailand', '马来西亚': 'Malaysia', '印度尼西亚': 'Indonesia', '越南': 'Vietnam',
  '菲律宾': 'Philippines', '埃及': 'Egypt', '沙特阿拉伯': 'Saudi Arabia', '伊朗': 'Iran',
  '巴基斯坦': 'Pakistan', '孟加拉国': 'Bangladesh', '尼日利亚': 'Nigeria'
}

// 格式化数字
function formatNumber(num: number): string {
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K'
  return num.toString()
}

// 全屏切换
function toggleFullscreen() {
  const container = mainContainerRef.value
  if (!container) return
  
  if (document.fullscreenElement) {
    document.exitFullscreen()
    isFullscreen.value = false
  } else {
    container.requestFullscreen()
    isFullscreen.value = true
  }
}

// 跳转到报告页面
function goToReport() {
  router.push('/report')
}

// 加载世界地图GeoJSON
async function loadWorldMap() {
  if (geoJsonLoaded.value) return true
  
  // 优先从本地加载
  try {
    const response = await fetch('/world.json')
    if (response.ok) {
      const worldJson = await response.json()
      echarts.registerMap('world', worldJson)
      geoJsonLoaded.value = true
      console.log('世界地图加载成功（本地）')
      return true
    }
  } catch (e) {
    console.warn('本地地图加载失败:', e.message)
  }
  
  // 备用CDN
  try {
    const response = await fetch('https://echarts.apache.org/examples/data/asset/geo/world.json')
    if (response.ok) {
      const worldJson = await response.json()
      echarts.registerMap('world', worldJson)
      geoJsonLoaded.value = true
      console.log('世界地图加载成功（CDN）')
      return true
    }
  } catch (e) {
    console.warn('CDN地图加载失败:', e.message)
  }
  
  return false
}

// 加载数据
async function loadData() {
  loading.value = true
  selectedCountry.value = null
  
  await nextTick()
  
  try {
    // 优先使用预加载的缓存数据
    const cache = categoryStore.preloadedData
    let countries: any[] = []
    
    if (cache) {
      // 根据数据类型选择缓存
      if (dataType.value === 'patent' && cache.techMapPatent) {
        console.log('TechMap: 使用专利缓存数据')
        countries = cache.techMapPatent?.countries || []
      } else if (dataType.value === 'paper' && cache.techMapPaper) {
        console.log('TechMap: 使用论文缓存数据')
        countries = cache.techMapPaper?.countries || []
      } else if (dataType.value === 'project' && cache.techMapProject) {
        console.log('TechMap: 使用项目缓存数据')
        countries = cache.techMapProject?.countries || []
      } else {
        // 缓存中没有对应数据，重新请求
        const res = await analysisApi.getTechMap(dataType.value, categoryStore.currentCode || undefined)
        countries = res.data?.countries || []
      }
    } else {
      // 没有缓存，正常请求
      const res = await analysisApi.getTechMap(dataType.value, categoryStore.currentCode || undefined)
      countries = res.data?.countries || []
    }
    
    countriesData.value = countries
    
    const total = countries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    
    if (dataType.value === 'patent') stats.totalPatent = total
    else if (dataType.value === 'paper') stats.totalPaper = total
    else stats.totalProject = total
    stats.countryCount = countries.length
    
    tableData.value = countries.map((c: any) => ({
      country: c.country,
      count: c.count || 0,
      percentage: total > 0 ? ((c.count || 0) / total * 100).toFixed(1) + '%' : '0%'
    }))
    
    loading.value = false
    await nextTick()
    
    setTimeout(() => {
      updateChart()
    }, 50)
  } catch (e) {
    console.error('加载数据失败', e)
    loading.value = false
  }
}

// 监听图表模式切换
watch(chartMode, async () => {
  if (chartMode.value === 'map') {
    const loaded = await loadWorldMap()
    if (!loaded) {
      // 地图加载失败，切换回柱状图
      alert('地图数据加载失败，请稍后重试或使用柱状图模式')
      chartMode.value = 'bar'
      return
    }
  }
  await nextTick()
  updateChart()
})

// 更新图表（根据模式选择柱状图或地图）
function updateChart() {
  if (chartMode.value === 'bar') {
    updateBarChart()
  } else {
    updateMapChart()
  }
}

// 更新柱状图
function updateBarChart() {
  if (!mainChartRef.value) return
  
  if (mainChart) mainChart.dispose()
  mainChart = echarts.init(mainChartRef.value)
  
  const countries = countriesData.value
  const topCountries = countries.slice(0, 20)
  const dataTypeName = dataType.value === 'patent' ? '专利' : dataType.value === 'project' ? '项目' : '论文'
  
  const option: echarts.EChartsOption = {
    backgroundColor: 'transparent',
    title: {
      text: `全球${dataTypeName}分布排行`,
      subtext: `基于 ${countries.length} 个国家/地区的数据分析`,
      left: 'center',
      top: 5,
      textStyle: { color: '#fff', fontSize: 16 },
      subtextStyle: { color: '#94a3b8', fontSize: 11 }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(30, 41, 59, 0.95)',
      borderColor: '#475569',
      textStyle: { color: '#fff' },
      axisPointer: { type: 'shadow' }
    },
    grid: { left: 60, right: 30, bottom: 100, top: 70, containLabel: false },
    xAxis: {
      type: 'category',
      data: topCountries.map((c: any) => c.country),
      axisLine: { lineStyle: { color: '#475569' } },
      axisLabel: {
        color: '#94a3b8', fontSize: 10, rotate: 45, interval: 0,
        formatter: (value: string) => value.length > 8 ? value.substring(0, 8) + '...' : value
      }
    },
    yAxis: {
      type: 'value',
      name: dataTypeName + '数量',
      nameTextStyle: { color: '#94a3b8', fontSize: 11 },
      axisLine: { lineStyle: { color: '#475569' } },
      axisLabel: { 
        color: '#94a3b8', fontSize: 10,
        formatter: (value: number) => value >= 10000 ? (value / 10000).toFixed(0) + 'w' : value >= 1000 ? (value / 1000).toFixed(0) + 'k' : String(value)
      },
      splitLine: { lineStyle: { color: '#334155' } }
    },
    dataZoom: [{ type: 'inside', start: 0, end: 100 }],
    series: [{
      name: dataTypeName,
      type: 'bar',
      data: topCountries.map((c: any) => ({
        value: c.count,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#3b82f6' },
            { offset: 1, color: '#1d4ed8' }
          ])
        }
      })),
      barWidth: '50%',
      barMaxWidth: 40
    }]
  }
  
  mainChart.setOption(option)
  bindChartClick(topCountries)
}

// 更新地图
function updateMapChart() {
  if (!mainChartRef.value) return
  
  if (mainChart) mainChart.dispose()
  mainChart = echarts.init(mainChartRef.value)
  
  const countries = countriesData.value
  const dataTypeName = dataType.value === 'patent' ? '专利' : dataType.value === 'project' ? '项目' : '论文'
  
  // 转换数据为地图格式
  const mapData = countries.map((c: any) => {
    const englishName = countryNameMap[c.country] || c.country
    return {
      name: englishName,
      value: c.count || 0,
      originalName: c.country
    }
  })
  
  const values = mapData.map((d: any) => d.value).filter((v: number) => v > 0)
  const maxValue = Math.max(...values, 1)
  
  const option: echarts.EChartsOption = {
    backgroundColor: 'transparent',
    title: {
      text: `全球${dataTypeName}分布地图`,
      subtext: `基于 ${countries.length} 个国家/地区的数据分析`,
      left: 'center',
      top: 5,
      textStyle: { color: '#fff', fontSize: 16 },
      subtextStyle: { color: '#94a3b8', fontSize: 11 }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(30, 41, 59, 0.95)',
      borderColor: '#475569',
      textStyle: { color: '#fff' },
      formatter: (params: any) => {
        if (params.data) {
          return `<div style="font-weight: bold;">${params.data.originalName || params.name}</div>
                  <div>${dataTypeName}数量: ${params.data.value || 0}</div>`
        }
        return params.name
      }
    },
    visualMap: {
      min: 0,
      max: maxValue,
      left: 'left',
      top: 'bottom',
      text: ['高', '低'],
      textStyle: { color: '#9ca3af' },
      inRange: {
        color: ['#1e3a5f', '#2563eb', '#3b82f6', '#60a5fa', '#93c5fd']
      },
      calculable: true
    },
    geo: {
      map: 'world',
      roam: true,
      zoom: 1.2,
      center: [10, 30],
      itemStyle: {
        areaColor: '#1f2937',
        borderColor: '#374151',
        borderWidth: 0.5
      },
      emphasis: {
        itemStyle: {
          areaColor: '#3b82f6',
          shadowBlur: 10,
          shadowColor: 'rgba(59, 130, 246, 0.5)'
        },
        label: {
          show: false
        }
      },
      select: {
        itemStyle: {
          areaColor: '#2563eb'
        }
      }
    },
    series: [{
      name: dataTypeName,
      type: 'map',
      geoIndex: 0,
      data: mapData
    }]
  }
  
  mainChart.setOption(option)
  
  // 绑定点击事件
  mainChart.off('click')
  mainChart.on('click', (params: any) => {
    if (params.data) {
      const country = countries.find((c: any) => 
        c.country === params.data.originalName || 
        countryNameMap[c.country] === params.name
      )
      if (country) {
        selectedCountry.value = country
        nextTick(() => updateCountryChart())
      }
    }
  })
}

// 绑定图表点击事件
function bindChartClick(topCountries: any[]) {
  if (!mainChart) return
  mainChart.off('click')
  mainChart.on('click', (params: any) => {
    const country = topCountries.find((c: any) => c.country === params.name)
    if (country) {
      selectedCountry.value = country
      nextTick(() => updateCountryChart())
    }
  })
}

// 更新国家分析图表
function updateCountryChart() {
  if (!selectedCountry.value) return
  
  if (!countryChart && countryChartRef.value) {
    countryChart = echarts.init(countryChartRef.value)
  }
  if (!countryChart) return
  
  const country = selectedCountry.value
  const dataTypeName = dataType.value === 'patent' ? '专利' : dataType.value === 'project' ? '项目' : '论文'
  
  let option: echarts.EChartsOption
  
  if (countryChartType.value === 'quality') {
    // 质量指标模式：显示饼图，展示该国家在全球的占比和排名信息
    const total = countriesData.value.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    const percentage = total > 0 ? ((country.count || 0) / total * 100) : 0
    const rank = countriesData.value.findIndex((c: any) => c.country === country.country) + 1
    
    option = {
      backgroundColor: 'transparent',
      title: {
        text: `${country.country} - 质量指标`,
        left: 'center',
        top: 5,
        textStyle: { color: '#fff', fontSize: 14 }
      },
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(30, 41, 59, 0.95)',
        borderColor: '#475569',
        textStyle: { color: '#fff' },
        formatter: '{b}: {c} ({d}%)'
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '60%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#1f2937',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'center',
          formatter: () => `第${rank}名\n${percentage.toFixed(1)}%`,
          fontSize: 14,
          fontWeight: 'bold',
          color: '#fff',
          lineHeight: 20
        },
        data: [
          { value: country.count || 0, name: country.country, itemStyle: { color: '#3b82f6' } },
          { value: total - (country.count || 0), name: '其他国家', itemStyle: { color: '#374151' } }
        ]
      }]
    }
  } else {
    // 数量分布模式：显示柱状图
    option = {
      backgroundColor: 'transparent',
      title: {
        text: `${country.country} - 数量分布`,
        left: 'center',
        top: 5,
        textStyle: { color: '#fff', fontSize: 14 }
      },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(30, 41, 59, 0.95)',
        borderColor: '#475569',
        textStyle: { color: '#fff' }
      },
      grid: { left: '10%', right: '10%', bottom: '15%', top: '25%' },
      xAxis: {
        type: 'category',
        data: [dataTypeName],
        axisLine: { lineStyle: { color: '#475569' } },
        axisLabel: { color: '#94a3b8', fontSize: 11 }
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: '#475569' } },
        axisLabel: { color: '#94a3b8', fontSize: 10 },
        splitLine: { lineStyle: { color: '#334155' } }
      },
      series: [{
        type: 'bar',
        data: [{ value: country.count || 0, itemStyle: { color: '#3b82f6' } }],
        barWidth: '50%',
        label: { show: true, position: 'top', color: '#fff', fontSize: 11 }
      }]
    }
  }
  
  countryChart.setOption(option, true)
  countryChart.resize()
}

// 加载统计数据
async function loadStats() {
  try {
    const [patentRes, paperRes, projectRes] = await Promise.all([
      analysisApi.getTechMap('patent'),
      analysisApi.getTechMap('paper'),
      analysisApi.getTechMap('project')
    ])
    
    const patentCountries = patentRes.data?.countries || []
    const paperCountries = paperRes.data?.countries || []
    const projectCountries = projectRes.data?.countries || []
    
    stats.totalPatent = patentCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    stats.totalPaper = paperCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    stats.totalProject = projectCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    
    const allCountries = new Set([
      ...patentCountries.map((c: any) => c.country),
      ...paperCountries.map((c: any) => c.country),
      ...projectCountries.map((c: any) => c.country)
    ])
    stats.countryCount = allCountries.size
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

function handleResize() {
  mainChart?.resize()
  countryChart?.resize()
}

onMounted(async () => {
  await nextTick()
  await loadData()
  await loadStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  mainChart?.dispose()
  countryChart?.dispose()
})
</script>

<style scoped>
.chart-container {
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  border: 1px solid #374151;
  border-radius: 0.75rem;
}

/* 地图主题加载动画 */
.globe-loader {
  position: relative;
  width: 120px;
  height: 120px;
}

.globe {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 3rem;
  animation: float 2s ease-in-out infinite;
}

.ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 2px solid rgba(59, 130, 246, 0.5);
  border-radius: 50%;
  animation: ring-pulse 2s ease-out infinite;
}

.ring-1 { width: 70px; height: 70px; }
.ring-2 { width: 90px; height: 90px; animation-delay: 0.3s; }
.ring-3 { width: 110px; height: 110px; animation-delay: 0.6s; }

.pulse-dot {
  position: absolute;
  width: 8px;
  height: 8px;
  background: #3b82f6;
  border-radius: 50%;
  animation: orbit 3s linear infinite;
}

.pulse-dot:nth-child(5) { top: 50%; left: 0; }
.pulse-dot.delay-1 { animation-delay: -1s; }
.pulse-dot.delay-2 { animation-delay: -2s; }

@keyframes float {
  0%, 100% { transform: translate(-50%, -50%) translateY(0); }
  50% { transform: translate(-50%, -50%) translateY(-8px); }
}

@keyframes ring-pulse {
  0% { transform: translate(-50%, -50%) scale(0.8); opacity: 1; }
  100% { transform: translate(-50%, -50%) scale(1.3); opacity: 0; }
}

@keyframes orbit {
  from { transform: rotate(0deg) translateX(55px) rotate(0deg); }
  to { transform: rotate(360deg) translateX(55px) rotate(-360deg); }
}

select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%239ca3af' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 0.5rem center;
  background-repeat: no-repeat;
  background-size: 1.5em 1.5em;
  padding-right: 2.5rem;
}

select option {
  background-color: #1f2937;
  color: #ffffff;
}
</style>

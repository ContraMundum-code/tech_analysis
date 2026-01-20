<template>
  <div class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-gray-700/50">
    <div class="flex items-center justify-between mb-4">
      <div>
        <h3 class="text-lg font-semibold text-white flex items-center">
          <svg class="w-5 h-5 mr-2 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
          </svg>
          技术关联网络
        </h3>
        <p class="text-gray-400 text-sm mt-1">展示技术关键词之间的共现关系，节点大小表示出现频次，连线粗细表示共现次数</p>
      </div>
      <!-- 可交互的分类筛选器 -->
      <div class="flex items-center space-x-4 text-sm">
        <label class="flex items-center cursor-pointer select-none" @click="toggleCategory('core')">
          <span class="w-4 h-4 rounded border-2 mr-2 flex items-center justify-center transition-all"
                :class="categoryVisible.core ? 'bg-cyan-500 border-cyan-500' : 'border-gray-500 bg-transparent'">
            <svg v-if="categoryVisible.core" class="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
            </svg>
          </span>
          <span :class="categoryVisible.core ? 'text-cyan-400' : 'text-gray-500'">核心技术</span>
        </label>
        <label class="flex items-center cursor-pointer select-none" @click="toggleCategory('related')">
          <span class="w-4 h-4 rounded border-2 mr-2 flex items-center justify-center transition-all"
                :class="categoryVisible.related ? 'bg-green-500 border-green-500' : 'border-gray-500 bg-transparent'">
            <svg v-if="categoryVisible.related" class="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
            </svg>
          </span>
          <span :class="categoryVisible.related ? 'text-green-400' : 'text-gray-500'">关联技术</span>
        </label>
        <label class="flex items-center cursor-pointer select-none" @click="toggleCategory('peripheral')">
          <span class="w-4 h-4 rounded border-2 mr-2 flex items-center justify-center transition-all"
                :class="categoryVisible.peripheral ? 'bg-amber-500 border-amber-500' : 'border-gray-500 bg-transparent'">
            <svg v-if="categoryVisible.peripheral" class="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
            </svg>
          </span>
          <span :class="categoryVisible.peripheral ? 'text-amber-400' : 'text-gray-500'">边缘技术</span>
        </label>
      </div>
    </div>
    
    <!-- 国家筛选器 -->
    <div class="mb-4 flex items-center gap-4">
      <label class="text-gray-300 text-sm">选择国家/地区：</label>
      <select 
        v-model="selectedCountry" 
        @change="loadNetworkData"
        class="bg-gray-700 border border-gray-600 text-white text-sm rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500 min-w-[150px]"
      >
        <option value="">全部国家</option>
        <option v-for="c in countries" :key="c" :value="c">{{ c }}</option>
      </select>
      <span v-if="selectedCountry" class="text-gray-400 text-sm">
        当前显示: {{ selectedCountry }} 的技术关联网络
      </span>
    </div>
    
    <div v-if="loading" class="h-[650px] flex items-center justify-center relative network-container">
      <div class="flex flex-col items-center gap-4">
        <div class="network-loader">
          <div class="node node-center"></div>
          <div class="node node-1"></div>
          <div class="node node-2"></div>
          <div class="node node-3"></div>
          <div class="node node-4"></div>
          <div class="node node-5"></div>
          <svg class="connections" viewBox="0 0 100 100">
            <line class="conn conn-1" x1="50" y1="50" x2="50" y2="15"/>
            <line class="conn conn-2" x1="50" y1="50" x2="83" y2="30"/>
            <line class="conn conn-3" x1="50" y1="50" x2="83" y2="70"/>
            <line class="conn conn-4" x1="50" y1="50" x2="50" y2="85"/>
            <line class="conn conn-5" x1="50" y1="50" x2="17" y2="70"/>
            <line class="conn conn-6" x1="50" y1="50" x2="17" y2="30"/>
          </svg>
        </div>
        <span class="text-gray-400 animate-pulse">正在构建技术关联网络...</span>
      </div>
    </div>
    <div v-show="!loading" class="relative">
      <div ref="networkRef" class="h-[650px] rounded-xl bg-gradient-to-br from-gray-900/80 via-gray-800/50 to-gray-900/80 network-container"></div>
      <!-- 交互提示 -->
      <div class="absolute bottom-4 left-4 flex items-center gap-4 text-xs text-gray-500">
        <span class="flex items-center gap-1">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 15l-2 5L9 9l11 4-5 2zm0 0l5 5M7.188 2.239l.777 2.897M5.136 7.965l-2.898-.777M13.95 4.05l-2.122 2.122m-5.657 5.656l-2.12 2.122" />
          </svg>
          拖拽移动节点
        </span>
        <span class="flex items-center gap-1">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM10 7v3m0 0v3m0-3h3m-3 0H7" />
          </svg>
          滚轮缩放
        </span>
        <span class="flex items-center gap-1">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
          </svg>
          悬停查看详情
        </span>
      </div>
      <!-- 节点统计 -->
      <div v-if="nodeStats.total > 0" class="absolute top-4 right-4 bg-gray-800/90 backdrop-blur-sm rounded-lg px-4 py-3 text-sm border border-gray-700/50">
        <div class="text-gray-400 mb-2">当前显示</div>
        <div class="flex items-center gap-4">
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-cyan-400"></span>
            <span class="text-white">{{ nodeStats.core }}</span>
            <span class="text-gray-500">核心</span>
          </span>
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-green-400"></span>
            <span class="text-white">{{ nodeStats.related }}</span>
            <span class="text-gray-500">关联</span>
          </span>
          <span class="flex items-center gap-1.5">
            <span class="w-2.5 h-2.5 rounded-full bg-amber-400"></span>
            <span class="text-white">{{ nodeStats.peripheral }}</span>
            <span class="text-gray-500">边缘</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { analysisApi } from '@/api/analysis'
import { useCategoryStore } from '@/stores/category'

const categoryStore = useCategoryStore()
const networkRef = ref<HTMLElement>()
const loading = ref(true)
const selectedCountry = ref('')
const countries = ref<string[]>([])

// 节点统计
const nodeStats = computed(() => {
  if (rawNodes.length === 0) return { total: 0, core: 0, related: 0, peripheral: 0 }
  
  const visibleNodes = rawNodes.filter(n => {
    if (n.category === 'core') return categoryVisible.core
    if (n.category === 'related') return categoryVisible.related
    return categoryVisible.peripheral
  })
  
  return {
    total: visibleNodes.length,
    core: visibleNodes.filter(n => n.category === 'core').length,
    related: visibleNodes.filter(n => n.category === 'related').length,
    peripheral: visibleNodes.filter(n => n.category === 'peripheral' || n.category === 'info').length
  }
})

// 分类可见性状态
const categoryVisible = reactive({
  core: true,
  related: true,
  peripheral: true
})

// 原始数据缓存
let rawNodes: any[] = []
let rawLinks: any[] = []
let chart: echarts.ECharts | null = null

// 切换分类显示
function toggleCategory(category: 'core' | 'related' | 'peripheral') {
  categoryVisible[category] = !categoryVisible[category]
  updateChartVisibility()
}

// 更新图表显示
function updateChartVisibility() {
  if (!chart || rawNodes.length === 0) return
  
  // 根据可见性筛选节点
  const filteredNodes = rawNodes.filter(n => {
    if (n.category === 'core') return categoryVisible.core
    if (n.category === 'related') return categoryVisible.related
    return categoryVisible.peripheral
  })
  
  const visibleNodeIds = new Set(filteredNodes.map(n => n.id))
  
  // 筛选连线（两端节点都可见才显示）
  const filteredLinks = rawLinks.filter(l => 
    visibleNodeIds.has(l.source) && visibleNodeIds.has(l.target)
  )
  
  // 如果没有可见节点，显示提示
  if (filteredNodes.length === 0) {
    chart.setOption({
      graphic: [{
        type: 'text',
        left: 'center',
        top: 'middle',
        style: {
          text: '请至少选择一个分类显示',
          fontSize: 16,
          fill: '#9ca3af'
        }
      }],
      series: [{
        data: [],
        links: []
      }]
    }, true)
    return
  }
  
  renderChart(filteredNodes, filteredLinks)
}

// 加载国家列表
async function loadCountries() {
  try {
    // 优先使用缓存
    const cache = categoryStore.preloadedData
    let countryData: any[] = []
    
    if (cache && cache.techMapPatent) {
      console.log('ThreeLayers: 使用缓存加载国家列表')
      countryData = cache.techMapPatent?.countries || []
    } else {
      const res = await analysisApi.getTechMap('patent', categoryStore.currentCode || undefined)
      countryData = res.data?.countries || []
    }
    
    countries.value = countryData.map((c: any) => c.country).filter((c: string) => c)
  } catch (e) {
    console.error('加载国家列表失败', e)
  }
}

// 加载网络数据
async function loadNetworkData() {
  loading.value = true
  
  try {
    // 优先使用缓存（仅当未选择国家时）
    const cache = categoryStore.preloadedData
    let nodes: any[] = []
    let links: any[] = []
    
    if (cache && cache.network && !selectedCountry.value) {
      console.log('ThreeLayers: 使用网络缓存数据')
      nodes = cache.network?.nodes || []
      links = cache.network?.links || []
    } else {
      const res = await analysisApi.getNetwork(selectedCountry.value || undefined, categoryStore.currentCode || undefined)
      const data = res.data || {}
      nodes = data.nodes || []
      links = data.links || []
    }
    
    // 缓存原始数据
    rawNodes = nodes
    rawLinks = links
    
    loading.value = false
    await nextTick()
    
    if (!networkRef.value) return
    
    if (!chart) {
      chart = echarts.init(networkRef.value)
    }
    
    // 根据当前可见性筛选
    updateChartVisibility()
    
  } catch (e) {
    console.error('加载技术关系网络失败', e)
    loading.value = false
  }
}

function renderChart(nodes: any[], links: any[]) {
  if (!chart) return
  
  // 技术关系网络的分类颜色 - 使用渐变色系
  const categoryColors: Record<string, { main: string; light: string; glow: string }> = {
    core: { main: '#06b6d4', light: '#22d3ee', glow: 'rgba(6, 182, 212, 0.6)' },      // cyan - 核心技术
    related: { main: '#22c55e', light: '#4ade80', glow: 'rgba(34, 197, 94, 0.5)' },   // green - 关联技术
    peripheral: { main: '#f59e0b', light: '#fbbf24', glow: 'rgba(245, 158, 11, 0.4)' } // amber - 边缘技术
  }
  
  const categories = [
    { name: '核心技术' },
    { name: '关联技术' },
    { name: '边缘技术' }
  ]
  
  const categoryMap: Record<string, number> = { 
    core: 0,
    related: 1,
    peripheral: 2,
    info: 2
  }
  
  // 计算节点大小范围
  const values = nodes.map((n: any) => n.value || 1)
  const maxValue = Math.max(...values, 1)
  const minValue = Math.min(...values, 1)
  
  // 计算连线强度范围
  const linkValues = links.map((l: any) => l.value || 1)
  const maxLinkValue = Math.max(...linkValues, 1)
  
  const graphData = nodes.map((n: any) => {
    // 根据值计算节点大小 (10-30)，使用平方根使差异更平滑
    const normalizedValue = maxValue > minValue 
      ? (n.value - minValue) / (maxValue - minValue)
      : 0.5
    const normalizedSize = 10 + Math.sqrt(normalizedValue) * 20
    
    const catIndex = categoryMap[n.category] ?? 2
    const colorSet = categoryColors[n.category] || categoryColors.peripheral
    
    return {
      id: n.id,
      name: n.name,
      value: n.value,
      symbolSize: normalizedSize,
      category: catIndex,
      itemStyle: {
        color: {
          type: 'radial',
          x: 0.5,
          y: 0.5,
          r: 0.5,
          colorStops: [
            { offset: 0, color: colorSet.light },
            { offset: 0.7, color: colorSet.main },
            { offset: 1, color: colorSet.main }
          ]
        },
        borderColor: 'rgba(255, 255, 255, 0.3)',
        borderWidth: 1,
        shadowBlur: normalizedSize > 20 ? 10 : 5,
        shadowColor: colorSet.glow
      },
      label: {
        show: catIndex === 0 || normalizedSize > 25,
        fontSize: catIndex === 0 ? 11 : 9,
        fontWeight: catIndex === 0 ? 'bold' : 'normal'
      }
    }
  })
  
  // 连线样式优化
  const graphLinks = links.map((l: any) => {
    const linkStrength = l.value || 1
    const normalizedWidth = Math.min(Math.max(0.5, Math.log2(linkStrength) * 0.6 + 0.5), 3)
    const normalizedOpacity = 0.25 + (linkStrength / maxLinkValue) * 0.35
    
    return {
      source: l.source,
      target: l.target,
      value: linkStrength,
      lineStyle: {
        width: normalizedWidth,
        opacity: normalizedOpacity,
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 1, y2: 0,
          colorStops: [
            { offset: 0, color: 'rgba(99, 102, 241, 0.5)' },
            { offset: 0.5, color: 'rgba(139, 92, 246, 0.4)' },
            { offset: 1, color: 'rgba(99, 102, 241, 0.5)' }
          ]
        },
        curveness: 0.15 + Math.random() * 0.1
      }
    }
  })
  
  const option: echarts.EChartsOption = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: '#374151',
      textStyle: { color: '#e5e7eb' },
      formatter: (params: any) => {
        if (params.dataType === 'node') {
          const catName = categories[params.data.category]?.name || '技术'
          const catColors = ['#06b6d4', '#22c55e', '#f59e0b']
          const color = catColors[params.data.category] || '#f59e0b'
          return `<div style="font-weight: bold; font-size: 14px; margin-bottom: 8px; color: ${color};">${params.data.name}</div>
                  <div style="color: #9ca3af; margin-bottom: 4px;">类型: <span style="color: #fff;">${catName}</span></div>
                  <div style="color: #9ca3af;">出现频次: <span style="color: #60a5fa; font-weight: bold; font-size: 16px;">${params.data.value || 0}</span></div>`
        } else if (params.dataType === 'edge') {
          return `<div style="padding: 4px;">
                    <div style="color: #a78bfa; font-weight: bold; margin-bottom: 4px;">技术关联</div>
                    <div style="color: #9ca3af;">共现次数: <span style="color: #fff; font-size: 14px;">${params.data.value || 1}</span></div>
                  </div>`
        }
        return ''
      }
    },
    // 隐藏ECharts内置图例，使用自定义的
    legend: {
      show: false
    },
    animationDuration: 1500,
    animationEasing: 'elasticOut',
    animationEasingUpdate: 'quinticInOut',
    animationDelayUpdate: (idx: number) => idx * 5,
    series: [{
      type: 'graph',
      layout: 'force',
      data: graphData,
      links: graphLinks,
      categories: categories.map((c, i) => ({
        name: c.name,
        itemStyle: {
          color: i === 0 ? '#06b6d4' : i === 1 ? '#22c55e' : '#f59e0b'
        }
      })),
      roam: true,
      draggable: true,
      zoom: 1.2,
      scaleLimit: {
        min: 0.5,
        max: 3
      },
      label: {
        show: true,
        position: 'bottom',
        distance: 8,
        fontSize: 11,
        color: '#e5e7eb',
        backgroundColor: 'rgba(17, 24, 39, 0.85)',
        padding: [4, 8],
        borderRadius: 4,
        borderColor: 'rgba(255, 255, 255, 0.1)',
        borderWidth: 1,
        formatter: (params: any) => {
          // 只显示核心技术或大节点的标签
          if (params.data.category === 0 || params.data.symbolSize > 40) {
            const name = params.data.name
            return name.length > 8 ? name.substring(0, 8) + '...' : name
          }
          return ''
        }
      },
      emphasis: {
        focus: 'adjacency',
        blurScope: 'coordinateSystem',
        label: {
          show: true,
          fontSize: 13,
          fontWeight: 'bold',
          color: '#fff',
          formatter: '{b}'
        },
        itemStyle: {
          shadowBlur: 25,
          shadowColor: 'rgba(99, 102, 241, 0.8)',
          borderWidth: 3,
          borderColor: '#fff'
        },
        lineStyle: {
          width: 4,
          opacity: 1,
          color: '#a78bfa'
        }
      },
      blur: {
        itemStyle: {
          opacity: 0.3
        },
        lineStyle: {
          opacity: 0.1
        }
      },
      select: {
        label: {
          show: true,
          color: '#fff'
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 3
        }
      },
      selectedMode: 'single',
      force: {
        initLayout: 'circular',
        repulsion: [300, 600],
        gravity: 0.05,
        edgeLength: [60, 180],
        friction: 0.65,
        layoutAnimation: true
      },
      lineStyle: {
        curveness: 0.25
      },
      labelLayout: {
        hideOverlap: true
      }
    }]
  }
  
  chart.setOption(option, true)
}

function handleResize() {
  chart?.resize()
}

onMounted(async () => {
  await loadCountries()
  await loadNetworkData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
/* 网络主题加载动画 */
.network-loader {
  position: relative;
  width: 100px;
  height: 100px;
}

.network-loader .node {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: node-pulse 1.5s ease-in-out infinite;
}

.node-center {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 16px;
  height: 16px;
  background: #06b6d4;
  box-shadow: 0 0 20px rgba(6, 182, 212, 0.6);
}

.node-1 { top: 5%; left: 50%; transform: translateX(-50%); background: #22c55e; animation-delay: 0s; }
.node-2 { top: 20%; right: 8%; background: #f59e0b; animation-delay: 0.2s; }
.node-3 { bottom: 20%; right: 8%; background: #22c55e; animation-delay: 0.4s; }
.node-4 { bottom: 5%; left: 50%; transform: translateX(-50%); background: #f59e0b; animation-delay: 0.6s; }
.node-5 { bottom: 20%; left: 8%; background: #06b6d4; animation-delay: 0.8s; }

.connections {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.conn {
  stroke: rgba(99, 102, 241, 0.4);
  stroke-width: 1.5;
  stroke-dasharray: 50;
  animation: dash 2s linear infinite;
}

.conn-1 { animation-delay: 0s; }
.conn-2 { animation-delay: 0.3s; }
.conn-3 { animation-delay: 0.6s; }
.conn-4 { animation-delay: 0.9s; }
.conn-5 { animation-delay: 1.2s; }
.conn-6 { animation-delay: 1.5s; }

@keyframes node-pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  50% { transform: translate(-50%, -50%) scale(1.3); opacity: 0.7; }
}

.node-1, .node-2, .node-3, .node-4, .node-5 {
  animation-name: node-outer-pulse;
}

@keyframes node-outer-pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
}

@keyframes dash {
  to { stroke-dashoffset: -100; }
}

.network-container {
  box-shadow: 
    inset 0 0 60px rgba(6, 182, 212, 0.03),
    inset 0 0 120px rgba(34, 197, 94, 0.02);
  position: relative;
  overflow: hidden;
}

.network-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(6, 182, 212, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(34, 197, 94, 0.06) 0%, transparent 40%),
    radial-gradient(circle at 50% 50%, rgba(139, 92, 246, 0.04) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 动态光效 */
.network-container::after {
  content: '';
  position: absolute;
  inset: -50%;
  background: conic-gradient(
    from 0deg,
    transparent 0%,
    rgba(99, 102, 241, 0.03) 10%,
    transparent 20%
  );
  animation: rotate-glow 20s linear infinite;
  pointer-events: none;
  z-index: 0;
}

@keyframes rotate-glow {
  to {
    transform: rotate(360deg);
  }
}
</style>

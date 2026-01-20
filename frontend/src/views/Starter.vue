<template>
  <div class="min-h-screen bg-gray-900">
    <!-- 顶部导航栏 -->
    <header class="fixed top-0 left-0 right-0 z-50 bg-gray-900/80 backdrop-blur-lg border-b border-gray-700/50">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex h-16 items-center justify-between">
          <!-- Logo -->
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center shadow-lg">
              <span class="text-white text-lg">🔬</span>
            </div>
            <h1 class="text-white text-xl font-bold">技术解析平台</h1>
          </div>
          
          <!-- 右侧操作 -->
          <div class="flex items-center gap-4">
            <template v-if="userStore.isLoggedIn">
              <!-- 当前分类显示 -->
              <div v-if="categoryStore.hasCategory" class="hidden md:flex items-center gap-2 px-3 py-1 rounded-lg bg-blue-500/20 border border-blue-500/30">
                <span class="text-lg">{{ categoryStore.icon }}</span>
                <span class="text-blue-400 text-sm font-medium">{{ categoryStore.currentName }}</span>
                <button @click="goToClassification" class="text-blue-400 hover:text-blue-300 text-xs ml-1">切换</button>
              </div>
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center text-white text-sm font-medium">
                  {{ userStore.username?.charAt(0)?.toUpperCase() || 'U' }}
                </div>
                <span class="text-white text-sm hidden sm:block">{{ userStore.username }}</span>
                <button @click="handleLogout" class="text-gray-400 hover:text-red-400 transition-colors" title="退出登录">
                  🚪
                </button>
              </div>
            </template>
            <template v-else>
              <router-link to="/login" class="text-gray-300 hover:text-white transition-colors text-sm">登录</router-link>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="relative flex items-center justify-center overflow-hidden pt-16 pb-8" :class="userStore.isLoggedIn ? 'min-h-[50vh]' : 'min-h-screen'">
      <!-- Animated Background -->
      <div class="absolute inset-0 bg-gradient-to-br from-gray-900 via-blue-900/20 to-gray-900"></div>
      
      <!-- Particle Effect -->
      <div class="absolute inset-0">
        <div v-for="i in 50" :key="i" 
             class="particle absolute rounded-full bg-blue-500/30"
             :style="getParticleStyle(i)">
        </div>
      </div>
      
      <!-- Gradient Overlay -->
      <div class="absolute inset-0 bg-gradient-to-b from-gray-900/80 via-gray-900/60 to-gray-900/90"></div>
      
      <!-- Content -->
      <div class="relative z-10 container mx-auto px-4 text-center">
        <h1 class="text-4xl sm:text-5xl lg:text-6xl font-bold text-white mb-6 leading-tight">
          领先的<span class="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">技术解析平台</span>
        </h1>
        <p class="text-xl sm:text-2xl text-gray-200 mb-8 leading-relaxed max-w-3xl mx-auto">
          深耕全球技术创新前沿，揭秘专利、论文、项目背后的创新密码
        </p>
        
        <!-- 未登录显示更多描述 -->
        <p v-if="!userStore.isLoggedIn" class="text-lg text-gray-300 mb-12 max-w-3xl mx-auto">
          为技术研究、投资决策和战略合作提供专业的数据支持和智能分析工具
        </p>
        
        <!-- Action Buttons -->
        <div class="flex flex-col sm:flex-row items-center justify-center gap-4 sm:gap-6">
          <template v-if="userStore.isLoggedIn">
            <router-link to="/classification" 
                         class="btn-primary rounded-lg px-8 py-4 text-base font-semibold text-white shadow-lg hover:shadow-xl transition-all duration-300 flex items-center gap-2">
              <span>🔬</span>
              选择技术分类
            </router-link>
          </template>
          <template v-else>
            <router-link to="/login" 
                         class="btn-primary rounded-lg px-8 py-4 text-base font-semibold text-white shadow-lg hover:shadow-xl transition-all duration-300 flex items-center gap-2">
              <span>🚀</span>
              开始探索
            </router-link>
          </template>
        </div>
        
        <!-- Stats (简化版) -->
        <div v-if="!userStore.isLoggedIn" class="grid grid-cols-2 md:grid-cols-4 gap-8 mt-16 pt-8 border-t border-white/20">
          <div class="text-center">
            <div class="text-3xl font-bold text-white mb-2">{{ formatNumber(stats.patents) }}</div>
            <div class="text-gray-300 text-sm">技术专利</div>
          </div>
          <div class="text-center">
            <div class="text-3xl font-bold text-white mb-2">{{ formatNumber(stats.papers) }}</div>
            <div class="text-gray-300 text-sm">科研论文</div>
          </div>
          <div class="text-center">
            <div class="text-3xl font-bold text-white mb-2">{{ formatNumber(stats.projects) }}</div>
            <div class="text-gray-300 text-sm">研究项目</div>
          </div>
          <div class="text-center">
            <div class="text-3xl font-bold text-white mb-2">{{ stats.countries }}+</div>
            <div class="text-gray-300 text-sm">国家覆盖</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 登录后显示仪表盘内容 -->
    <section v-if="userStore.isLoggedIn" class="py-8 bg-gray-900">
      <div class="container mx-auto px-4 space-y-6">
        <!-- 当前分类快捷入口 -->
        <div v-if="categoryStore.hasCategory" class="bg-gradient-to-r from-blue-500/20 to-purple-500/20 backdrop-blur-sm p-6 rounded-xl border border-blue-500/30">
          <div class="flex flex-wrap items-center justify-between gap-4">
            <div class="flex items-center gap-4">
              <span class="text-4xl">{{ categoryStore.icon }}</span>
              <div>
                <h2 class="text-xl font-bold text-white">{{ categoryStore.currentName }}</h2>
                <p class="text-gray-400 text-sm mt-1">{{ categoryStore.currentCode }} · 当前正在分析该技术领域</p>
              </div>
            </div>
            <div class="flex flex-wrap items-center gap-3">
              <router-link to="/techmap" class="py-2 px-4 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition-all text-sm flex items-center gap-2">
                🗺️ 技术图谱
              </router-link>
              <router-link to="/trend" class="py-2 px-4 rounded-lg bg-green-600 text-white hover:bg-green-700 transition-all text-sm flex items-center gap-2">
                📈 趋势分析
              </router-link>
              <router-link to="/network" class="py-2 px-4 rounded-lg bg-purple-600 text-white hover:bg-purple-700 transition-all text-sm flex items-center gap-2">
                🔗 关联网络
              </router-link>
              <router-link to="/report" class="py-2 px-4 rounded-lg bg-orange-600 text-white hover:bg-orange-700 transition-all text-sm flex items-center gap-2">
                📝 分析报告
              </router-link>
            </div>
          </div>
        </div>

        <!-- 统计卡片 -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div class="stat-card">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-400 text-sm">专利总数</p>
                <p class="text-3xl font-bold text-white mt-1">{{ stats.patents.toLocaleString() }}</p>
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
                <p class="text-3xl font-bold text-white mt-1">{{ stats.projects.toLocaleString() }}</p>
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
                <p class="text-3xl font-bold text-white mt-1">{{ stats.papers.toLocaleString() }}</p>
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
                <p class="text-3xl font-bold text-white mt-1">{{ stats.countries }}</p>
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
    </section>

    <!-- 未登录显示功能介绍 -->
    <section v-if="!userStore.isLoggedIn" id="features" class="py-20 bg-gray-800/50">
      <div class="container mx-auto px-4">
        <h2 class="text-3xl font-bold text-white text-center mb-12">核心功能</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="feature in features" :key="feature.title" 
               class="feature-card p-6 rounded-xl bg-gray-800/80 border border-gray-700 hover:border-blue-500/50 transition-all duration-300">
            <div class="text-4xl mb-4">{{ feature.icon }}</div>
            <h3 class="text-lg font-semibold text-white mb-2">{{ feature.title }}</h3>
            <p class="text-gray-400 text-sm">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-gray-900 border-t border-gray-800 py-12">
      <div class="container mx-auto px-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div class="md:col-span-2">
            <div class="flex items-center gap-3 mb-4">
              <span class="text-2xl">🔬</span>
              <span class="text-xl font-bold text-white">技术解析平台</span>
            </div>
            <p class="text-gray-400 text-sm leading-relaxed">
              领先的技术解析平台，整合全球专利、论文、项目数据，运用AI技术为研究机构、企业和投资者提供深度洞察和决策支持。
            </p>
          </div>
          <div>
            <h3 class="text-sm font-semibold text-white mb-4">核心功能</h3>
            <ul class="space-y-2">
              <li><router-link to="/techmap" class="text-sm text-gray-400 hover:text-blue-400 transition-colors">技术图谱分析</router-link></li>
              <li><router-link to="/network" class="text-sm text-gray-400 hover:text-blue-400 transition-colors">关联网络</router-link></li>
              <li><router-link to="/trend" class="text-sm text-gray-400 hover:text-blue-400 transition-colors">趋势分析</router-link></li>
              <li><router-link to="/report" class="text-sm text-gray-400 hover:text-blue-400 transition-colors">智能报告</router-link></li>
            </ul>
          </div>
          <div>
            <h3 class="text-sm font-semibold text-white mb-4">联系我们</h3>
            <ul class="space-y-2">
              <li><a href="#" class="text-sm text-gray-400 hover:text-blue-400 transition-colors">技术支持</a></li>
              <li><a href="#" class="text-sm text-gray-400 hover:text-blue-400 transition-colors">商务合作</a></li>
              <li><a href="#" class="text-sm text-gray-400 hover:text-blue-400 transition-colors">反馈建议</a></li>
            </ul>
          </div>
        </div>
        <div class="mt-12 pt-8 border-t border-gray-800 text-center">
          <p class="text-sm text-gray-500">© 2024 技术解析平台. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCategoryStore } from '@/stores/category'
import { analysisApi } from '@/api/analysis'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()
const categoryStore = useCategoryStore()

const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()

const stats = reactive({
  patents: 0,
  papers: 0,
  projects: 0,
  countries: 50
})

const features = [
  { icon: '🗺️', title: '技术图谱', description: '交互式探索全球技术分布，可视化专利、论文、项目数据' },
  { icon: '🔗', title: '关联网络', description: '技术关键词共现关系网络，发现技术间的关联' },
  { icon: '📈', title: '趋势分析', description: '实时监控技术发展动态，把握创新趋势' },
  { icon: '📝', title: '智能报告', description: 'AI智能生成技术分析报告，辅助决策' }
]

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

function formatNumber(num: number): string {
  if (num >= 10000) return (num / 10000).toFixed(1) + '万'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K'
  return num.toString()
}

function handleLogout() {
  categoryStore.clearCategory()
  userStore.logout()
  router.push('/login')
}

function goToClassification() {
  router.push('/classification')
}

function getParticleStyle(index: number) {
  const size = Math.random() * 4 + 2
  const left = Math.random() * 100
  const top = Math.random() * 100
  const duration = Math.random() * 20 + 10
  const delay = Math.random() * 5
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    top: `${top}%`,
    animation: `float ${duration}s ease-in-out ${delay}s infinite`
  }
}

async function loadStats() {
  try {
    const code = categoryStore.currentCode || undefined
    const [patentRes, paperRes, projectRes, trendRes] = await Promise.all([
      analysisApi.getTechMap('patent', code),
      analysisApi.getTechMap('paper', code),
      analysisApi.getTechMap('project', code),
      analysisApi.getTrend(undefined, undefined, undefined, code)
    ])
    
    const patentCountries = patentRes.data?.countries || []
    const paperCountries = paperRes.data?.countries || []
    const projectCountries = projectRes.data?.countries || []
    
    stats.patents = patentCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    stats.papers = paperCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    stats.projects = projectCountries.reduce((sum: number, c: any) => sum + (c.count || 0), 0)
    
    const allCountries = new Set([
      ...patentCountries.map((c: any) => c.country),
      ...paperCountries.map((c: any) => c.country),
      ...projectCountries.map((c: any) => c.country)
    ])
    stats.countries = allCountries.size || 50

    // 登录后渲染图表
    if (userStore.isLoggedIn) {
      await nextTick()
      
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
      const yearlyData = trendRes.data?.yearlyData || []
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
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
}

.feature-card:hover {
  transform: translateY(-4px);
}

.particle {
  pointer-events: none;
}

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

@keyframes float {
  0%, 100% {
    transform: translateY(0) translateX(0);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-20px) translateX(10px);
    opacity: 0.6;
  }
}
</style>

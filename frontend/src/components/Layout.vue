<template>
  <div class="flex h-screen bg-gray-900">
    <!-- 左侧导航栏 -->
    <aside class="w-64 bg-gray-900 border-r border-gray-700 flex flex-col">
      <!-- Logo -->
      <div class="p-4 border-b border-gray-700">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
            <span class="text-white text-lg">🔬</span>
          </div>
          <h1 class="text-white text-lg font-bold">技术解析平台</h1>
        </div>
      </div>

      <!-- 当前分类 -->
      <div v-if="categoryStore.currentCode" class="p-4 border-b border-gray-700 bg-gray-800/50">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">{{ categoryStore.icon }}</span>
            <span class="text-white font-medium text-sm">{{ categoryStore.currentName }}</span>
          </div>
          <button 
            @click="goToClassification" 
            class="text-xs text-blue-400 hover:text-blue-300 transition-colors"
            title="切换分类"
          >
            切换
          </button>
        </div>
      </div>
      
      <!-- 导航菜单 -->
      <nav class="flex-1 p-4 space-y-1">
        <router-link to="/techmap" class="nav-item" :class="{ 'nav-active': route.name === 'TechMap' }">
          <span class="material-icon">🗺️</span>
          <span>技术图谱</span>
        </router-link>
        <router-link to="/trend" class="nav-item" :class="{ 'nav-active': route.name === 'TrendAnalysis' }">
          <span class="material-icon">📈</span>
          <span>趋势分析</span>
        </router-link>
        <router-link to="/network" class="nav-item" :class="{ 'nav-active': route.name === 'ThreeLayers' }">
          <span class="material-icon">🔗</span>
          <span>关联网络</span>
        </router-link>
        <router-link to="/report" class="nav-item" :class="{ 'nav-active': route.name === 'Report' }">
          <span class="material-icon">📝</span>
          <span>分析报告</span>
        </router-link>
        
        <!-- 管理员菜单 -->
        <div v-if="userStore.role === 'ADMIN'" class="pt-4 mt-4 border-t border-gray-700">
          <p class="px-3 text-xs text-gray-500 uppercase tracking-wider mb-2">管理</p>
          <router-link to="/admin" class="nav-item" :class="{ 'nav-active': route.name === 'Admin' }">
            <span class="material-icon">⚙️</span>
            <span>数据管理</span>
          </router-link>
          <router-link to="/settings" class="nav-item" :class="{ 'nav-active': route.name === 'Settings' }">
            <span class="material-icon">🔧</span>
            <span>系统设置</span>
          </router-link>
        </div>
      </nav>
    </aside>
    
    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 顶部导航栏 -->
      <header class="h-16 bg-gray-900/80 backdrop-blur-sm border-b border-gray-700 px-6 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <h2 class="text-white text-xl font-semibold">{{ currentTitle }}</h2>
          <p class="text-gray-400 text-sm hidden md:block">{{ currentDescription }}</p>
        </div>
        
        <div class="flex items-center gap-4">
          <!-- 用户信息 -->
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center text-white text-sm font-medium">
              {{ userStore.username?.charAt(0)?.toUpperCase() || 'U' }}
            </div>
            <span class="text-white text-sm hidden sm:block">{{ userStore.username }}</span>
            <button @click="handleLogout" class="text-gray-400 hover:text-red-400 transition-colors" title="退出登录">
              🚪
            </button>
          </div>
        </div>
      </header>
      
      <!-- 页面内容 -->
      <main class="flex-1 overflow-auto p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCategoryStore } from '@/stores/category'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const categoryStore = useCategoryStore()

const pageInfo: Record<string, { title: string; description: string }> = {
  Dashboard: { title: '仪表盘', description: '数据概览与统计分析' },
  Classification: { title: '技术分类', description: '浏览技术领域分类目录' },
  TechMap: { title: '技术图谱', description: '交互式探索全球技术分布' },
  TrendAnalysis: { title: '趋势分析', description: '实时监控技术发展动态' },
  ThreeLayers: { title: '关联网络', description: '技术关键词共现关系网络' },
  Report: { title: '分析报告', description: 'AI智能生成技术分析报告' },
  Admin: { title: '数据管理', description: '导入和管理专利、项目、论文数据' },
  Settings: { title: '系统设置', description: '配置大模型API等系统参数' }
}

// 如果有选中分类，在标题中显示分类名
const currentTitle = computed(() => {
  const baseTitle = pageInfo[route.name as string]?.title || '技术解析平台'
  if (categoryStore.currentCode && route.name !== 'Classification') {
    return `${baseTitle} - ${categoryStore.currentName}`
  }
  return baseTitle
})

const currentDescription = computed(() => pageInfo[route.name as string]?.description || '')

function handleLogout() {
  categoryStore.clearCategory()
  userStore.logout()
  router.push('/login')
}

function goToClassification() {
  router.push('/classification')
}
</script>

<style scoped>
.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  color: #9ca3af;
  transition: all 0.2s;
  text-decoration: none;
}

.nav-item:hover {
  background-color: rgba(55, 65, 81, 0.5);
  color: #ffffff;
}

.nav-active {
  background-color: rgba(59, 130, 246, 0.1);
  color: #60a5fa;
  font-weight: 500;
}

.material-icon {
  font-size: 1.25rem;
}
</style>

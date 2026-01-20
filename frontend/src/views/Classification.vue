<template>
  <div class="min-h-screen flex flex-col bg-gray-900">
    <!-- 预加载遮罩层 -->
    <div v-if="isPreloading" class="preload-overlay">
      <div class="preload-container">
        <!-- 数据加载动画 -->
        <div class="data-loader">
          <div class="data-cube">
            <div class="cube-face front">📊</div>
            <div class="cube-face back">📈</div>
            <div class="cube-face left">📋</div>
            <div class="cube-face right">🔍</div>
            <div class="cube-face top">💡</div>
            <div class="cube-face bottom">⚡</div>
          </div>
          <div class="data-rings">
            <div class="ring ring-1"></div>
            <div class="ring ring-2"></div>
            <div class="ring ring-3"></div>
          </div>
        </div>
        
        <!-- 加载信息 -->
        <div class="preload-info">
          <h3 class="text-xl font-bold text-white mb-2">正在加载 {{ preloadingName }}</h3>
          <p class="text-blue-400 mb-4">{{ categoryStore.preloadStatus }}</p>
          
          <!-- 进度条 -->
          <div class="progress-container">
            <div class="progress-bar" :style="{ width: categoryStore.preloadProgress + '%' }"></div>
          </div>
          <p class="text-gray-400 text-sm mt-2">{{ categoryStore.preloadProgress }}%</p>
          
          <!-- 加载项列表 -->
          <div class="loading-items mt-4">
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 12.5 }">
              <span class="icon">📄</span>
              <span>专利数据</span>
            </div>
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 25 }">
              <span class="icon">📚</span>
              <span>论文数据</span>
            </div>
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 37.5 }">
              <span class="icon">🔬</span>
              <span>项目数据</span>
            </div>
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 50 }">
              <span class="icon">📈</span>
              <span>趋势分析</span>
            </div>
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 62.5 }">
              <span class="icon">🕸️</span>
              <span>关联网络</span>
            </div>
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 75 }">
              <span class="icon">🔥</span>
              <span>热门技术</span>
            </div>
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 87.5 }">
              <span class="icon">📊</span>
              <span>成熟度分析</span>
            </div>
            <div class="loading-item" :class="{ 'completed': categoryStore.preloadProgress >= 100 }">
              <span class="icon">🌍</span>
              <span>区域分布</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Header -->
    <header class="bg-gray-900/90 backdrop-blur-lg border-b border-gray-700 sticky top-0 z-50">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="flex h-16 items-center justify-between">
          <!-- Logo -->
          <router-link to="/starter" class="flex items-center gap-4">
            <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center shadow-lg">
              <span class="text-white text-lg">🔬</span>
            </div>
            <h1 class="text-white text-xl font-bold">技术解析平台</h1>
          </router-link>
          
          <!-- Right Actions -->
          <div class="flex items-center gap-4">
            <span class="text-sm text-gray-400 hidden sm:block">{{ userStore.username }}</span>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-grow">
      <section class="relative py-16 sm:py-24 overflow-hidden">
        <!-- Background -->
        <div class="absolute inset-0 bg-gradient-to-br from-gray-900 via-blue-900/20 to-gray-900"></div>
        <div class="absolute inset-0 bg-gradient-to-b from-gray-900/80 via-gray-900/60 to-gray-900/90"></div>
        
        <!-- Content -->
        <div class="container mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div class="max-w-4xl mx-auto">
            <!-- Title -->
            <div class="text-center mb-12">
              <h1 class="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white">技术分类目录</h1>
            </div>
            
            <!-- Classification Container -->
            <div class="bg-white/5 backdrop-blur-sm rounded-xl border border-white/10 p-6">
              <h2 class="text-xl font-semibold text-white mb-6 flex items-center gap-2">
                <span class="text-blue-400">📂</span>
                技术领域分类
              </h2>
              
              <!-- Categories -->
              <div class="space-y-3">
                <!-- AI Category -->
                <div class="classification-item rounded-lg p-4 bg-gradient-to-r from-blue-500/10 to-purple-500/10 cursor-pointer hover:from-blue-500/20 hover:to-purple-500/20 transition-all duration-300">
                  <div class="flex items-center justify-between" @click="toggleExpand('TP18')">
                    <div class="flex items-center gap-3">
                      <span class="text-2xl">🤖</span>
                      <div>
                        <h3 class="font-semibold text-white text-lg">TP18 人工智能</h3>
                        <p class="text-gray-300 text-sm mt-1">Artificial Intelligence - 包括机器学习、深度学习、自然语言处理等</p>
                      </div>
                    </div>
                    <div class="flex items-center gap-2">
                    
                      <span class="text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': expandedCode === 'TP18' }">▼</span>
                    </div>
                  </div>
                  
                  <!-- Sub Items -->
                  <div v-if="expandedCode === 'TP18'" class="mt-4 space-y-2">
                    <div class="rounded-lg p-3 bg-white/5 cursor-pointer hover:bg-white/10 transition-all" @click="navigateToReport('TP181', '计算机视觉')">
                      <div class="flex items-center gap-3">
                        <span class="text-blue-400">👁️</span>
                        <div>
                          <h4 class="font-medium text-white">TP181 计算机视觉</h4>
                          <p class="text-gray-400 text-xs mt-1">Computer Vision - 图像识别、目标检测、图像分割等技术</p>
                        </div>
                      </div>
                    </div>
                    <div class="rounded-lg p-3 bg-white/5 cursor-pointer hover:bg-white/10 transition-all" @click="navigateToReport('TP182', '自然语言处理')">
                      <div class="flex items-center gap-3">
                        <span class="text-green-400">💬</span>
                        <div>
                          <h4 class="font-medium text-white">TP182 自然语言处理</h4>
                          <p class="text-gray-400 text-xs mt-1">Natural Language Processing - 文本分析、机器翻译、情感分析等</p>
                        </div>
                      </div>
                    </div>
                    <div class="rounded-lg p-3 bg-white/5 cursor-pointer hover:bg-white/10 transition-all" @click="navigateToReport('TP183', '机器学习')">
                      <div class="flex items-center gap-3">
                        <span class="text-purple-400">🧠</span>
                        <div>
                          <h4 class="font-medium text-white">TP183 机器学习</h4>
                          <p class="text-gray-400 text-xs mt-1">Machine Learning - 监督学习、无监督学习、强化学习等</p>
                        </div>
                      </div>
                    </div>
                    <div class="rounded-lg p-3 bg-white/5 cursor-pointer hover:bg-white/10 transition-all" @click="navigateToReport('TP184', '知识表示与推理')">
                      <div class="flex items-center gap-3">
                        <span class="text-yellow-400">💡</span>
                        <div>
                          <h4 class="font-medium text-white">TP184 知识表示与推理</h4>
                          <p class="text-gray-400 text-xs mt-1">Knowledge Representation & Reasoning - 知识图谱、逻辑推理等</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Computer Technology -->
                <div class="classification-item rounded-lg p-4 bg-gradient-to-r from-cyan-500/10 to-blue-500/10 cursor-pointer hover:from-cyan-500/20 hover:to-blue-500/20 transition-all duration-300"
                     @click="navigateToReport('TP3', '计算机技术')">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3">
                      <span class="text-2xl">💻</span>
                      <div>
                        <h3 class="font-semibold text-white text-lg">TP3 计算机技术</h3>
                        <p class="text-gray-300 text-sm mt-1">Computer Technology - 计算机硬件、软件、网络等技术</p>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Automation -->
                <div class="classification-item rounded-lg p-4 bg-gradient-to-r from-green-500/10 to-emerald-500/10 cursor-pointer hover:from-green-500/20 hover:to-emerald-500/20 transition-all duration-300"
                     @click="navigateToReport('TP2', '自动化技术')">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3">
                      <span class="text-2xl">⚙️</span>
                      <div>
                        <h3 class="font-semibold text-white text-lg">TP2 自动化技术</h3>
                        <p class="text-gray-300 text-sm mt-1">Automation Technology - 自动控制、机器人技术等</p>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Electronics -->
                <div class="classification-item rounded-lg p-4 bg-gradient-to-r from-purple-500/10 to-pink-500/10 cursor-pointer hover:from-purple-500/20 hover:to-purple-500/20 transition-all duration-300"
                     @click="navigateToReport('TN', '电子技术')">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3">
                      <span class="text-2xl">⚡</span>
                      <div>
                        <h3 class="font-semibold text-white text-lg">TN 电子技术</h3>
                        <p class="text-gray-300 text-sm mt-1">Electronic Technology - 电子电路、半导体技术等</p>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Communication -->
                <div class="classification-item rounded-lg p-4 bg-gradient-to-r from-orange-500/10 to-red-500/10 cursor-pointer hover:from-orange-500/20 hover:to-red-500/20 transition-all duration-300"
                     @click="navigateToReport('TN91', '通信技术')">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3">
                      <span class="text-2xl">📡</span>
                      <div>
                        <h3 class="font-semibold text-white text-lg">TN91 通信技术</h3>
                        <p class="text-gray-300 text-sm mt-1">Communication Technology - 无线通信、光纤通信等</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- Footer -->
    <footer class="bg-gray-900 border-t border-gray-800">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8">
          <div class="col-span-2 md:col-span-1">
            <div class="flex items-center gap-2 mb-4">
              <span class="text-2xl">🔬</span>
              <span class="text-lg font-bold text-white">技术解析平台</span>
            </div>
            <p class="text-sm text-gray-500">领先的技术解析平台，为技术研究和合作提供专业支持。</p>
          </div>
          <div>
            <h3 class="text-sm font-semibold text-white mb-4">平台</h3>
            <ul class="space-y-2">
              <li><router-link to="/techmap" class="text-sm text-gray-500 hover:text-blue-400">技术图谱</router-link></li>
              <li><router-link to="/trend" class="text-sm text-gray-500 hover:text-blue-400">趋势分析</router-link></li>
              <li><router-link to="/report" class="text-sm text-gray-500 hover:text-blue-400">分析报告</router-link></li>
            </ul>
          </div>
          <div>
            <h3 class="text-sm font-semibold text-white mb-4">公司</h3>
            <ul class="space-y-2">
              <li><a href="#" class="text-sm text-gray-500 hover:text-blue-400">关于我们</a></li>
              <li><a href="#" class="text-sm text-gray-500 hover:text-blue-400">联系我们</a></li>
            </ul>
          </div>
          <div>
            <h3 class="text-sm font-semibold text-white mb-4">法律</h3>
            <ul class="space-y-2">
              <li><a href="#" class="text-sm text-gray-500 hover:text-blue-400">隐私政策</a></li>
              <li><a href="#" class="text-sm text-gray-500 hover:text-blue-400">服务条款</a></li>
            </ul>
          </div>
        </div>
        <div class="mt-12 pt-8 border-t border-gray-800 text-center">
          <p class="text-sm text-gray-500">© 2024 技术解析平台。保留所有权利。</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCategoryStore } from '@/stores/category'

const router = useRouter()
const userStore = useUserStore()
const categoryStore = useCategoryStore()
const expandedCode = ref<string | null>(null)
const isPreloading = ref(false)
const preloadingName = ref('')

function toggleExpand(code: string) {
  expandedCode.value = expandedCode.value === code ? null : code
}

async function navigateToReport(code: string, name: string) {
  // 保存选中的分类到 store
  categoryStore.setCategory(code, name)
  
  // 显示预加载界面
  isPreloading.value = true
  preloadingName.value = name
  
  try {
    // 预加载所有分析数据
    const success = await categoryStore.preloadData(code)
    
    if (success) {
      // 加载成功，延迟一点跳转以让用户看到100%
      await new Promise(resolve => setTimeout(resolve, 500))
      
      // 跳转到仪表盘（带分类参数）
      router.push({
        path: '/',
        query: { code, name }
      })
    } else {
      // 加载失败，也跳转但不带缓存
      router.push({
        path: '/',
        query: { code, name }
      })
    }
  } catch (error) {
    console.error('预加载失败:', error)
    // 发生错误也继续跳转
    router.push({
      path: '/',
      query: { code, name }
    })
  } finally {
    isPreloading.value = false
  }
}
</script>

<style scoped>
.classification-item {
  opacity: 0;
  animation: fadeInUp 0.6s ease forwards;
}

.classification-item:nth-child(1) { animation-delay: 0.1s; }
.classification-item:nth-child(2) { animation-delay: 0.2s; }
.classification-item:nth-child(3) { animation-delay: 0.3s; }
.classification-item:nth-child(4) { animation-delay: 0.4s; }
.classification-item:nth-child(5) { animation-delay: 0.5s; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 预加载遮罩层样式 */
.preload-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(17, 24, 39, 0.98);
  backdrop-filter: blur(10px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preload-container {
  text-align: center;
  padding: 2rem;
}

/* 3D数据立方体动画 */
.data-loader {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 2rem;
  perspective: 400px;
}

.data-cube {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 50px;
  height: 50px;
  transform-style: preserve-3d;
  animation: rotateCube 4s linear infinite;
  transform: translate(-50%, -50%);
}

.cube-face {
  position: absolute;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.3), rgba(147, 51, 234, 0.3));
  border: 1px solid rgba(99, 102, 241, 0.5);
  border-radius: 8px;
}

.cube-face.front { transform: translateZ(25px); }
.cube-face.back { transform: rotateY(180deg) translateZ(25px); }
.cube-face.left { transform: rotateY(-90deg) translateZ(25px); }
.cube-face.right { transform: rotateY(90deg) translateZ(25px); }
.cube-face.top { transform: rotateX(90deg) translateZ(25px); }
.cube-face.bottom { transform: rotateX(-90deg) translateZ(25px); }

@keyframes rotateCube {
  0% { transform: translate(-50%, -50%) rotateX(0deg) rotateY(0deg); }
  100% { transform: translate(-50%, -50%) rotateX(360deg) rotateY(360deg); }
}

/* 环形动画 */
.data-rings {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
}

.ring {
  position: absolute;
  border: 2px solid transparent;
  border-radius: 50%;
  animation: ringPulse 2s ease-in-out infinite;
}

.ring-1 {
  width: 80px;
  height: 80px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-color: rgba(59, 130, 246, 0.5);
  animation-delay: 0s;
}

.ring-2 {
  width: 100px;
  height: 100px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-color: rgba(147, 51, 234, 0.4);
  animation-delay: 0.3s;
}

.ring-3 {
  width: 120px;
  height: 120px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-color: rgba(236, 72, 153, 0.3);
  animation-delay: 0.6s;
}

@keyframes ringPulse {
  0%, 100% { 
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
  50% { 
    transform: translate(-50%, -50%) scale(1.1);
    opacity: 0.5;
  }
}

/* 进度条 */
.progress-container {
  width: 300px;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  overflow: hidden;
  margin: 0 auto;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6, #ec4899);
  border-radius: 3px;
  transition: width 0.3s ease;
  box-shadow: 0 0 10px rgba(139, 92, 246, 0.5);
}

/* 加载项列表 */
.loading-items {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.5rem;
  max-width: 400px;
  margin: 0 auto;
}

.loading-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.loading-item .icon {
  font-size: 1rem;
  filter: grayscale(100%);
  transition: filter 0.3s ease;
}

.loading-item.completed {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.loading-item.completed .icon {
  filter: grayscale(0%);
}

.preload-info h3 {
  background: linear-gradient(90deg, #fff, #a5b4fc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>

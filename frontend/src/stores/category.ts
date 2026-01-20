import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { analysisApi } from '@/api/analysis'

export interface CategoryInfo {
  code: string
  name: string
  icon: string
  keywords: string[]
}

// 预加载数据缓存
export interface PreloadedData {
  techMapPatent: any
  techMapPaper: any
  techMapProject: any
  trend: any
  network: any
  hotTech: any
  maturity: any
  region: any
}

// 预定义的分类信息
// 注意：TP181（计算机视觉）的 keywords 为空，因为数据库中所有数据都是该领域的，不需要过滤
const CATEGORY_MAP: Record<string, CategoryInfo> = {
  'TP18': { code: 'TP18', name: '人工智能', icon: '🤖', keywords: ['人工智能', '机器学习', '深度学习', '神经网络', 'AI'] },
  'TP181': { code: 'TP181', name: '计算机视觉', icon: '👁️', keywords: [] },
  'TP182': { code: 'TP182', name: '自然语言处理', icon: '💬', keywords: ['自然语言', 'NLP', '文本分析', '机器翻译'] },
  'TP183': { code: 'TP183', name: '机器学习', icon: '🧠', keywords: ['机器学习', '监督学习', '无监督学习', '强化学习'] },
  'TP184': { code: 'TP184', name: '知识表示与推理', icon: '💡', keywords: ['知识图谱', '知识表示', '逻辑推理'] },
  'TP3': { code: 'TP3', name: '计算机技术', icon: '💻', keywords: ['计算机', '软件', '算法', '数据结构', '程序'] },
  'TP2': { code: 'TP2', name: '自动化技术', icon: '⚙️', keywords: ['自动化', '控制', '机器人', 'PLC', '传感器'] },
  'TN': { code: 'TN', name: '电子技术', icon: '⚡', keywords: ['电子', '电路', '半导体', '芯片', '集成电路'] },
  'TN91': { code: 'TN91', name: '通信技术', icon: '📡', keywords: ['通信', '无线', '5G', '4G', '网络', '信号'] }
}

export const useCategoryStore = defineStore('category', () => {
  // 当前选中的分类代码
  const currentCode = ref(sessionStorage.getItem('categoryCode') || '')
  const currentName = ref(sessionStorage.getItem('categoryName') || '')

  // 当前分类信息
  const currentCategory = computed<CategoryInfo | null>(() => {
    if (!currentCode.value) return null
    return CATEGORY_MAP[currentCode.value] || {
      code: currentCode.value,
      name: currentName.value,
      icon: '📂',
      keywords: [currentName.value]
    }
  })

  // 是否已选择分类
  const hasCategory = computed(() => !!currentCode.value)

  // 设置当前分类
  function setCategory(code: string, name: string) {
    currentCode.value = code
    currentName.value = name
    sessionStorage.setItem('categoryCode', code)
    sessionStorage.setItem('categoryName', name)
  }

  // 清除当前分类
  function clearCategory() {
    currentCode.value = ''
    currentName.value = ''
    sessionStorage.removeItem('categoryCode')
    sessionStorage.removeItem('categoryName')
  }

  // 获取当前分类的关键词（用于数据过滤）
  const keywords = computed(() => currentCategory.value?.keywords || [])

  // 获取当前分类的图标
  const icon = computed(() => currentCategory.value?.icon || '📂')

  // 预加载数据缓存
  const preloadedData = ref<PreloadedData | null>(null)
  const isPreloading = ref(false)
  const preloadProgress = ref(0)
  const preloadStatus = ref('')

  // 预加载所有分析数据
  async function preloadData(categoryCode: string): Promise<boolean> {
    isPreloading.value = true
    preloadProgress.value = 0
    preloadStatus.value = '正在初始化...'
    
    const totalSteps = 8
    let completedSteps = 0
    
    const updateProgress = (status: string) => {
      completedSteps++
      preloadProgress.value = Math.round((completedSteps / totalSteps) * 100)
      preloadStatus.value = status
    }
    
    try {
      const data: PreloadedData = {
        techMapPatent: null,
        techMapPaper: null,
        techMapProject: null,
        trend: null,
        network: null,
        hotTech: null,
        maturity: null,
        region: null
      }
      
      // 并行加载所有数据
      const [
        techMapPatentRes,
        techMapPaperRes,
        techMapProjectRes,
        trendRes,
        networkRes,
        hotTechRes,
        maturityRes,
        regionRes
      ] = await Promise.all([
        analysisApi.getTechMap('patent', categoryCode).then(res => { updateProgress('专利数据加载完成'); return res }),
        analysisApi.getTechMap('paper', categoryCode).then(res => { updateProgress('论文数据加载完成'); return res }),
        analysisApi.getTechMap('project', categoryCode).then(res => { updateProgress('项目数据加载完成'); return res }),
        analysisApi.getTrend(undefined, undefined, undefined, categoryCode).then(res => { updateProgress('趋势数据加载完成'); return res }),
        analysisApi.getNetwork(undefined, categoryCode).then(res => { updateProgress('网络数据加载完成'); return res }),
        analysisApi.getHotTech(categoryCode).then(res => { updateProgress('热门技术加载完成'); return res }),
        analysisApi.getMaturity(categoryCode).then(res => { updateProgress('成熟度数据加载完成'); return res }),
        analysisApi.getRegion(categoryCode).then(res => { updateProgress('区域分布加载完成'); return res })
      ])
      
      data.techMapPatent = techMapPatentRes.data
      data.techMapPaper = techMapPaperRes.data
      data.techMapProject = techMapProjectRes.data
      data.trend = trendRes.data
      data.network = networkRes.data
      data.hotTech = hotTechRes.data
      data.maturity = maturityRes.data
      data.region = regionRes.data
      
      preloadedData.value = data
      preloadStatus.value = '数据加载完成！'
      
      return true
    } catch (error) {
      console.error('预加载数据失败:', error)
      preloadStatus.value = '加载失败，请重试'
      return false
    } finally {
      isPreloading.value = false
    }
  }

  // 清除缓存数据
  function clearPreloadedData() {
    preloadedData.value = null
    preloadProgress.value = 0
    preloadStatus.value = ''
  }

  return {
    currentCode,
    currentName,
    currentCategory,
    hasCategory,
    keywords,
    icon,
    setCategory,
    clearCategory,
    CATEGORY_MAP,
    // 预加载相关
    preloadedData,
    isPreloading,
    preloadProgress,
    preloadStatus,
    preloadData,
    clearPreloadedData
  }
})

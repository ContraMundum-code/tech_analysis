<template>
  <div class="space-y-6">
    <!-- 数据导入 -->
    <div class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-gray-700/50">
      <h3 class="text-lg font-semibold text-white mb-4 flex items-center">
        <svg class="w-5 h-5 mr-2 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
        </svg>
        数据导入
      </h3>
      <div class="mb-4 p-3 bg-cyan-500/10 rounded-lg text-sm text-cyan-300 border border-cyan-500/20">
        <p class="flex items-center"><span class="text-cyan-400 mr-2">✓</span> 支持大文件导入（最大 500MB）</p>
        <p class="flex items-center"><span class="text-cyan-400 mr-2">✓</span> 流式处理，内存占用低</p>
        <p class="flex items-center"><span class="text-cyan-400 mr-2">✓</span> 支持 10 万+ 条数据导入</p>
      </div>
      <el-tabs v-model="activeTab" class="dark-tabs">
        <el-tab-pane label="专利数据" name="patent">
          <el-upload drag :auto-upload="false" :on-change="(f: any) => handleFileChange(f, 'patent')" :show-file-list="false" accept=".xlsx,.xls">
            <div class="py-8">
              <svg class="mx-auto h-12 w-12 text-gray-500" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <p class="mt-2 text-gray-400">拖拽 Excel 文件到此处或<em class="text-cyan-400">点击上传</em></p>
              <p class="text-xs text-gray-500 mt-1">支持 .xlsx 格式，最大 500MB</p>
            </div>
          </el-upload>
        </el-tab-pane>
        <el-tab-pane label="项目数据" name="project">
          <el-upload drag :auto-upload="false" :on-change="(f: any) => handleFileChange(f, 'project')" :show-file-list="false" accept=".xlsx,.xls">
            <div class="py-8">
              <svg class="mx-auto h-12 w-12 text-gray-500" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <p class="mt-2 text-gray-400">拖拽 Excel 文件到此处或<em class="text-cyan-400">点击上传</em></p>
              <p class="text-xs text-gray-500 mt-1">支持 .xlsx 格式，最大 500MB</p>
            </div>
          </el-upload>
        </el-tab-pane>
        <el-tab-pane label="论文数据" name="paper">
          <el-upload drag :auto-upload="false" :on-change="(f: any) => handleFileChange(f, 'paper')" :show-file-list="false" accept=".xlsx,.xls">
            <div class="py-8">
              <svg class="mx-auto h-12 w-12 text-gray-500" stroke="currentColor" fill="none" viewBox="0 0 48 48">
                <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <p class="mt-2 text-gray-400">拖拽 Excel 文件到此处或<em class="text-cyan-400">点击上传</em></p>
              <p class="text-xs text-gray-500 mt-1">支持 .xlsx 格式，最大 500MB</p>
            </div>
          </el-upload>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 导入进度 -->
    <div v-if="importing" class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-gray-700/50">
      <h3 class="text-lg font-semibold text-white mb-4 flex items-center">
        <svg class="animate-spin w-5 h-5 mr-2 text-cyan-400" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        正在导入...
      </h3>
      <el-progress :percentage="100" status="success" :indeterminate="true" />
      <p class="text-gray-400 mt-2 text-sm">{{ importStatus }}</p>
    </div>

    <!-- 导入结果 -->
    <div v-if="importResult" class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-green-500/30">
      <div class="flex items-center">
        <div class="w-12 h-12 rounded-full bg-green-500/20 flex items-center justify-center mr-4">
          <svg class="h-6 w-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <div>
          <h3 class="text-lg font-semibold text-green-400">导入成功</h3>
          <p class="text-gray-400">共导入 <span class="text-white font-semibold">{{ importResult.imported }}</span> 条数据，耗时 {{ (importResult.duration / 1000).toFixed(1) }} 秒</p>
        </div>
      </div>
    </div>

    <!-- 数据格式说明 -->
    <div class="bg-gray-800/50 backdrop-blur-sm p-6 rounded-xl border border-gray-700/50">
      <h3 class="text-lg font-semibold text-white mb-4 flex items-center">
        <svg class="w-5 h-5 mr-2 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        Excel 格式说明
      </h3>
      <el-tabs class="dark-tabs">
        <el-tab-pane label="专利数据">
          <p class="text-sm text-gray-400 mb-2">Excel 列顺序（第一行为表头）：</p>
          <code class="text-xs bg-gray-900/50 text-cyan-300 p-3 rounded-lg block whitespace-pre-wrap border border-gray-700/50">
序号 | 标题 | 摘要 | 申请人 | 公开号 | 公开年份 | 月份 | 公开国别 | IPC | 专利有效性 | 标题关键词 | 摘要关键词 | 标题实体 | 摘要实体
          </code>
        </el-tab-pane>
        <el-tab-pane label="项目数据">
          <p class="text-sm text-gray-400 mb-2">Excel 列顺序（第一行为表头）：</p>
          <code class="text-xs bg-gray-900/50 text-cyan-300 p-3 rounded-lg block whitespace-pre-wrap border border-gray-700/50">
项目id | 标题 | 资助机构 | 承担机构 | 项目类型 | 项目金额 | 项目年份 | 项目月份 | 摘要 | 标题关键词 | 摘要关键词 | 标题实体 | 摘要实体
          </code>
        </el-tab-pane>
        <el-tab-pane label="论文数据">
          <p class="text-sm text-gray-400 mb-2">Excel 列顺序（第一行为表头）：</p>
          <code class="text-xs bg-gray-900/50 text-cyan-300 p-3 rounded-lg block whitespace-pre-wrap border border-gray-700/50">
Authors | Author Full Names | 标题 | Source Title | Document Type | Author Keywords | 摘要 | Addresses | ... | Publication Year | DOI | ... | Countries | 标题关键词 | 摘要关键词 | 标题实体 | 摘要实体
          </code>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const activeTab = ref('patent')
const importing = ref(false)
const importStatus = ref('')
const importResult = ref<{ imported: number; duration: number } | null>(null)

async function handleFileChange(file: any, type: string) {
  if (!file.raw) return
  
  importing.value = true
  importResult.value = null
  importStatus.value = `正在上传并导入${type === 'patent' ? '专利' : type === 'project' ? '项目' : '论文'}数据...`

  try {
    let res
    if (type === 'patent') {
      res = await adminApi.importPatent(file.raw)
    } else if (type === 'project') {
      res = await adminApi.importProject(file.raw)
    } else {
      res = await adminApi.importPaper(file.raw)
    }
    
    importResult.value = res.data
    ElMessage.success(`导入成功，共 ${res.data.imported} 条数据`)
  } catch (e: any) {
    ElMessage.error(e.message || '导入失败')
  } finally {
    importing.value = false
    importStatus.value = ''
  }
}
</script>

<template>
  <div class="max-w-2xl">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-white flex items-center">
        <svg class="w-7 h-7 mr-3 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
        </svg>
        系统设置
      </h1>
      <p class="text-gray-400 mt-1 ml-10">配置大模型API用于生成分析报告</p>
    </div>

    <div class="bg-gray-800/50 backdrop-blur-sm rounded-xl border border-gray-700/50 p-6">
      <h2 class="text-lg font-semibold text-white mb-6 flex items-center">
        <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center mr-3">
          <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
        </div>
        智谱大模型配置
      </h2>

      <form @submit.prevent="saveConfig" class="space-y-5">
        <div>
          <label class="block text-sm font-medium text-gray-300 mb-2">API 地址</label>
          <input
            v-model="config.apiUrl"
            type="text"
            class="w-full px-4 py-3 bg-gray-900/50 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:ring-2 focus:ring-cyan-500 focus:border-transparent transition-all"
            placeholder="https://open.bigmodel.cn/api/paas/v4/chat/completions"
          />
          <p class="text-xs text-gray-500 mt-1">智谱AI开放平台API地址</p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-300 mb-2">API Key</label>
          <div class="relative">
            <input
              v-model="config.apiKey"
              :type="showApiKey ? 'text' : 'password'"
              class="w-full px-4 py-3 pr-12 bg-gray-900/50 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:ring-2 focus:ring-cyan-500 focus:border-transparent transition-all"
              placeholder="请输入您的API Key"
            />
            <button
              type="button"
              @click="showApiKey = !showApiKey"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300 transition-colors"
            >
              <svg v-if="showApiKey" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path>
              </svg>
              <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
              </svg>
            </button>
          </div>
          <p class="text-xs text-gray-500 mt-1">
            请前往 <a href="https://open.bigmodel.cn/" target="_blank" class="text-cyan-400 hover:underline">智谱AI开放平台</a> 获取API Key
          </p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-300 mb-2">模型选择</label>
          <select
            v-model="config.model"
            class="w-full px-4 py-3 bg-gray-900/50 border border-gray-700 rounded-lg text-white focus:ring-2 focus:ring-cyan-500 focus:border-transparent transition-all"
          >
            <option value="glm-4-flash">GLM-4-Flash (快速经济)</option>
            <option value="glm-4-air">GLM-4-Air (均衡)</option>
            <option value="glm-4-airx">GLM-4-AirX (高速)</option>
            <option value="glm-4-long">GLM-4-Long (长文本)</option>
            <option value="glm-4">GLM-4 (高质量)</option>
            <option value="glm-4-plus">GLM-4-Plus (增强)</option>
            <option value="glm-4.7">GLM-4.7 (最新)</option>
          </select>
          <p class="text-xs text-gray-500 mt-1">选择用于生成报告的模型，推荐使用 GLM-4-Flash</p>
        </div>

        <div class="pt-4 flex space-x-3">
          <button
            type="submit"
            :disabled="saving"
            class="px-5 py-2.5 bg-gradient-to-r from-cyan-500 to-blue-500 text-white rounded-lg hover:from-cyan-600 hover:to-blue-600 disabled:opacity-50 flex items-center font-medium transition-all"
          >
            <svg v-if="saving" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ saving ? '保存中...' : '保存配置' }}
          </button>
          <button
            type="button"
            @click="testConnection"
            :disabled="testing"
            class="px-5 py-2.5 bg-gray-700 text-gray-200 border border-gray-600 rounded-lg hover:bg-gray-600 disabled:opacity-50 flex items-center font-medium transition-all"
          >
            <svg v-if="testing" class="animate-spin -ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ testing ? '测试中...' : '测试连接' }}
          </button>
        </div>
      </form>

      <div v-if="message" :class="['mt-5 p-4 rounded-lg flex items-center', messageType === 'success' ? 'bg-green-500/10 text-green-400 border border-green-500/20' : 'bg-red-500/10 text-red-400 border border-red-500/20']">
        <svg v-if="messageType === 'success'" class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <svg v-else class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getSystemConfig, saveSystemConfig, type SystemConfig } from '../api/config'

const config = ref<SystemConfig>({
  apiUrl: 'https://open.bigmodel.cn/api/paas/v4/chat/completions',
  apiKey: '',
  model: 'glm-4-flash'
})

const showApiKey = ref(false)
const saving = ref(false)
const testing = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')

onMounted(async () => {
  try {
    const res = await getSystemConfig()
    if (res.data) {
      config.value = res.data
    }
  } catch (e) {
    console.error('加载配置失败', e)
  }
})

async function saveConfig() {
  saving.value = true
  message.value = ''
  try {
    await saveSystemConfig(config.value)
    message.value = '配置保存成功'
    messageType.value = 'success'
  } catch (e: any) {
    message.value = e.message || '保存失败'
    messageType.value = 'error'
  } finally {
    saving.value = false
  }
}

async function testConnection() {
  if (!config.value.apiKey || config.value.apiKey.includes('***')) {
    message.value = '请先输入完整的API Key'
    messageType.value = 'error'
    return
  }
  testing.value = true
  message.value = ''
  try {
    await saveSystemConfig(config.value)
    message.value = '配置已保存，请在报告生成页面测试效果'
    messageType.value = 'success'
  } catch (e: any) {
    message.value = e.message || '测试失败'
    messageType.value = 'error'
  } finally {
    testing.value = false
  }
}
</script>

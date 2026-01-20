<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-900 relative overflow-hidden">
    <!-- 背景装饰 -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-cyan-500/20 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-purple-500/20 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-blue-500/10 rounded-full blur-3xl"></div>
    </div>
    
    <!-- 登录卡片 -->
    <div class="relative bg-gray-800/50 backdrop-blur-xl p-8 rounded-2xl border border-gray-700/50 w-[420px] shadow-2xl">
      <!-- Logo -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-cyan-500 to-blue-500 flex items-center justify-center">
          <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
          </svg>
        </div>
        <h1 class="text-2xl font-bold text-white">技术解析平台</h1>
        <p class="text-gray-400 text-sm mt-2">专利 · 论文 · 项目 智能分析</p>
      </div>
      
      <el-tabs v-model="activeTab" class="dark-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form @submit.prevent="handleLogin" class="space-y-4">
            <el-form-item>
              <el-input 
                v-model="loginForm.username" 
                placeholder="用户名" 
                prefix-icon="User"
                size="large"
                class="dark-input"
              />
            </el-form-item>
            <el-form-item>
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="密码" 
                prefix-icon="Lock"
                size="large"
                class="dark-input"
                show-password
              />
            </el-form-item>
            <el-button 
              type="primary" 
              class="w-full h-11 bg-gradient-to-r from-cyan-500 to-blue-500 border-0 text-base font-medium" 
              :loading="loading" 
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="注册" name="register">
          <el-form @submit.prevent="handleRegister" class="space-y-4">
            <el-form-item>
              <el-input 
                v-model="registerForm.username" 
                placeholder="用户名" 
                prefix-icon="User"
                size="large"
                class="dark-input"
              />
            </el-form-item>
            <el-form-item>
              <el-input 
                v-model="registerForm.email" 
                placeholder="邮箱" 
                prefix-icon="Message"
                size="large"
                class="dark-input"
              />
            </el-form-item>
            <el-form-item>
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="密码" 
                prefix-icon="Lock"
                size="large"
                class="dark-input"
                show-password
              />
            </el-form-item>
            <el-button 
              type="primary" 
              class="w-full h-11 bg-gradient-to-r from-cyan-500 to-blue-500 border-0 text-base font-medium" 
              :loading="loading" 
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <!-- 底部装饰 -->
      <div class="mt-6 pt-6 border-t border-gray-700/50 text-center">
        <p class="text-gray-500 text-xs">© 2024 技术解析平台 · 数据驱动创新</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', email: '', password: '' })

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(loginForm.username, loginForm.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    await userStore.register(registerForm.username, registerForm.password, registerForm.email)
    ElMessage.success('注册成功')
    router.push('/')
  } catch {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

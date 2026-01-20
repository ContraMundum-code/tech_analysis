import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || '')

  const isLoggedIn = computed(() => !!token.value)

  async function login(usernameVal: string, password: string) {
    const res = await authApi.login(usernameVal, password)
    token.value = res.data.token
    username.value = res.data.username
    role.value = res.data.role
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('role', res.data.role)
  }

  async function register(usernameVal: string, password: string, email: string) {
    const res = await authApi.register(usernameVal, password, email)
    token.value = res.data.token
    username.value = res.data.username
    role.value = res.data.role
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('role', res.data.role)
  }

  function logout() {
    token.value = ''
    username.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
  }

  return { token, username, role, isLoggedIn, login, register, logout }
})

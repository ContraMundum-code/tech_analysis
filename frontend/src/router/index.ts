import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    name: 'Starter',
    component: () => import('@/views/Starter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/classification',
    name: 'Classification',
    component: () => import('@/views/Classification.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '',
    component: () => import('@/components/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '/techmap',
        name: 'TechMap',
        component: () => import('@/views/TechMap.vue')
      },
      {
        path: '/trend',
        name: 'TrendAnalysis',
        component: () => import('@/views/TrendAnalysis.vue')
      },
      {
        path: '/network',
        name: 'ThreeLayers',
        component: () => import('@/views/ThreeLayers.vue')
      },
      {
        path: '/report',
        name: 'Report',
        component: () => import('@/views/Report.vue')
      },
      {
        path: '/admin',
        name: 'Admin',
        component: () => import('@/views/AdminDashboard.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: '/settings',
        name: 'Settings',
        component: () => import('@/views/SystemSettings.vue'),
        meta: { requiresAdmin: true }
      }
    ]
  },
  // 兼容旧路由
  { path: '/starter', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  
  // 已登录用户访问登录页，重定向到首页
  if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.role !== 'ADMIN') {
    next('/')
  } else {
    next()
  }
})

export default router

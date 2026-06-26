import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/features/auth/views/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/',
      name: 'dashboard',
      component: () => import('@/features/tasklists/views/DashboardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/tasks/:listId',
      name: 'tasks',
      component: () => import('@/features/tasks/views/TasksView.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const authData = localStorage.getItem('auth')
  let isAuthenticated = false
  if (authData) {
    try {
      const parsed = JSON.parse(authData)
      isAuthenticated = !!parsed.token && !!parsed.user
    } catch {
    }
  }

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
  } else if (to.meta.guest && isAuthenticated) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router

import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'

interface FailedRequest {
  resolve: (token: string) => void
  reject: (error: unknown) => void
}

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000',
  headers: { 'Content-Type': 'application/json' },
})

let isRefreshing = false
let failedQueue: FailedRequest[] = []

function processQueue(error: unknown, token: string | null = null) {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error)
    } else {
      resolve(token!)
    }
  })
  failedQueue = []
}

api.interceptors.request.use((config) => {
  const raw = localStorage.getItem('auth')
  if (raw) {
    try {
      const parsed = JSON.parse(raw)
      if (parsed.token) {
        config.headers.Authorization = `Bearer ${parsed.token}`
      }
    } catch {
    }
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

    if (
      error.response?.status !== 401 ||
      !originalRequest ||
      originalRequest._retry ||
      originalRequest.url?.includes('/auth/')
    ) {
      return Promise.reject(error)
    }

    if (isRefreshing) {
      return new Promise<string>((resolve, reject) => {
        failedQueue.push({ resolve, reject })
      }).then((token) => {
        originalRequest.headers.Authorization = `Bearer ${token}`
        return api(originalRequest)
      })
    }

    originalRequest._retry = true
    isRefreshing = true

    try {
      const refreshToken = (() => {
        try {
          const raw = localStorage.getItem('auth_refresh')
          return raw ? JSON.parse(raw) : null
        } catch {
          return null
        }
      })()
      if (!refreshToken) throw new Error('No refresh token')

      const { data } = await axios.post(
        `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000'}/auth/refresh`,
        { refreshToken },
      )

      const raw = localStorage.getItem('auth')
      if (raw) {
        const auth = JSON.parse(raw)
        auth.token = data.token
        localStorage.setItem('auth', JSON.stringify(auth))
      }
      localStorage.setItem('auth_refresh', JSON.stringify(data.refreshToken))

      processQueue(null, data.token)

      originalRequest.headers.Authorization = `Bearer ${data.token}`
      return api(originalRequest)
    } catch (refreshError) {
      processQueue(refreshError, null)
      localStorage.removeItem('auth')
      window.location.href = '/login'
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
    }
  },
)

export default api

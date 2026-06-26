import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000',
  headers: { 'Content-Type': 'application/json' },
})

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
  (error) => {
    if (error.response?.status === 401) {
      const requestUrl = error.config?.url || ''
      const wasAuthed = !!localStorage.getItem('auth')

      if (!requestUrl.includes('/auth/') && wasAuthed) {
        localStorage.removeItem('auth')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export default api

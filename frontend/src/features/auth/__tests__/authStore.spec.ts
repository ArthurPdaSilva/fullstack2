import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useAuthStore } from '../stores/authStore'
import api from '@/services/api'

vi.mock('@/services/api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
  },
}))

describe('authStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    ;(api.get as ReturnType<typeof vi.fn>).mockResolvedValue({ data: [] })
  })

  it('starts unauthenticated', () => {
    const store = useAuthStore()
    expect(store.isAuthenticated).toBe(false)
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })

  it('can logout', () => {
    const store = useAuthStore()
    store.logout()
    expect(store.isAuthenticated).toBe(false)
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })

  it('logout clears user and token', () => {
    const store = useAuthStore()
    store.user = { id: '1', name: 'John', email: 'john@email.com' }
    store.token = 'fake-token'
    store.logout()
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })

  it('isAuthenticated is true when user and token exist', () => {
    const store = useAuthStore()
    store.user = { id: '1', name: 'John', email: 'john@email.com' }
    store.token = 'fake-token'
    expect(store.isAuthenticated).toBe(true)
  })

  it('isAuthenticated is false when only token exists', () => {
    const store = useAuthStore()
    store.token = 'fake-token'
    expect(store.isAuthenticated).toBe(false)
  })

  it('isAuthenticated is false when only user exists', () => {
    const store = useAuthStore()
    store.user = { id: '1', name: 'John', email: 'john@email.com' }
    expect(store.isAuthenticated).toBe(false)
  })

  it('stores refreshToken from login', async () => {
    const store = useAuthStore()
    const api = (await import('@/services/api')).default
    ;(api.post as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: {
        token: 'access-token',
        refreshToken: 'refresh-token',
        type: 'Bearer',
        expiresIn: 900000,
        refreshExpiresIn: 604800000,
      },
    })
    await store.login({ email: 'john@email.com', password: '123456' })
    expect(store.token).toBe('access-token')
    expect(store.refreshToken).toBe('refresh-token')
  })

  it('stores refreshToken from register', async () => {
    const store = useAuthStore()
    const api = (await import('@/services/api')).default
    ;(api.post as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: {
        token: 'access-token',
        refreshToken: 'refresh-token',
        type: 'Bearer',
        expiresIn: 900000,
        refreshExpiresIn: 604800000,
      },
    })
    await store.register({ name: 'John', email: 'john@email.com', password: '123456' })
    expect(store.token).toBe('access-token')
    expect(store.refreshToken).toBe('refresh-token')
  })

  it('clears refreshToken on logout', () => {
    const store = useAuthStore()
    store.user = { id: '1', name: 'John', email: 'john@email.com' }
    store.token = 'fake-token'
    store.refreshToken = 'refresh-token'
    store.logout()
    expect(store.refreshToken).toBeNull()
  })

  it('refreshTokenAction calls /auth/refresh and updates tokens', async () => {
    const store = useAuthStore()
    const api = (await import('@/services/api')).default
    store.token = 'expired-token'
    store.refreshToken = 'valid-refresh-token'
    ;(api.post as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: {
        token: 'new-access-token',
        refreshToken: 'new-refresh-token',
        type: 'Bearer',
        expiresIn: 900000,
        refreshExpiresIn: 604800000,
      },
    })
    await store.refreshTokenAction()
    expect(store.token).toBe('new-access-token')
    expect(store.refreshToken).toBe('new-refresh-token')
    expect(api.post).toHaveBeenCalledWith('/auth/refresh', {
      refreshToken: 'valid-refresh-token',
    })
  })

  it('refreshTokenAction throws when no refresh token', async () => {
    const store = useAuthStore()
    store.refreshToken = null
    await expect(store.refreshTokenAction()).rejects.toThrow('No refresh token available')
  })
})

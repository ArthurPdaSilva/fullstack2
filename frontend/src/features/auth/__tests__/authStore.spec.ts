import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useAuthStore } from '../stores/authStore'

vi.mock('@/services/api', () => ({
  default: {
    post: vi.fn(),
  },
}))

describe('authStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
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
})

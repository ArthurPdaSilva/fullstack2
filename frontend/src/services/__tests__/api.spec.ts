import { describe, expect, it, vi, beforeEach } from 'vitest'

const mockAxiosCreate = vi.fn(() => ({
  interceptors: {
    request: { use: vi.fn() },
    response: { use: vi.fn() },
  },
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  delete: vi.fn(),
}))

vi.mock('axios', () => ({
  default: { create: mockAxiosCreate },
}))

describe('api', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('creates axios instance with correct base URL', async () => {
    await import('../api')
    expect(mockAxiosCreate).toHaveBeenCalledWith(
      expect.objectContaining({
        baseURL: expect.any(String),
        headers: { 'Content-Type': 'application/json' },
      }),
    )
  })
})

import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useListStore } from '../stores/listStore'

vi.mock('@/services/api', () => {
  const mock = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  }
  return { default: mock }
})

import api from '@/services/api'

describe('listStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('starts empty', () => {
    const store = useListStore()
    expect(store.isEmpty).toBe(true)
    expect(store.lists).toHaveLength(0)
  })

  it('adds a list via API', async () => {
    const store = useListStore()
    const createdList = { id: 'list-1', name: 'Trabalho', createdAt: new Date().toISOString(), taskCount: 0 }
    ;(api.post as ReturnType<typeof vi.fn>).mockResolvedValue({ data: createdList })

    const result = await store.addList('Trabalho')
    expect(store.lists).toHaveLength(1)
    expect(store.lists[0].name).toBe('Trabalho')
    expect(store.lists[0].id).toBe('list-1')
    expect(result).toEqual(createdList)
    expect(api.post).toHaveBeenCalledWith('/tasklists', { name: 'Trabalho' })
  })

  it('renames a list via API', async () => {
    const store = useListStore()
    const updatedList = { id: 'list-1', name: 'Work', createdAt: new Date().toISOString(), taskCount: 0 }
    store.lists = [{ id: 'list-1', name: 'Trabalho', createdAt: new Date().toISOString(), taskCount: 0 }]
    ;(api.put as ReturnType<typeof vi.fn>).mockResolvedValue({ data: updatedList })

    await store.renameList('list-1', 'Work')
    expect(store.lists[0].name).toBe('Work')
    expect(api.put).toHaveBeenCalledWith('/tasklists/list-1', { name: 'Work' })
  })

  it('deletes a list via API', async () => {
    const store = useListStore()
    ;(api.delete as ReturnType<typeof vi.fn>).mockResolvedValue({})
    store.lists = [{ id: 'list-1', name: 'Trabalho', createdAt: new Date().toISOString(), taskCount: 0 }]

    await store.deleteList('list-1')
    expect(store.isEmpty).toBe(true)
    expect(api.delete).toHaveBeenCalledWith('/tasklists/list-1')
  })

  it('gets list by id', () => {
    const store = useListStore()
    store.lists = [{ id: 'list-1', name: 'Estudos', createdAt: new Date().toISOString(), taskCount: 0 }]
    expect(store.getListById('list-1')?.name).toBe('Estudos')
    expect(store.getListById('non-existent')).toBeUndefined()
  })

  it('fetches all lists from API', async () => {
    const store = useListStore()
    const apiLists = [
      { id: 'list-1', name: 'Trabalho', createdAt: new Date().toISOString(), taskCount: 0 },
      { id: 'list-2', name: 'Estudos', createdAt: new Date().toISOString(), taskCount: 3 },
    ]
    ;(api.get as ReturnType<typeof vi.fn>).mockResolvedValue({ data: apiLists })

    await store.fetchAll()
    expect(store.lists).toHaveLength(2)
    expect(store.lists[1].taskCount).toBe(3)
    expect(api.get).toHaveBeenCalledWith('/tasklists')
  })

  it('detects pending tasks', () => {
    const store = useListStore()
    const tasks = [
      { id: 't1', completed: false, taskListId: 'list-1' },
      { id: 't2', completed: true, taskListId: 'list-1' },
    ]
    expect(store.hasPendingTasks('list-1', tasks)).toBe(true)

    const allDone = [
      { id: 't1', completed: true, taskListId: 'list-1' },
      { id: 't2', completed: true, taskListId: 'list-1' },
    ]
    expect(store.hasPendingTasks('list-1', allDone)).toBe(false)
  })

  it('hasPendingTasks returns false for empty tasks', () => {
    const store = useListStore()
    expect(store.hasPendingTasks('list-1', [])).toBe(false)
  })
})

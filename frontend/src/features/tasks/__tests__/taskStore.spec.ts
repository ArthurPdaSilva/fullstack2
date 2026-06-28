import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useTaskStore } from '../stores/taskStore'

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

describe('taskStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('starts with empty tasks', () => {
    const store = useTaskStore()
    expect(store.tasks).toHaveLength(0)
    expect(store.pendingTasks).toHaveLength(0)
    expect(store.completedTasks).toHaveLength(0)
  })

  it('pendingTasks returns only non-completed tasks', () => {
    const store = useTaskStore()
    store.tasks = [
      { id: '1', title: 'Task 1', completed: false, createdAt: '', taskListId: 'list-1' },
      { id: '2', title: 'Task 2', completed: true, createdAt: '', taskListId: 'list-1' },
      { id: '3', title: 'Task 3', completed: false, createdAt: '', taskListId: 'list-1' },
    ]
    expect(store.pendingTasks).toHaveLength(2)
    expect(store.completedTasks).toHaveLength(1)
  })

  it('getTasksByList returns tasks filtered by taskListId', () => {
    const store = useTaskStore()
    store.tasks = [
      { id: 'task-1', title: 'Task 1', completed: false, createdAt: '', taskListId: 'list-1' },
      { id: 'task-2', title: 'Task 2', completed: true, createdAt: '', taskListId: 'list-1' },
      { id: 'task-3', title: 'Task 3', completed: false, createdAt: '', taskListId: 'list-2' },
    ]

    const listTasks = store.getTasksByList('list-1')
    expect(listTasks).toHaveLength(2)
    expect(listTasks[0].id).toBe('task-1')
    expect(listTasks[1].id).toBe('task-2')
  })

  it('getTasksByList returns empty array for list with no tasks', () => {
    const store = useTaskStore()
    store.tasks = [
      { id: 'task-1', title: 'Task 1', completed: false, createdAt: '', taskListId: 'list-2' },
    ]
    const tasks = store.getTasksByList('list-1')
    expect(tasks).toHaveLength(0)
  })

  it('fetchAll maps taskListId from API response', async () => {
    const store = useTaskStore()
    const apiTasks = [
      { id: '1', title: 'Task 1', description: null, completed: false, taskListId: 'list-1', createdAt: '2024-01-01', updatedAt: '2024-01-01' },
    ]
    ;(api.get as ReturnType<typeof vi.fn>).mockResolvedValue({ data: apiTasks })

    await store.fetchAll()
    expect(store.tasks).toHaveLength(1)
    expect(store.tasks[0].taskListId).toBe('list-1')
  })

  it('addTask sends taskListId in the request', async () => {
    const store = useTaskStore()
    const apiResponse = { id: 'task-1', title: 'New Task', description: null, completed: false, taskListId: 'list-1', createdAt: '2024-01-01', updatedAt: '2024-01-01' }
    ;(api.post as ReturnType<typeof vi.fn>).mockResolvedValue({ data: apiResponse })

    const task = await store.addTask('list-1', { title: 'New Task' })
    expect(task.taskListId).toBe('list-1')
    expect(api.post).toHaveBeenCalledWith('/tasks', { title: 'New Task', taskListId: 'list-1' })
  })
})

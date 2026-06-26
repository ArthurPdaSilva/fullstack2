import { useListStore } from '@/features/tasklists/stores/listStore'
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useTaskStore } from '../stores/taskStore'

vi.mock('@/services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('taskStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
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
      { id: '1', title: 'Task 1', completed: false, createdAt: '', listId: '' },
      { id: '2', title: 'Task 2', completed: true, createdAt: '', listId: '' },
      { id: '3', title: 'Task 3', completed: false, createdAt: '', listId: '' },
    ]
    expect(store.pendingTasks).toHaveLength(2)
    expect(store.completedTasks).toHaveLength(1)
  })

  it('getTasksByList returns tasks from list', () => {
    const store = useTaskStore()
    const listStore = useListStore()
    const list = listStore.addList('Test List')
    listStore.addTaskToList(list.id, 'task-1')
    listStore.addTaskToList(list.id, 'task-2')

    store.tasks = [
      { id: 'task-1', title: 'Task 1', completed: false, createdAt: '', listId: list.id },
      { id: 'task-2', title: 'Task 2', completed: true, createdAt: '', listId: list.id },
      { id: 'task-3', title: 'Task 3', completed: false, createdAt: '', listId: 'other' },
    ]

    const listTasks = store.getTasksByList(list.id)
    expect(listTasks).toHaveLength(2)
    expect(listTasks[0].id).toBe('task-1')
    expect(listTasks[1].id).toBe('task-2')
  })

  it('getTasksByList returns empty array for non-existent list', () => {
    const store = useTaskStore()
    const tasks = store.getTasksByList('non-existent')
    expect(tasks).toHaveLength(0)
  })

  it('getTasksByList returns empty array when list has no tasks', () => {
    const store = useTaskStore()
    const listStore = useListStore()
    const list = listStore.addList('Empty List')
    const tasks = store.getTasksByList(list.id)
    expect(tasks).toHaveLength(0)
  })
})

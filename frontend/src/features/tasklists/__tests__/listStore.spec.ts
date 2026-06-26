import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import { useListStore } from '../stores/listStore'

describe('listStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('starts empty', () => {
    const store = useListStore()
    expect(store.isEmpty).toBe(true)
    expect(store.lists).toHaveLength(0)
  })

  it('adds a list', () => {
    const store = useListStore()
    store.addList('Trabalho')
    expect(store.lists).toHaveLength(1)
    expect(store.lists[0].name).toBe('Trabalho')
    expect(store.lists[0].id).toBeTruthy()
    expect(store.lists[0].taskIds).toEqual([])
  })

  it('renames a list', () => {
    const store = useListStore()
    const list = store.addList('Trabalho')
    store.renameList(list.id, 'Work')
    expect(store.lists[0].name).toBe('Work')
  })

  it('deletes a list', () => {
    const store = useListStore()
    const list = store.addList('Trabalho')
    store.deleteList(list.id)
    expect(store.isEmpty).toBe(true)
  })

  it('gets list by id', () => {
    const store = useListStore()
    const list = store.addList('Estudos')
    expect(store.getListById(list.id)?.name).toBe('Estudos')
    expect(store.getListById('non-existent')).toBeUndefined()
  })

  it('manages task ids in list', () => {
    const store = useListStore()
    const list = store.addList('Lista')
    store.addTaskToList(list.id, 'task-1')
    store.addTaskToList(list.id, 'task-2')
    expect(store.getListById(list.id)?.taskIds).toHaveLength(2)

    store.removeTaskFromList(list.id, 'task-1')
    expect(store.getListById(list.id)?.taskIds).toEqual(['task-2'])
  })

  it('detects pending tasks', () => {
    const store = useListStore()
    const list = store.addList('Lista')
    store.addTaskToList(list.id, 't1')
    store.addTaskToList(list.id, 't2')

    const tasks = [
      { id: 't1', completed: false },
      { id: 't2', completed: true },
    ]
    expect(store.hasPendingTasks(list.id, tasks)).toBe(true)

    const allDone = [
      { id: 't1', completed: true },
      { id: 't2', completed: true },
    ]
    expect(store.hasPendingTasks(list.id, allDone)).toBe(false)
  })

  it('does not add duplicate task ids', () => {
    const store = useListStore()
    const list = store.addList('Lista')
    store.addTaskToList(list.id, 'task-1')
    store.addTaskToList(list.id, 'task-1')
    expect(store.getListById(list.id)?.taskIds).toHaveLength(1)
  })

  it('renaming non-existent list does nothing', () => {
    const store = useListStore()
    store.renameList('non-existent', 'New Name')
    expect(store.lists).toHaveLength(0)
  })

  it('adds multiple lists', () => {
    const store = useListStore()
    store.addList('Trabalho')
    store.addList('Estudos')
    store.addList('Pessoal')
    expect(store.lists).toHaveLength(3)
  })

  it('addTaskToList to non-existent list does nothing', () => {
    const store = useListStore()
    store.addTaskToList('non-existent', 'task-1')
    expect(store.lists).toHaveLength(0)
  })

  it('removeTaskFromList from non-existent list does nothing', () => {
    const store = useListStore()
    store.removeTaskFromList('non-existent', 'task-1')
    expect(store.lists).toHaveLength(0)
  })

  it('hasPendingTasks returns false for non-existent list', () => {
    const store = useListStore()
    expect(store.hasPendingTasks('non-existent', [])).toBe(false)
  })

  it('persists lists to localStorage', () => {
    const store = useListStore()
    store.addList('Persisted List')
    const key = `lists_guest`
    const saved = JSON.parse(localStorage.getItem(key) || '[]')
    expect(saved).toHaveLength(1)
    expect(saved[0].name).toBe('Persisted List')
  })
})

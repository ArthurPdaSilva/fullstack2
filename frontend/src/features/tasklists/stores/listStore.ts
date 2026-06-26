import { useAuthStore } from '@/features/auth/stores/authStore'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { TaskList } from '../types'

export const useListStore = defineStore('lists', () => {
  const lists = ref<TaskList[]>([])

  const isEmpty = computed(() => lists.value.length === 0)

  function storageKey(): string {
    const uid = useAuthStore().user?.id
    return `lists_${uid || 'guest'}`
  }

  function save() {
    localStorage.setItem(storageKey(), JSON.stringify(lists.value))
  }

  function load() {
    try {
      const raw = localStorage.getItem(storageKey())
      lists.value = raw ? JSON.parse(raw) : []
    } catch {
      lists.value = []
    }
  }

  function addList(name: string): TaskList {
    const id = crypto.randomUUID()
    const list: TaskList = {
      id,
      name: name.trim(),
      createdAt: new Date().toISOString(),
      taskIds: [],
    }
    lists.value.push(list)
    save()
    return list
  }

  function renameList(id: string, newName: string) {
    const list = lists.value.find((l) => l.id === id)
    if (list) {
      list.name = newName.trim()
      save()
    }
  }

  function deleteList(id: string) {
    lists.value = lists.value.filter((l) => l.id !== id)
    save()
  }

  function getListById(id: string): TaskList | undefined {
    return lists.value.find((l) => l.id === id)
  }

  function addTaskToList(listId: string, taskId: string) {
    const list = lists.value.find((l) => l.id === listId)
    if (list && !list.taskIds.includes(taskId)) {
      list.taskIds.push(taskId)
      save()
    }
  }

  function removeTaskFromList(listId: string, taskId: string) {
    const list = lists.value.find((l) => l.id === listId)
    if (list) {
      list.taskIds = list.taskIds.filter((id) => id !== taskId)
      save()
    }
  }

  function hasPendingTasks(listId: string, tasks: { id: string; completed: boolean }[]): boolean {
    const list = lists.value.find((l) => l.id === listId)
    if (!list) return false
    return list.taskIds.some((id) => {
      const task = tasks.find((t) => t.id === id)
      return task && !task.completed
    })
  }

  load()

  return {
    lists,
    isEmpty,
    load,
    addList,
    renameList,
    deleteList,
    getListById,
    addTaskToList,
    removeTaskFromList,
    hasPendingTasks,
  }
})

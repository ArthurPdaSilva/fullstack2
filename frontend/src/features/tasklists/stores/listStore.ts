import api from '@/services/api'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { TaskList, TaskListRequest } from '../types'

export const useListStore = defineStore('lists', () => {
  const lists = ref<TaskList[]>([])

  const isEmpty = computed(() => lists.value.length === 0)

  async function fetchAll() {
    const { data } = await api.get<TaskList[]>('/tasklists')
    lists.value = data
  }

  async function addList(name: string): Promise<TaskList> {
    const { data } = await api.post<TaskList>('/tasklists', { name } as TaskListRequest)
    lists.value.push(data)
    return data
  }

  async function renameList(id: string, newName: string) {
    const { data } = await api.put<TaskList>(`/tasklists/${id}`, { name: newName } as TaskListRequest)
    const idx = lists.value.findIndex((l) => l.id === id)
    if (idx !== -1) {
      lists.value[idx] = data
    }
  }

  async function deleteList(id: string) {
    await api.delete(`/tasklists/${id}`)
    lists.value = lists.value.filter((l) => l.id !== id)
  }

  function getListById(id: string): TaskList | undefined {
    return lists.value.find((l) => l.id === id)
  }

  function hasPendingTasks(listId: string, tasks: { id: string; completed: boolean; taskListId?: string | null }[]): boolean {
    return tasks.some((t) => t.taskListId === listId && !t.completed)
  }

  return {
    lists,
    isEmpty,
    fetchAll,
    addList,
    renameList,
    deleteList,
    getListById,
    hasPendingTasks,
  }
})

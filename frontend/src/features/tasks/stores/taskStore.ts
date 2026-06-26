import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'
import { useListStore } from '@/features/tasklists/stores/listStore'
import type { Task, TaskRequest, TaskResponse } from '../types'

export const useTaskStore = defineStore('tasks', () => {
  const tasks = ref<Task[]>([])

  const pendingTasks = computed(() => tasks.value.filter((t) => !t.completed))
  const completedTasks = computed(() => tasks.value.filter((t) => t.completed))

  function getTasksByList(listId: string): Task[] {
    const listStore = useListStore()
    const list = listStore.getListById(listId)
    if (!list) return []
    return list.taskIds
      .map((id) => tasks.value.find((t) => t.id === id))
      .filter((t): t is Task => !!t)
  }

  async function fetchAll(token?: string) {
    const headers: Record<string, string> = {}
    if (token) {
      headers.Authorization = `Bearer ${token}`
    }
    const { data } = await api.get<TaskResponse[]>('/tasks', { headers })
    tasks.value = data.map((t) => ({
      ...t,
      description: t.description || undefined,
      listId: '',
    }))
  }

  async function addTask(listId: string, payload: TaskRequest, token?: string): Promise<Task> {
    const headers: Record<string, string> = {}
    if (token) {
      headers.Authorization = `Bearer ${token}`
    }
    const { data } = await api.post<TaskResponse>('/tasks', payload, { headers })
    const task: Task = {
      ...data,
      description: data.description || undefined,
      listId,
    }
    tasks.value.push(task)
    const listStore = useListStore()
    listStore.addTaskToList(listId, task.id)
    return task
  }

  async function updateTask(taskId: string, payload: TaskRequest): Promise<Task> {
    const { data } = await api.put<TaskResponse>(`/tasks/${taskId}`, payload)
    const idx = tasks.value.findIndex((t) => t.id === taskId)
    if (idx !== -1) {
      tasks.value[idx] = {
        ...data,
        description: data.description || undefined,
        listId: tasks.value[idx].listId,
      }
    }
    return tasks.value[idx]
  }

  async function toggleTask(taskId: string) {
    const task = tasks.value.find((t) => t.id === taskId)
    if (!task) return
    await updateTask(taskId, {
      title: task.title,
      completed: !task.completed,
    })
  }

  async function removeTask(listId: string, taskId: string) {
    await api.delete(`/tasks/${taskId}`)
    tasks.value = tasks.value.filter((t) => t.id !== taskId)
    const listStore = useListStore()
    listStore.removeTaskFromList(listId, taskId)
  }

  return {
    tasks,
    pendingTasks,
    completedTasks,
    getTasksByList,
    fetchAll,
    addTask,
    updateTask,
    toggleTask,
    removeTask,
  }
})

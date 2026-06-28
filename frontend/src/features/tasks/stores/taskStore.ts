import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'
import type { Task, TaskRequest, TaskResponse } from '../types'

export const useTaskStore = defineStore('tasks', () => {
  const tasks = ref<Task[]>([])

  const pendingTasks = computed(() => tasks.value.filter((t) => !t.completed))
  const completedTasks = computed(() => tasks.value.filter((t) => t.completed))

  function getTasksByList(listId: string): Task[] {
    return tasks.value.filter((t) => t.taskListId === listId)
  }

  async function fetchAll() {
    const { data } = await api.get<TaskResponse[]>('/tasks')
    tasks.value = data.map((t) => ({
      ...t,
      description: t.description || undefined,
      taskListId: t.taskListId,
    }))
  }

  async function addTask(listId: string, payload: TaskRequest): Promise<Task> {
    const { data } = await api.post<TaskResponse>('/tasks', { ...payload, taskListId: listId })
    const task: Task = {
      ...data,
      description: data.description || undefined,
      taskListId: data.taskListId,
    }
    tasks.value.push(task)
    return task
  }

  async function updateTask(taskId: string, payload: TaskRequest): Promise<Task> {
    const { data } = await api.put<TaskResponse>(`/tasks/${taskId}`, payload)
    const idx = tasks.value.findIndex((t) => t.id === taskId)
    if (idx !== -1) {
      tasks.value[idx] = {
        ...data,
        description: data.description || undefined,
        taskListId: data.taskListId,
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

  async function removeTask(taskId: string) {
    await api.delete(`/tasks/${taskId}`)
    tasks.value = tasks.value.filter((t) => t.id !== taskId)
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

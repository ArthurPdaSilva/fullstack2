export interface Task {
  id: string
  title: string
  description?: string
  completed: boolean
  createdAt: string
  updatedAt?: string
  taskListId: string | null
}

export interface TaskRequest {
  title: string
  description?: string
  completed?: boolean
  taskListId?: string | null
}

export interface TaskResponse {
  id: string
  title: string
  description: string | null
  completed: boolean
  taskListId: string | null
  createdAt: string
  updatedAt: string
}

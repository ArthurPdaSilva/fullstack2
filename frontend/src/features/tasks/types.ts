export interface Task {
  id: string
  title: string
  description?: string
  completed: boolean
  createdAt: string
  updatedAt?: string
  listId: string
}

export interface TaskRequest {
  title: string
  description?: string
  completed?: boolean
}

export interface TaskResponse {
  id: string
  title: string
  description: string | null
  completed: boolean
  createdAt: string
  updatedAt: string
}

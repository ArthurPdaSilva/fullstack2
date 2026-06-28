export interface TaskList {
  id: string
  name: string
  createdAt: string
  taskCount: number
}

export interface TaskListRequest {
  name: string
}

export interface TaskListResponse {
  id: string
  name: string
  createdAt: string
  taskCount: number
}

export interface User {
  id: string
  name: string
  email: string
}

export interface AuthResponse {
  token: string
  type: string
  expiresIn: number
  refreshToken: string
  refreshExpiresIn: number
}

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterPayload {
  name: string
  email: string
  password: string
}

import type { AxiosError } from 'axios'

interface ApiErrorResponse {
  status?: number
  message?: string
  errors?: string[]
}

const errorMap: Record<string, string> = {
  'Invalid email or password': 'Email ou senha inválidos',
  'Email already registered': 'Este email já está cadastrado',
  'User not found': 'Usuário não encontrado',
  'Task not found or access denied': 'Tarefa não encontrada',
  'An unexpected error occurred': 'Erro interno do servidor',
  'Validation failed': 'Dados inválidos',
  'Title is required': 'O título é obrigatório',
  'Name is required': 'O nome é obrigatório',
  'Email is required': 'O email é obrigatório',
  'Password is required': 'A senha é obrigatória',
  'Invalid email format': 'Formato de email inválido',
}

const fieldErrorMap: Record<string, string> = {
  'Title is required': 'O título é obrigatório',
  'Name is required': 'O nome é obrigatório',
  'Email is required': 'O email é obrigatório',
  'Password is required': 'A senha é obrigatória',
  'Invalid email format': 'Formato de email inválido',
}

function translateMessage(msg: string): string {
  return errorMap[msg] || msg
}

export function translateApiError(error: unknown): string | string[] {
  const axiosError = error as AxiosError<ApiErrorResponse>

  if (!axiosError.response) {
    if (axiosError.code === 'ECONNABORTED') {
      return 'O servidor não respondeu. Tente novamente.'
    }
    return 'Erro de conexão com o servidor. Verifique se o backend está rodando.'
  }

  const data = axiosError.response.data
  if (!data) return 'Erro desconhecido'

  if (data.errors && data.errors.length > 0) {
    return data.errors.map((msg) => fieldErrorMap[msg] || msg)
  }

  if (data.message) {
    return translateMessage(data.message)
  }

  return 'Erro desconhecido'
}

import { describe, expect, it } from 'vitest'
import { translateApiError } from '../errorHandler'

describe('translateApiError', () => {
  it('returns connection error when no response', () => {
    const error = { code: 'ECONNABORTED', response: undefined }
    const result = translateApiError(error)
    expect(result).toBe('O servidor não respondeu. Tente novamente.')
  })

  it('returns generic connection error when no code', () => {
    const error = { response: undefined }
    const result = translateApiError(error)
    expect(result).toBe('Erro de conexão com o servidor. Verifique se o backend está rodando.')
  })

  it('returns unknown error when no data', () => {
    const error = { response: { data: undefined } }
    const result = translateApiError(error)
    expect(result).toBe('Erro desconhecido')
  })

  it('translates field errors', () => {
    const error = {
      response: {
        data: {
          errors: ['Title is required', 'Name is required'],
        },
      },
    }
    const result = translateApiError(error)
    expect(Array.isArray(result)).toBe(true)
    expect(result).toEqual(['O título é obrigatório', 'O nome é obrigatório'])
  })

  it('translates known error messages', () => {
    const error = {
      response: {
        data: { message: 'Invalid email or password' },
      },
    }
    const result = translateApiError(error)
    expect(result).toBe('Email ou senha inválidos')
  })

  it('returns original message when not in errorMap', () => {
    const error = {
      response: {
        data: { message: 'Some unknown error' },
      },
    }
    const result = translateApiError(error)
    expect(result).toBe('Some unknown error')
  })

  it('translates "Email already registered"', () => {
    const error = {
      response: {
        data: { message: 'Email already registered' },
      },
    }
    const result = translateApiError(error)
    expect(result).toBe('Este email já está cadastrado')
  })

  it('translates "Task not found or access denied"', () => {
    const error = {
      response: {
        data: { message: 'Task not found or access denied' },
      },
    }
    const result = translateApiError(error)
    expect(result).toBe('Tarefa não encontrada')
  })

  it('translates "An unexpected error occurred"', () => {
    const error = {
      response: {
        data: { message: 'An unexpected error occurred' },
      },
    }
    const result = translateApiError(error)
    expect(result).toBe('Erro interno do servidor')
  })
})

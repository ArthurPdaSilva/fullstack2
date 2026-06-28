import { describe, expect, it, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { createPinia, setActivePinia } from 'pinia'
import LoginForm from '../components/LoginForm.vue'

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
}))

const vuetify = createVuetify({ components, directives })

function mountLoginForm() {
  return mount(LoginForm, {
    global: {
      plugins: [vuetify, createPinia()],
    },
  })
}

describe('LoginForm', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('clears fields when toggling from login to register mode', async () => {
    const wrapper = mountLoginForm()

    const emailInput = wrapper.find('input[type="email"]')
    const passwordInput = wrapper.find('input[type="password"]')
    await emailInput.setValue('test@email.com')
    await passwordInput.setValue('123456')

    const toggleBtn = wrapper.findAllComponents({ name: 'v-btn' }).at(-1)!
    await toggleBtn.trigger('click')

    expect(wrapper.find<HTMLInputElement>('input[type="email"]').element.value).toBe('')
    expect(wrapper.find<HTMLInputElement>('input[type="password"]').element.value).toBe('')
    const nameInput = wrapper.find<HTMLInputElement>('input[type="text"]')
    expect(nameInput.exists()).toBe(true)
    expect(nameInput.element.value).toBe('')
  })

  it('clears fields when toggling from register to login mode', async () => {
    const wrapper = mountLoginForm()

    const toggleBtn = wrapper.findAllComponents({ name: 'v-btn' }).at(-1)!
    await toggleBtn.trigger('click')

    const nameInput = wrapper.find<HTMLInputElement>('input[type="text"]')
    const emailInput = wrapper.find('input[type="email"]')
    const passwordInput = wrapper.find('input[type="password"]')
    await nameInput.setValue('John')
    await emailInput.setValue('john@email.com')
    await passwordInput.setValue('123456')

    await toggleBtn.trigger('click')

    expect(wrapper.find<HTMLInputElement>('input[type="email"]').element.value).toBe('')
    expect(wrapper.find<HTMLInputElement>('input[type="password"]').element.value).toBe('')
  })

  it('clears error when toggling modes', async () => {
    const wrapper = mountLoginForm()

    ;(wrapper.vm as unknown as { error: string }).error = 'Credenciais inválidas'
    await wrapper.vm.$nextTick()

    expect(wrapper.find('.v-alert').exists()).toBe(true)

    const toggleBtn = wrapper.findAllComponents({ name: 'v-btn' }).at(-1)!
    await toggleBtn.trigger('click')

    expect(wrapper.find('.v-alert').exists()).toBe(false)
  })
})

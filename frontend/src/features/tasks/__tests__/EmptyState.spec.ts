import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import EmptyState from '../components/EmptyState.vue'

const vuetify = createVuetify({ components, directives })

describe('EmptyState', () => {
  it('renders the empty state message', () => {
    const wrapper = mount(EmptyState, {
      global: { plugins: [vuetify] },
    })
    expect(wrapper.text()).toContain('Nenhuma tarefa aqui')
    expect(wrapper.text()).toContain('Adicione sua primeira tarefa a esta lista')
  })

  it('renders the clipboard icon', () => {
    const wrapper = mount(EmptyState, {
      global: { plugins: [vuetify] },
    })
    const icon = wrapper.findComponent({ name: 'v-icon' })
    expect(icon.exists()).toBe(true)
  })
})

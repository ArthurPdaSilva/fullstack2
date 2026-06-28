import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import TaskItem from '../components/TaskItem.vue'
import type { Task } from '../types'

const vuetify = createVuetify({ components, directives })

function mountTaskItem(task: Task) {
  return mount(TaskItem, {
    props: { task },
    global: { plugins: [vuetify] },
  })
}

describe('TaskItem', () => {
  const baseTask: Task = {
    id: '1',
    title: 'Test Task',
    completed: false,
    createdAt: '2024-01-01T00:00:00Z',
    taskListId: 'list-1',
  }

  it('renders the task title', () => {
    const wrapper = mountTaskItem(baseTask)
    expect(wrapper.text()).toContain('Test Task')
  })

  it('renders description when provided', () => {
    const wrapper = mountTaskItem({ ...baseTask, description: 'Task description' })
    expect(wrapper.text()).toContain('Task description')
  })

  it('does not render description when not provided', () => {
    const wrapper = mountTaskItem(baseTask)
    expect(wrapper.text()).not.toContain('undefined')
  })

  it('emits toggle event when checkbox is clicked', async () => {
    const wrapper = mountTaskItem(baseTask)
    const checkbox = wrapper.findComponent({ name: 'v-checkbox-btn' })
    await checkbox.trigger('change')
    expect(wrapper.emitted('toggle')).toBeTruthy()
    expect(wrapper.emitted('toggle')![0]).toEqual(['1'])
  })

  it('emits edit event when edit button is clicked', async () => {
    const wrapper = mountTaskItem(baseTask)
    const buttons = wrapper.findAllComponents({ name: 'v-btn' })
    const editBtn = buttons.find((b) => b.text() === '')
    if (editBtn) {
      await editBtn.trigger('click')
    } else {
      const editIcon = wrapper.findComponent({ name: 'v-icon' })
      await editIcon.trigger('click')
    }
  })

  it('emits delete event when delete button is clicked', async () => {
    const wrapper = mountTaskItem(baseTask)
    const deleteBtn = wrapper.findAllComponents({ name: 'v-btn' }).at(-1)
    if (deleteBtn) {
      await deleteBtn.trigger('click')
    }
  })

  it('applies completed class when task is completed', () => {
    const wrapper = mountTaskItem({ ...baseTask, completed: true })
    expect(wrapper.find('.task-completed').exists()).toBe(true)
  })

  it('does not apply completed class when task is not completed', () => {
    const wrapper = mountTaskItem(baseTask)
    expect(wrapper.find('.task-completed').exists()).toBe(false)
  })
})

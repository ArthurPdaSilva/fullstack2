import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import ListCard from '../components/ListCard.vue'
import type { TaskList } from '../types'

const vuetify = createVuetify({ components, directives })

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'dashboard', component: { template: '<div>Dashboard</div>' } },
    { path: '/tasks/:listId', name: 'tasks', component: { template: '<div>Tasks</div>' } },
  ],
})

function mountListCard(list: TaskList, taskCount = 0) {
  return mount(ListCard, {
    props: { list, taskCount },
    global: { plugins: [vuetify, router] },
  })
}

describe('ListCard', () => {
  const baseList: TaskList = {
    id: 'list-1',
    name: 'Trabalho',
    createdAt: '2024-01-01T00:00:00Z',
    taskCount: 0,
  }

  it('renders the list name', () => {
    const wrapper = mountListCard(baseList)
    expect(wrapper.text()).toContain('Trabalho')
  })

  it('renders task count in singular', () => {
    const wrapper = mountListCard(baseList, 1)
    expect(wrapper.text()).toContain('1 tarefa')
  })

  it('renders task count in plural', () => {
    const wrapper = mountListCard(baseList, 3)
    expect(wrapper.text()).toContain('3 tarefas')
  })

  it('renders task count zero', () => {
    const wrapper = mountListCard(baseList, 0)
    expect(wrapper.text()).toContain('0 tarefas')
  })

  it('emits edit event when edit button is clicked', async () => {
    const wrapper = mountListCard(baseList)
    const editBtn = wrapper.findAllComponents({ name: 'v-btn' }).at(0)
    expect(editBtn).toBeTruthy()
    await editBtn!.trigger('click')
    expect(wrapper.emitted('edit')).toBeTruthy()
    expect(wrapper.emitted('edit')![0]).toEqual([baseList])
  })

  it('emits delete event when delete button is clicked', async () => {
    const wrapper = mountListCard(baseList)
    const deleteBtn = wrapper.findAllComponents({ name: 'v-btn' }).at(-1)
    expect(deleteBtn).toBeTruthy()
    await deleteBtn!.trigger('click')
    expect(wrapper.emitted('delete')).toBeTruthy()
    expect(wrapper.emitted('delete')![0]).toEqual([baseList])
  })
})

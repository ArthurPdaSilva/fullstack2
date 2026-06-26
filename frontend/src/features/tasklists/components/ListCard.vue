<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { TaskList } from '../types'

defineProps<{
  list: TaskList
  taskCount: number
}>()

const emit = defineEmits<{
  edit: [list: TaskList]
  delete: [list: TaskList]
}>()

const router = useRouter()

const listIcons: Record<string, string> = {
  trabalho: 'mdi-briefcase',
  estudos: 'mdi-book-open-variant',
  pessoal: 'mdi-account',
  compras: 'mdi-cart',
  saúde: 'mdi-heart-pulse',
  finanças: 'mdi-currency-usd',
  projetos: 'mdi-code-tags',
  leitura: 'mdi-book',
}
</script>

<template>
  <v-card
    hover
    :prepend-icon="listIcons[list.name.toLowerCase()] || 'mdi-format-list-bulleted'"
    @click="router.push({ name: 'tasks', params: { listId: list.id } })"
    class="list-card"
  >
    <template #title>
      <span class="font-weight-medium text-body-1">{{ list.name }}</span>
    </template>
    <template #subtitle>
      <span class="text-caption">{{ taskCount }} tarefa{{ taskCount !== 1 ? 's' : '' }}</span>
    </template>
    <template #actions>
      <v-btn icon variant="text" color="grey" size="small" @click.stop="emit('edit', list)">
        <v-icon>mdi-pencil</v-icon>
      </v-btn>
      <v-btn icon variant="text" color="grey" size="small" @click.stop="emit('delete', list)">
        <v-icon>mdi-delete</v-icon>
      </v-btn>
    </template>
  </v-card>
</template>

<style scoped>
.list-card {
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}
.list-card:hover {
  transform: translateY(-2px);
}
</style>

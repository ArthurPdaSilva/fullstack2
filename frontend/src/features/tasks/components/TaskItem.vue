<script setup lang="ts">
import type { Task } from '../types'

defineProps<{
  task: Task
}>()

const emit = defineEmits<{
  toggle: [id: string]
  edit: [task: Task]
  delete: [id: string]
}>()
</script>

<template>
  <v-card :class="{ 'task-completed': task.completed }" class="task-item">
    <div class="d-flex align-center pa-4">
      <v-checkbox-btn
        :model-value="task.completed"
        color="success"
        @change="emit('toggle', task.id)"
        hide-details
      />
      <div class="flex-grow-1 ml-3">
        <div
          class="text-body-1"
          :class="{
            'text-decoration-line-through text-medium-emphasis': task.completed,
          }"
        >
          {{ task.title }}
        </div>
        <div v-if="task.description" class="text-caption text-grey mt-1">
          {{ task.description }}
        </div>
      </div>
      <v-btn
        icon
        variant="text"
        color="grey"
        size="small"
        class="mr-1"
        @click="emit('edit', task)"
      >
        <v-icon>mdi-pencil</v-icon>
      </v-btn>
      <v-btn icon variant="text" color="grey" size="small" @click="emit('delete', task.id)">
        <v-icon>mdi-delete</v-icon>
      </v-btn>
    </div>
  </v-card>
</template>

<style scoped>
.task-item {
  transition: opacity 0.2s;
}
.task-completed {
  opacity: 0.6;
}
</style>

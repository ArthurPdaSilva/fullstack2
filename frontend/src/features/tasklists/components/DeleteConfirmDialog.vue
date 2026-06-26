<script setup lang="ts">
import { useTaskStore } from '@/features/tasks/stores/taskStore'
import { computed } from 'vue'
import { useListStore } from '../stores/listStore'

const props = defineProps<{
  modelValue: boolean
  list: { id: string; name: string }
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirmed: []
}>()

const listStore = useListStore()
const taskStore = useTaskStore()

const hasPending = computed(() => listStore.hasPendingTasks(props.list.id, taskStore.tasks))
</script>

<template>
  <v-dialog
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    max-width="480"
    persistent
  >
    <v-card rounded="xl">
      <v-card-title class="text-h5 font-weight-bold pt-6 px-6 d-flex align-center">
        <v-icon color="error" class="mr-2">mdi-alert-circle</v-icon>
        Excluir Lista
      </v-card-title>
      <v-card-text class="px-6 pb-0">
        <p class="text-body-1 mb-3">
          Tem certeza que deseja excluir a lista
          <strong>{{ list.name }}</strong
          >?
        </p>
        <v-alert
          v-if="hasPending"
          type="warning"
          variant="tonal"
          class="mb-2"
          density="compact"
          icon="mdi-alert"
        >
          Esta lista contém tarefas pendentes que também serão removidas permanentemente.
        </v-alert>
      </v-card-text>
      <v-card-actions class="pa-6 pt-2">
        <v-spacer />
        <v-btn variant="text" color="grey" @click="emit('update:modelValue', false)">
          Cancelar
        </v-btn>
        <v-btn color="error" @click="emit('confirmed')" rounded="lg"> Sim, Excluir </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

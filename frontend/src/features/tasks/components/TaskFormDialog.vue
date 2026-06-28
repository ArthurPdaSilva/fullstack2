<script setup lang="ts">
import { ref, watch } from 'vue'
import { useTaskStore } from '../stores/taskStore'
import { translateApiError } from '@/services/errorHandler'

const props = defineProps<{
  modelValue: boolean
  listId: string
  task?: { id: string; title: string; description?: string } | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  saved: []
}>()

const taskStore = useTaskStore()
const title = ref('')
const description = ref('')
const valid = ref(false)
const formRef = ref()
const error = ref('')

const titleRules = [
  (v: string) => !!v || 'Título é obrigatório',
  (v: string) => {
    const exists = taskStore.tasks.some(
      (t) => t.taskListId === props.listId && t.title === v.trim() && t.id !== props.task?.id,
    )
    return !exists || 'Já existe uma tarefa com este título'
  },
]

watch(
  () => props.modelValue,
  (open) => {
    if (open) {
      title.value = props.task?.title || ''
      description.value = props.task?.description || ''
    }
  },
)

async function save() {
  error.value = ''
  const { valid: isValid } = await formRef.value.validate()
  if (!isValid) return

  try {
    if (props.task) {
      await taskStore.updateTask(props.task.id, {
        title: title.value,
        description: description.value || undefined,
      })
    } else {
      await taskStore.addTask(props.listId, {
        title: title.value,
        description: description.value || undefined,
      })
    }
    emit('saved')
    emit('update:modelValue', false)
  } catch (e: unknown) {
    const result = translateApiError(e)
    error.value = Array.isArray(result) ? result[0] : result
  }
}
</script>

<template>
  <v-dialog
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    max-width="480"
    persistent
  >
    <v-card rounded="xl">
      <v-card-title class="text-h5 font-weight-bold pt-6 px-6">
        <v-icon class="mr-2" color="primary">mdi-{{ task ? 'pencil' : 'plus-circle' }}</v-icon>
        {{ task ? 'Editar Tarefa' : 'Nova Tarefa' }}
      </v-card-title>
      <v-card-text class="px-6 pb-0">
        <v-form ref="formRef" v-model="valid" validate-on="submit" @submit.prevent="save">
          <v-text-field
            v-model="title"
            label="Título"
            :rules="titleRules"
            autofocus
            required
            prepend-inner-icon="mdi-format-title"
          />
          <v-textarea
            v-model="description"
            label="Descrição (opcional)"
            rows="3"
            prepend-inner-icon="mdi-text"
          />
          <v-alert v-if="error" type="error" variant="tonal" class="mt-2" closable>
            {{ error }}
          </v-alert>
        </v-form>
      </v-card-text>
      <v-card-actions class="pa-6 pt-0">
        <v-spacer />
        <v-btn variant="text" color="grey" @click="emit('update:modelValue', false)">
          Cancelar
        </v-btn>
        <v-btn color="primary" :disabled="!title.trim()" @click="save" rounded="lg">
          {{ task ? 'Salvar' : 'Adicionar' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

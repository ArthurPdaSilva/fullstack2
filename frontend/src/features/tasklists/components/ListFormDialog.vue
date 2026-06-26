<script setup lang="ts">
import { ref, watch } from 'vue'
import { useListStore } from '../stores/listStore'

const props = defineProps<{
  modelValue: boolean
  list?: { id: string; name: string } | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  saved: []
}>()

const listStore = useListStore()
const name = ref('')
const valid = ref(false)
const formRef = ref()

const nameRules = [
  (v: string) => !!v || 'Nome é obrigatório',
  (v: string) =>
    !listStore.lists.find((l) => l.name === v.trim() && l.id !== props.list?.id) ||
    'Já existe uma lista com este nome',
]

watch(
  () => props.modelValue,
  (open) => {
    if (open) {
      name.value = props.list?.name || ''
    }
  },
)

async function save() {
  const { valid: isValid } = await formRef.value.validate()
  if (!isValid) return

  if (props.list) {
    listStore.renameList(props.list.id, name.value)
  } else {
    listStore.addList(name.value)
  }
  emit('saved')
  emit('update:modelValue', false)
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
        <v-icon class="mr-2" color="primary">mdi-{{ list ? 'pencil' : 'plus-circle' }}</v-icon>
        {{ list ? 'Renomear Lista' : 'Nova Lista' }}
      </v-card-title>
      <v-card-text class="px-6 pb-0">
        <v-form ref="formRef" v-model="valid" @submit.prevent="save">
          <v-text-field
            v-model="name"
            label="Nome da lista"
            :rules="nameRules"
            autofocus
            required
            prepend-inner-icon="mdi-format-list-bulleted"
          />
        </v-form>
      </v-card-text>
      <v-card-actions class="pa-6 pt-0">
        <v-spacer />
        <v-btn variant="text" color="grey" @click="emit('update:modelValue', false)">
          Cancelar
        </v-btn>
        <v-btn color="primary" :disabled="!name.trim()" @click="save" rounded="lg">
          {{ list ? 'Salvar' : 'Criar' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

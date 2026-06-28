<script setup lang="ts">
import { onMounted, ref } from 'vue'
import DeleteConfirmDialog from '../components/DeleteConfirmDialog.vue'
import ListCard from '../components/ListCard.vue'
import ListFormDialog from '../components/ListFormDialog.vue'
import { useListStore } from '../stores/listStore'
import type { TaskList } from '../types'

const listStore = useListStore()

const showFormDialog = ref(false)
const showDeleteDialog = ref(false)
const editingList = ref<TaskList | null>(null)
const deletingList = ref<TaskList | null>(null)

function taskCount(listId: string) {
  const list = listStore.getListById(listId)
  if (!list) return 0
  return list.taskCount
}

onMounted(() => {
  listStore.fetchAll()
})

function openNew() {
  editingList.value = null
  showFormDialog.value = true
}

function openEdit(list: TaskList) {
  editingList.value = list
  showFormDialog.value = true
}

function openDelete(list: TaskList) {
  deletingList.value = list
  showDeleteDialog.value = true
}

async function confirmDelete() {
  if (!deletingList.value) return
  await listStore.deleteList(deletingList.value.id)
  showDeleteDialog.value = false
  deletingList.value = null
}
</script>

<template>
  <v-container class="py-6">
    <div class="d-flex align-center mb-6">
      <div>
        <h1 class="text-h4 font-weight-bold">Minhas Listas</h1>
        <p class="text-body-1 text-medium-emphasis mt-1">Gerencie suas tarefas por categorias</p>
      </div>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openNew" size="large">
        Nova Lista
      </v-btn>
    </div>

    <v-row v-if="!listStore.isEmpty">
      <v-col v-for="list in listStore.lists" :key="list.id" cols="12" sm="6" md="4" lg="3">
        <ListCard
          :list="list"
          :task-count="taskCount(list.id)"
          @edit="openEdit"
          @delete="openDelete"
        />
      </v-col>
    </v-row>

    <div v-else class="d-flex flex-column align-center justify-center pa-12 mt-6">
      <v-icon size="96" color="grey-lighten-2" class="mb-4">mdi-view-dashboard-outline</v-icon>
      <h2 class="text-h5 text-grey-darken-1 mb-2">Nenhuma lista</h2>
      <p class="text-body-1 text-grey mb-6">
        Crie sua primeira lista para começar a organizar suas tarefas
      </p>
      <v-btn color="primary" size="large" prepend-icon="mdi-plus" @click="openNew">
        Criar Lista
      </v-btn>
    </div>

    <ListFormDialog v-model="showFormDialog" :list="editingList" @saved="editingList = null" />
    <DeleteConfirmDialog
      v-if="deletingList"
      v-model="showDeleteDialog"
      :list="deletingList"
      @confirmed="confirmDelete"
    />
  </v-container>
</template>

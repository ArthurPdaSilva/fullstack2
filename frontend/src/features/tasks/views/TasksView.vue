<script setup lang="ts">
import { useAuthStore } from '@/features/auth/stores/authStore'
import { useListStore } from '@/features/tasklists/stores/listStore'
import { translateApiError } from '@/services/errorHandler'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EmptyState from '../components/EmptyState.vue'
import TaskFormDialog from '../components/TaskFormDialog.vue'
import TaskItem from '../components/TaskItem.vue'
import { useTaskStore } from '../stores/taskStore'
import type { Task } from '../types'

const route = useRoute()
const router = useRouter()
const listStore = useListStore()
const taskStore = useTaskStore()
const authStore = useAuthStore()

const listId = computed(() => route.params.listId as string)
const list = computed(() => listStore.getListById(listId.value))
const listTasks = computed(() => taskStore.getTasksByList(listId.value))

const showFormDialog = ref(false)
const editingTask = ref<Task | null>(null)
const error = ref('')

const tasksLoading = ref(true)

onMounted(async () => {
  if (!list.value) {
    router.push({ name: 'dashboard' })
    return
  }
  try {
    await taskStore.fetchAll(authStore.token || undefined)
  } catch (e: unknown) {
    const result = translateApiError(e)
    error.value = Array.isArray(result) ? result[0] : result
  } finally {
    tasksLoading.value = false
  }
})

function openNew() {
  editingTask.value = null
  showFormDialog.value = true
}

function openEdit(task: Task) {
  editingTask.value = task
  showFormDialog.value = true
}

async function toggleTask(taskId: string) {
  try {
    await taskStore.toggleTask(taskId)
  } catch (e: unknown) {
    const result = translateApiError(e)
    error.value = Array.isArray(result) ? result[0] : result
  }
}

async function deleteTask(taskId: string) {
  try {
    await taskStore.removeTask(listId.value, taskId)
  } catch (e: unknown) {
    const result = translateApiError(e)
    error.value = Array.isArray(result) ? result[0] : result
  }
}

function goBack() {
  router.push({ name: 'dashboard' })
}

const pendingCount = computed(() => listTasks.value.filter((t) => !t.completed).length)
const completedCount = computed(() => listTasks.value.filter((t) => t.completed).length)
</script>

<template>
  <v-container v-if="list" class="py-6">
    <div class="d-flex align-center mb-2">
      <v-btn icon variant="text" color="grey" class="mr-1" @click="goBack">
        <v-icon>mdi-arrow-left</v-icon>
      </v-btn>
      <div>
        <h2 class="text-h5 font-weight-bold">{{ list.name }}</h2>
        <div class="d-flex align-center ga-3 text-caption text-medium-emphasis">
          <span>{{ listTasks.length }} tarefa{{ listTasks.length !== 1 ? 's' : '' }}</span>
          <span v-if="pendingCount > 0"
            >{{ pendingCount }} pendente{{ pendingCount !== 1 ? 's' : '' }}</span
          >
          <span v-if="completedCount > 0"
            >{{ completedCount }} concluída{{ completedCount !== 1 ? 's' : '' }}</span
          >
        </div>
      </div>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openNew" size="large">
        Nova Tarefa
      </v-btn>
    </div>

    <v-divider class="mb-4" />

    <v-alert v-if="error" type="warning" variant="tonal" class="mb-3" closable>
      {{ error }}
    </v-alert>

    <v-progress-linear v-if="tasksLoading" indeterminate color="primary" />

    <div v-else-if="listTasks.length" class="d-flex flex-column ga-3">
      <div v-if="pendingCount > 0">
        <h3 class="text-subtitle-2 text-grey-darken-1 mb-2 font-weight-medium">Pendentes</h3>
        <div class="d-flex flex-column ga-2">
          <TaskItem
            v-for="task in listTasks.filter((t) => !t.completed)"
            :key="task.id"
            :task="task"
            @toggle="toggleTask"
            @edit="openEdit"
            @delete="deleteTask"
          />
        </div>
      </div>
      <div v-if="completedCount > 0" class="mt-2">
        <h3 class="text-subtitle-2 text-grey-darken-1 mb-2 font-weight-medium">Concluídas</h3>
        <div class="d-flex flex-column ga-2">
          <TaskItem
            v-for="task in listTasks.filter((t) => t.completed)"
            :key="task.id"
            :task="task"
            @toggle="toggleTask"
            @edit="openEdit"
            @delete="deleteTask"
          />
        </div>
      </div>
    </div>

    <EmptyState v-else />

    <TaskFormDialog
      v-model="showFormDialog"
      :list-id="listId"
      :task="editingTask"
      @saved="editingTask = null"
    />
  </v-container>
</template>

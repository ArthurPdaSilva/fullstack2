<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/features/auth/stores/authStore'

const authStore = useAuthStore()
const router = useRouter()

function logout() {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <v-app-bar flat color="white" class="border-b">
    <v-app-bar-title class="d-flex align-center">
      <v-icon color="primary" class="mr-2">mdi-checkbox-marked-circle-plus-outline</v-icon>
      <span class="font-weight-bold text-primary">JTech</span>
      <span class="font-weight-light ml-1">Tasklist</span>
    </v-app-bar-title>
    <v-spacer />
    <div class="d-flex align-center mr-2">
      <v-avatar color="primary" size="32" class="mr-2">
        <span class="text-caption text-white font-weight-bold">
          {{ authStore.user?.name?.charAt(0).toUpperCase() }}
        </span>
      </v-avatar>
      <span class="text-body-2 mr-4 text-medium-emphasis">{{ authStore.user?.name }}</span>
    </div>
    <v-btn icon variant="text" color="grey-darken-1" @click="logout" title="Sair">
      <v-icon>mdi-logout</v-icon>
    </v-btn>
  </v-app-bar>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/features/auth/stores/authStore';
import AppHeader from '@/shared/components/AppHeader.vue';

const authStore = useAuthStore()
</script>

<template>
  <v-app>
    <AppHeader v-if="authStore.isAuthenticated" />
    <v-main class="bg-grey-lighten-3">
      <router-view />
    </v-main>
  </v-app>
</template>

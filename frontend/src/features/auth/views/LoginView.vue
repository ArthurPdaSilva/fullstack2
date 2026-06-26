<script setup lang="ts">
import LoginForm from '../components/LoginForm.vue'
</script>

<template>
  <v-container class="fill-height pa-0" fluid>
    <v-row class="fill-height ma-0">
      <v-col cols="12" md="6" class="d-flex align-center justify-center pa-6">
        <LoginForm />
      </v-col>
      <v-col
        cols="12"
        md="6"
        class="d-none d-md-flex align-center justify-center flex-column"
        style="background: linear-gradient(135deg, #1565c0 0%, #0d47a1 50%, #1a237e 100%)"
      >
        <v-icon size="80" color="white">mdi-checkbox-marked-circle-plus-outline</v-icon>
        <h1 class="text-h3 text-white font-weight-bold mt-4">JTech Tasklist</h1>
        <p class="text-h6 text-white mt-2">
          Organize suas tarefas de forma inteligente
        </p>
      </v-col>
    </v-row>
  </v-container>
</template>

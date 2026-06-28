<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import { translateApiError } from '@/services/errorHandler'

const authStore = useAuthStore()
const router = useRouter()

const isRegister = ref(false)
const name = ref('')
const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const valid = ref(false)
const formRef = ref()

const emailRules = [(v: string) => !!v || 'Email é obrigatório']
const passwordRules = [(v: string) => !!v || 'Senha é obrigatória']
const nameRules = [(v: string) => !!v || 'Nome é obrigatório']

async function handleSubmit() {
  error.value = ''
  const { valid: isValid } = await formRef.value.validate()
  if (!isValid) return

  loading.value = true
  try {
    if (isRegister.value) {
      await authStore.register({ name: name.value, email: email.value, password: password.value })
    } else {
      await authStore.login({ email: email.value, password: password.value })
    }
    router.push({ name: 'dashboard' })
  } catch (e: unknown) {
    const result = translateApiError(e)
    error.value = Array.isArray(result) ? result[0] : result
  } finally {
    loading.value = false
  }
}

function toggleMode() {
  isRegister.value = !isRegister.value
  name.value = ''
  email.value = ''
  password.value = ''
  error.value = ''
  formRef.value?.reset()
}
</script>

<template>
  <v-card class="pa-8" max-width="420" width="100%" rounded="xl">
    <div class="text-center mb-6">
      <v-icon size="48" color="primary" class="mb-2"
        >mdi-checkbox-marked-circle-plus-outline</v-icon
      >
      <h2 class="text-h5 font-weight-bold">{{ isRegister ? 'Criar Conta' : 'Bem-vindo' }}</h2>
      <p class="text-body-2 text-medium-emphasis mt-1">
        {{ isRegister ? 'Preencha os dados para se cadastrar' : 'Faça login para continuar' }}
      </p>
    </div>

    <v-form ref="formRef" v-model="valid" validate-on="submit" @submit.prevent="handleSubmit">
      <v-text-field
        v-if="isRegister"
        v-model="name"
        label="Nome"
        :rules="nameRules"
        prepend-inner-icon="mdi-account-outline"
        required
      />
      <v-text-field
        v-model="email"
        label="Email"
        :rules="emailRules"
        prepend-inner-icon="mdi-email-outline"
        type="email"
        required
      />
      <v-text-field
        v-model="password"
        label="Senha"
        :rules="passwordRules"
        prepend-inner-icon="mdi-lock-outline"
        type="password"
        required
      />

      <v-alert v-if="error" type="error" class="my-4" closable variant="tonal">
        {{ error }}
      </v-alert>

      <v-btn type="submit" color="primary" block size="large" :loading="loading" class="mb-3 mt-2">
        {{ isRegister ? 'Cadastrar' : 'Entrar' }}
      </v-btn>
    </v-form>

    <v-divider class="mb-4" />
    <v-btn variant="text" color="primary" block @click="toggleMode">
      {{ isRegister ? 'Já tem conta? Faça login' : 'Não tem conta? Cadastre-se' }}
    </v-btn>
  </v-card>
</template>

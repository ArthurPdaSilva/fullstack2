# FullStack2 - JTech Challenge - Frontend

## 1. Visão Geral da Arquitetura

## 2. Stack Tecnológica

## 3. Como Rodar Localmente

**Pré-requisitos:** Node.js 18+, npm (ou pnpm/yarn).

1. Acesse a pasta do frontend:

   ```bash
   cd frontend
   ```

2. Instale as dependências:

   ```bash
   npm install
   ```

3. Inicie o servidor de desenvolvimento:

   ```bash
   npm run dev
   ```

4. A aplicação estará disponível em [http://localhost:5173](http://localhost:5173)

5. (Opcional) Para build de produção:
   ```bash
   npm run build
   ```

## 4. Como Rodar os Testes

## 5. Estrutura de Pastas Detalhada

```
src/
├── main.ts                          # Entry point da aplicação
├── App.vue                          # Componente raiz
│
├── features/                        # Módulos por domínio (feature-based)
│   ├── auth/                        # Autenticação
│   │   ├── __tests__/
│   │   │   └── authStore.spec.ts
│   │   ├── components/
│   │   │   └── LoginForm.vue
│   │   ├── stores/
│   │   │   └── authStore.ts
│   │   ├── views/
│   │   │   └── LoginView.vue
│   │   └── types.ts
│   │
│   ├── tasklists/                   # Gerenciamento de listas
│   │   ├── __tests__/
│   │   │   └── listStore.spec.ts
│   │   ├── components/
│   │   │   ├── DeleteConfirmDialog.vue
│   │   │   ├── ListCard.vue
│   │   │   └── ListFormDialog.vue
│   │   ├── stores/
│   │   │   └── listStore.ts
│   │   ├── views/
│   │   │   └── DashboardView.vue
│   │   └── types.ts
│   │
│   └── tasks/                       # Gerenciamento de tarefas
│       ├── __tests__/
│       │   └── taskStore.spec.ts
│       ├── components/
│       │   ├── EmptyState.vue
│       │   ├── TaskFormDialog.vue
│       │   └── TaskItem.vue
│       ├── stores/
│       │   └── taskStore.ts
│       ├── views/
│       │   └── TasksView.vue
│       └── types.ts
│
├── plugins/                         # Configuração de plugins (Vuetify)
│   └── vuetify.ts
│
├── router/                          # Configuração de rotas
│   └── index.ts
│
├── services/                        # Camada de serviços (API, error handling)
│   ├── api.ts
│   └── errorHandler.ts
│
└── shared/                          # Código compartilhado entre features
    └── components/
        └── AppHeader.vue
```

Cada feature segue uma estrutura padronizada:

| Pasta         | Finalidade                                |
| ------------- | ----------------------------------------- |
| `__tests__/`  | Testes unitários do módulo (Vitest)       |
| `components/` | Componentes Vue específicos da feature    |
| `stores/`     | Stores Pinia (estado e lógica de negócio) |
| `views/`      | Páginas/visões da feature                 |
| `types.ts`    | Tipos e interfaces TypeScript do módulo   |

## 6. Decisões Técnicas Aprofundadas

## 7. Melhorias e Roadmap

### Cache de Requisições

Implementar uma camada de cache para requisições HTTP usando bibliotecas como **TanStack Query**. Isso traria:

- Deduplicação automática de requisições concorrentes
- Cache em memória com stale-while-revalidate
- Refetch automático em background
- Estado de loading/error padronizado

### Testes E2E com Playwright

Adicionar testes end-to-end com **Playwright** para cobrir fluxos críticos:

- Autenticação (login/logout)
- CRUD completo de listas e tarefas
- Persistência entre recarregamentos de página
- Responsividade nos diferentes breakpoints

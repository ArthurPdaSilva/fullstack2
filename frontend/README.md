# FullStack2 - JTech Challenge - Frontend

## 1. Visão Geral da Arquitetura

Organizei o frontend em uma arquitetura **feature-based** modular. Cada domínio da aplicação (`auth`, `tasklists`, `tasks`) é isolado em seu próprio módulo com componentes, store, views, testes unitários e tipos, o que facilita a manutenção e escalabilidade do código.

**Princípios arquiteturais:**

- Separação por domínio (feature-first)
- Persistência de estado com `pinia-plugin-persistedstate` (localStorage) para sessão e listas
- Comunicação com backend real via Axios com interceptors JWT
- Rotas protegidas por Navigation Guards

## 2. Stack Tecnológica

| Tecnologia              | Finalidade                                     |
| ----------------------- | ---------------------------------------------- |
| Vue 3                   | Framework frontend (Composition API)           |
| TypeScript              | Superset tipado para JavaScript                |
| Vite                    | Build tool e dev server                        |
| Pinia                   | Gerenciamento de estado                        |
| Vue Router              | Roteamento SPA                                 |
| Vuetify 3               | Biblioteca de componentes UI (Material Design) |
| Axios                   | Cliente HTTP para requisições à API            |
| Vitest                  | Testes unitários                               |
| Vue Test Utils          | Utilitários para testes de componentes         |
| Vue-TSC                 | Type-checking para Vue SFCs                    |
| ESLint + Prettier       | Linter e formatador de código                  |
| jsdom                   | Simulação de DOM para testes                   |
| @mdi/font               | Ícones Material Design                         |
| Docker / Docker Compose | Containerização e ambiente local               |

## 3. Como Rodar

### 3.1 Como Rodar Localmente

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

### 3.2 Como Rodar com Docker

**Pré-requisitos:** Docker e Docker Compose.

1. Na raiz do projeto, execute o ambiente completo:

   ```bash
   docker compose up --build
   ```

2. A aplicação estará disponível em [http://localhost:5173](http://localhost:5173)

3. A API estará disponível em [http://localhost:8000](http://localhost:8000)

4. pgAdmin: [http://localhost:5050](http://localhost:5050) (email: `admin@jtech.com.br` / senha: `admin`)

## 4. Como Rodar os Testes

```bash
npm run test:unit
```

Os testes usam **Vitest** com **jsdom** para simulação de DOM e **Vue Test Utils** para renderização de componentes.

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

### Uso de IA no Desenvolvimento

Usei inteligência artificial como apoio para o design visual e para reforçar a sintaxe do Vue 3 com Composition API, já que eu não tinha familiaridade com o ecossistema. A IA me ajudou a acelerar a prototipagem do layout com Vuetify e a escrever código seguindo boas práticas. Todo o código gerado foi revisado e adaptado por mim para garantir que atendesse aos requisitos do projeto.

### Uso do Docker

Optei por containerizar também o frontend com Docker, seguindo a mesma abordagem do backend. O Dockerfile utiliza um build multi-stage: a primeira etapa compila a aplicação Vue com Vite em um container Node.js, e a segunda etapa serve os arquivos estáticos com Nginx, resultando em uma imagem leve e otimizada para produção. O docker-compose na raiz do projeto orquestra todos os serviços (PostgreSQL, API Spring Boot, pgAdmin e frontend), permitindo subir o ambiente completo com um único comando. Essa estratégia garante consistência entre ambientes de desenvolvimento e produção, além de facilitar a integração com pipelines de CI/CD.

### Implementação da Autenticação

Apesar de o sistema de login ser um requisito mockado, optei por implementá-lo de forma completa no frontend (com exceção do refresh token). Isso faz mais sentido para testar as rotas de login e register exigidas como requisitos no backend, garantindo que a integração entre o usuário da sessão e as tasks ocorra sem erros de segurança ou problemas de persistência.

### ESLint + Prettier vs Biome

Mantive o **ESLint** e **Prettier** por já estarem configurados no template inicial do projeto. Porém, reconheço que o **Biome** tem vantagens significativas: unifica linting e formatação em uma única ferramenta, é mais rápido por ser escrito em Rust, e tem configuração mais simples (arquivo único, zero plugins).

### VueJS vs React

Apesar de possuir maior experiência com React, optei por desenvolver o projeto em Vue.js por uma decisão estratégica. Os projetos caso eu avance utilizarão Vue.js como principal tecnologia no front-end, tornando essa uma oportunidade para aprofundar meus conhecimentos no ecossistema que pretendo utilizar profissionalmente. Embora minha experiência prática com Vue fosse menor, eu já possuía uma base acadêmica sobre o framework, o que facilitou o aprendizado. Além disso, utilizei inteligência artificial como ferramenta de apoio para esclarecer dúvidas pontuais sobre a sintaxe do Vue 3 com Composition API (bem parecido com os states do React) e acelerar a adaptação às melhores práticas da tecnologia. Todo o código gerado foi analisado, revisado e adaptado por mim para atender aos requisitos do projeto.

## 7. Melhorias e Roadmap

### Curto prazo

- [ ] Implementar refresh token automático (backend gera, frontend não consome)
- [ ] Adicionar feedback visual (toast/snackbar) para operações CRUD
- [ ] Adicionar loading skeleton nas telas
- [ ] Dividir a tela de Login com a de Cadastro

### Médio prazo

- [ ] Adicionar drag-and-drop para reordenar tarefas
- [ ] Implementar busca/filtro de tarefas por texto e status
- [ ] Adicionar testes de integração com Vue Test Utils para componentes
- [ ] Migrar ESLint + Prettier para Biome

### Longo prazo

- [ ] Cache de requisições com TanStack Query
- [ ] Modo escuro (dark theme) nativo do Vuetify
- [ ] Suporte a multi-idiomas (i18n)
- [ ] Testes E2E com Playwright/Cypress
- [ ] CI/CD com GitHub Actions (lint → test → build → deploy)
- [ ] Titles dinâmicos para cada página

# FullStack2 — JTech Challenge

Monorepo do desafio técnico Fullstack2 da JTech. Uma aplicação TODO List multi-usuário com backend em Java/Spring Boot e frontend em Vue 3/TypeScript, tudo containerizado com Docker.

---

## 1. Visão Geral da Arquitetura

Organizei o projeto em duas frentes bem separadas — backend e frontend — cada uma com sua própria arquitetura, mas conversando via API REST com JWT.

### Backend (Java + Spring Boot)

Eu optei por não dar continuidade à arquitetura hexagonal proposta no template do projeto. Pensei nisso por dois motivos: os requisitos pediam explicitamente **controllers**, **repositories** e **services**, e eu tenho mais familiaridade com essa arquitetura em camadas. Organizei a API por domínio (`auth`, `task`, `tasklist`), cada um com suas classes de controller, service, DTO, domain e repository.

**Princípios arquiteturais:**

- Arquitetura em camadas por domínio (controller → service → repository)
- Injeção de dependência via Spring IoC com interfaces e implementações separadas (`*Service` / `*ServiceImpl`, `TokenProvider` / `JwtTokenProvider`)
- Tratamento global de exceções com `@RestControllerAdvice` e exceções customizadas (`ResourceNotFoundException`, `BadRequestException`, `UnauthorizedException`, `AccessDeniedException`)
- Documentação automática com SpringDoc OpenAPI e Swagger
- Containerização via Docker Compose

### Frontend (Vue 3 + TypeScript)

Organizei o frontend em uma arquitetura **feature-based** modular. Cada domínio da aplicação (`auth`, `tasklists`, `tasks`) é isolado em seu próprio módulo com componentes, store, views, testes e tipos — o que facilita a manutenção e escalabilidade do código.

**Princípios arquiteturais:**

- Separação por domínio (feature-first) — cada feature é auto-contida
- Gerenciamento de estado com Pinia + `pinia-plugin-persistedstate` para persistir sessão (access token e dados do usuário)
- Refresh token armazenado separadamente no `localStorage` (chave `auth_refresh`) para evitar conflitos de tipo com a versão do plugin
- Comunicação com backend via Axios com interceptors JWT e renovação automática de token com fila de requisições
- Rotas protegidas por Navigation Guards do Vue Router
- Testes unitários com Vitest + Vue Test Utils + jsdom
- Alias `@` configurado no Vite mapeando para `src/` para imports absolutos

**Rotas:**

| Path | Componente | Proteção |
|------|-----------|----------|
| `/login` | `LoginView` | Redireciona para `/` se autenticado |
| `/` | `DashboardView` | Requer autenticação |
| `/tasks/:listId` | `TasksView` | Requer autenticação |

---

## 2. Stack Tecnológica

| Categoria | Tecnologia | Versão | Finalidade |
|-----------|-----------|--------|------------|
| **Linguagem (backend)** | Java | 17+ | Linguagem principal da API |
| **Framework (backend)** | Spring Boot | 3.3.5 | Framework de aplicação |
| | Spring Web | — | REST controllers |
| | Spring Data JPA / Hibernate | — | ORM e persistência |
| | Spring Security | — | Autenticação e autorização |
| | Spring Validation | — | Validação de requisições |
| | Spring Boot Actuator | — | Monitoramento e health check |
| | Spring Boot DevTools | — | Auto-restart e live-reload em desenvolvimento |
| **Banco de Dados** | PostgreSQL | 16 | Banco relacional (produção) |
| | H2 Database | — | Banco em memória (testes) |
| **Segurança** | JJWT (io.jsonwebtoken) | 0.12.6 | Geração e validação de tokens JWT |
| | BCrypt | — | Hash seguro de senhas |
| **Boilerplate** | Lombok | 1.18.36 | Redução de código repetitivo |
| | spring-dotenv | 4.0.0 | Carregamento automático de `.env` |
| **Documentação API** | SpringDoc OpenAPI | 2.6.0 | Swagger UI automático |
| **Testes (backend)** | JUnit 5 | — | Framework de testes unitários |
| | Mockito | — | Mocks e isolamento de dependências |
| | MockMvc | — | Testes de integração REST |
| | Spring Security Test | — | Suporte a autenticação em testes |
| **Linguagem (frontend)** | TypeScript | ~5.8 | Superset tipado para JavaScript |
| **Framework (frontend)** | Vue 3 | 3.5 | Framework frontend (Composition API) |
| **Build / Dev Server** | Vite | 7.0 | Build tool e dev server |
| | vite-plugin-vue-devtools | 8.0 | Vue DevTools no navegador em desenvolvimento |
| **Roteamento** | Vue Router | 4.5 | Roteamento SPA |
| **Estado** | Pinia | 3.0 | Gerenciamento de estado |
| | pinia-plugin-persistedstate | 4.2 | Persistência automática em localStorage |
| **UI** | Vuetify 3 | 3.7 | Biblioteca de componentes Material Design |
| | @mdi/font | 7.4 | Ícones Material Design |
| **HTTP Client** | Axios | 1.7 | Requisições à API com interceptors |
| **Testes (frontend)** | Vitest | 3.2 | Framework de testes unitários |
| | Vue Test Utils | 2.4 | Renderização de componentes em testes |
| | jsdom | 26.1 | Simulação de DOM para testes |
| **Lint / Format** | ESLint | 9.31 | Linter (config flat) |
| | Prettier | 3.6 | Formatador de código |
| | vue-tsc | 3.0 | Type-checking para Vue SFCs |
| **Containerização** | Docker / Docker Compose | — | Ambiente completo em containers |
| | Nginx | — | Servidor web para o frontend em produção |

---

## 3. Como Rodar Localmente

### Opção 1: Docker Compose (recomendado)

**Pré-requisitos:** Docker e Docker Compose instalados.

1. Clone o repositório e acesse a pasta raiz:

   ```bash
   git clone <url-do-repo>
   cd fullstack2
   ```

2. Copie os arquivos de ambiente de exemplo:

   ```bash
   # Linux / macOS
   cp backend/.env.example backend/.env && cp frontend/.env.example frontend/.env

   # Windows (Command Prompt)
   copy backend\.env.example backend\.env && copy frontend\.env.example frontend\.env

   # Windows (PowerShell)
   Copy-Item backend\.env.example backend\.env; Copy-Item frontend\.env.example frontend\.env
   ```

   Os valores padrão já funcionam para o Docker Compose.

3. Suba todos os serviços:

   ```bash
   docker compose up --build
   ```

4. Acesse:

   | Serviço | URL |
   |---------|-----|
   | API (Spring Boot) | [http://localhost:8000](http://localhost:8000) |
   | Swagger UI | [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html) |
   | Frontend (Vue 3) | [http://localhost:5173](http://localhost:5173) |
    | pgAdmin | [http://localhost:5050](http://localhost:5050) (email: `admin@jtech.com.br` / senha: `admin`) |
    | | **Importante:** ao conectar no PostgreSQL pelo pgAdmin, use `db` como host (nome do serviço no Docker), não `localhost`. |

### Opção 2: Manual (backend + frontend separados)

#### Backend

**Pré-requisitos:** Java 17, Maven (ou o wrapper `./mvnw`), PostgreSQL 16 rodando.

1. Acesse a pasta `backend/` e copie o `.env`:

   ```bash
   cd backend
   ```
   ```bash
   # Linux / macOS
   cp .env.example .env

   # Windows (Command Prompt)
   copy .env.example .env

   # Windows (PowerShell)
   Copy-Item .env.example .env
   ```

2. Ajuste as variáveis no `.env` conforme seu ambiente:

   | Variável | Descrição | Padrão |
   |----------|-----------|--------|
   | `POSTGRES_USER` | Usuário do PostgreSQL | `tasklist` |
   | `POSTGRES_PASSWORD` | Senha do PostgreSQL | `tasklist` |
   | `POSTGRES_DB` | Nome do banco | `tasklist` |
   | `DB_HOST` | Host do PostgreSQL | `localhost` |
   | `DB_PORT` | Porta do PostgreSQL | `5432` |
    | `JWT_SECRET` | Chave secreta para JWT | (gerada no exemplo) |

   > O Spring Boot carrega automaticamente as variáveis do arquivo `.env` via `spring-dotenv`. Não é necessário exportá-las manualmente no terminal.
   >
   > O `.env.example` também contém `PGADMIN_DEFAULT_EMAIL` e `PGADMIN_DEFAULT_PASSWORD` — essas variáveis são consumidas **apenas** pelo serviço pgAdmin no Docker Compose, não pelo backend.

3. Execute a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

   A API estará disponível em [http://localhost:8000](http://localhost:8000).

#### Frontend

**Pré-requisitos:** Node.js 18+ (o `package.json` especifica `^20.19.0 || >=22.12.0`), npm.

1. Acesse a pasta `frontend/`:

   ```bash
   cd frontend
   ```

2. Copie o `.env`:

   ```bash
   # Linux / macOS
   cp .env.example .env

   # Windows (Command Prompt)
   copy .env.example .env

   # Windows (PowerShell)
   Copy-Item .env.example .env
   ```

   A única variável é `VITE_API_BASE_URL=http://localhost:8000`.

3. Instale as dependências:

   ```bash
   npm install
   ```

4. Inicie o servidor de desenvolvimento:

   ```bash
   npm run dev
   ```

   O frontend estará disponível em [http://localhost:5173](http://localhost:5173).

---

## 4. Como Rodar os Testes

### Backend

**Pré-requisito:** JDK 17 instalado e configurado como padrão (`java -version` deve mostrar `17`). Se seu sistema tiver uma versão mais nova (ex: Java 25), use:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk ./mvnw test
```

Os testes usam **JUnit 5** + **Mockito** para testes unitários e **MockMvc** para testes de integração dos endpoints REST. O banco **H2** em memória substitui automaticamente o PostgreSQL durante os testes.

```bash
./mvnw test
```

**Suites de teste (10 classes):**

| Classe | Tipo | Escopo |
|--------|------|--------|
| `AuthServiceTest` | Unitário | Serviço de autenticação (login, registro, refresh token) |
| `TaskServiceTest` | Unitário | Serviço de tarefas (CRUD, ownership) |
| `TaskListServiceTest` | Unitário | Serviço de listas (CRUD, ownership, trim) |
| `JwtTokenProviderTest` | Unitário | Geração e validação de tokens JWT |
| `GlobalExceptionHandlerTest` | Unitário | Tratamento global de exceções |
| `AuthControllerTest` | Integração | Endpoints de autenticação |
| `TaskControllerTest` | Integração | Endpoints de tarefas |
| `TaskListControllerTest` | Integração | Endpoints de listas |
| `BackendApplicationTests` | Smoke | Contexto da aplicação sobe |
| `HealthControllerTest` | Integração | Health check endpoint |

### Frontend

```bash
npm run test:unit     # Vitest (jsdom + Vue Test Utils)
npm run type-check    # vue-tsc — type-checking de todos os arquivos
npm run lint          # ESLint — linting com autofix
npm run format        # Prettier — formatação de código
```

**Scripts npm disponíveis:**

| Comando | Descrição |
|---------|-----------|
| `npm run dev` | Servidor de desenvolvimento Vite |
| `npm run build` | Build de produção |
| `npm run preview` | Preview do build de produção |
| `npm run test:unit` | Testes unitários com Vitest |
| `npm run type-check` | Type-checking com vue-tsc |
| `npm run lint` | Linting com ESLint (autofix) |
| `npm run format` | Formatação com Prettier |
| `npm run build-only` | Build de produção (apenas Vite, sem type-check) |

**Suites de teste (9 spec files):**

| Arquivo | Feature |
|---------|---------|
| `authStore.spec.ts` | Autenticação (login, registro, logout, refresh) |
| `LoginForm.spec.ts` | Componente de formulário de login/registro |
| `listStore.spec.ts` | Listas de tarefas (CRUD via API) |
| `taskStore.spec.ts` | Tarefas (CRUD, filtro por lista) |
| `ListCard.spec.ts` | Componente de card de lista |
| `TaskItem.spec.ts` | Componente de item de tarefa |
| `EmptyState.spec.ts` | Componente de estado vazio |
| `api.spec.ts` | Interceptador Axios (refresh token, falha) |
| `errorHandler.spec.ts` | Tratamento de erros da API |

---

## 5. Estrutura de Pastas Detalhada

```
fullstack2/
├── .github/
│   └── workflows/
│       └── ci.yml                      # GitHub Actions: lint + test + build (push/PR na main)
├── .gitignore                          # Ignora .env na raiz
├── .vscode/
│   └── settings.json
├── docker-compose.yml                  # Orquestração: db, api, pgadmin, frontend
├── EXPLANATION.md                      # Enunciado original do desafio
├── README.md                           # Este arquivo
│
├── backend/                            # Aplicação Java + Spring Boot
│   ├── .env / .env.example
│   ├── .gitattributes / .gitignore
│   ├── Dockerfile                      # Build multi-stage (compilação → JRE)
│   ├── mvnw / mvnw.cmd                 # Maven Wrapper
│   ├── pom.xml
│   │
│   └── src/
│       ├── main/
│       │   ├── java/com/jtech/tasklist/backend/
│       │   │   ├── BackendApplication.java           # Entry point
│       │   │   │
│       │   │   ├── auth/                              # Módulo de autenticação
│       │   │   │   ├── controller/
│       │   │   │   │   └── AuthController.java        # POST /auth/register, /login, /refresh
│       │   │   │   ├── domain/
│       │   │   │   │   └── User.java                  # Entidade JPA (id, name, email, password)
│       │   │   │   ├── dto/
│       │   │   │   │   ├── AuthResponse.java          # token, refreshToken, user
│       │   │   │   │   ├── LoginRequest.java          # email, password
│       │   │   │   │   ├── RefreshRequest.java        # refreshToken
│       │   │   │   │   └── RegisterRequest.java       # name, email, password
│       │   │   │   ├── repository/
│       │   │   │   │   └── UserRepository.java        # JPARepository + existsByEmail
│       │   │   │   └── service/
│       │   │   │       ├── AuthService.java           # Interface
│       │   │   │       └── AuthServiceImpl.java       # Implementação (register, login, refresh)
│       │   │   │
│       │   │   ├── task/                              # Módulo de tarefas
│       │   │   │   ├── controller/
│       │   │   │   │   └── TaskController.java        # CRUD /tasks
│       │   │   │   ├── domain/
│       │   │   │   │   └── Task.java                  # Entidade JPA (user FK, task_list FK c/ cascade)
│       │   │   │   ├── dto/
│       │   │   │   │   ├── TaskRequest.java           # title, description, completed, taskListId?
│       │   │   │   │   └── TaskResponse.java          # + taskListId
│       │   │   │   ├── repository/
│       │   │   │   │   └── TaskRepository.java        # + findByUserId, countByTaskListId
│       │   │   │   └── service/
│       │   │   │       ├── TaskService.java           # Interface
│       │   │   │       └── TaskServiceImpl.java       # Implementação (CRUD + ownership)
│       │   │   │
│       │   │   ├── tasklist/                          # Módulo de listas de tarefas
│       │   │   │   ├── controller/
│       │   │   │   │   └── TaskListController.java    # CRUD /tasklists
│       │   │   │   ├── domain/
│       │   │   │   │   └── TaskList.java              # Entidade JPA (id UUID, name, user FK, createdAt)
│       │   │   │   ├── dto/
│       │   │   │   │   ├── TaskListRequest.java       # name (com validação @NotBlank)
│       │   │   │   │   └── TaskListResponse.java      # id, name, createdAt, taskCount
│       │   │   │   ├── repository/
│       │   │   │   │   └── TaskListRepository.java    # + findByUserIdOrderByCreatedAt
│       │   │   │   └── service/
│       │   │   │       ├── TaskListService.java       # Interface
│       │   │   │       └── TaskListServiceImpl.java   # Implementação (CRUD + ownership)
│       │   │   │
│       │   │   ├── config/
│       │   │   │   ├── OpenApiConfig.java             # Configuração Swagger (bearer token)
│       │   │   │   ├── SecurityConfig.java            # Spring Security (permits /auth/**, JWTAuthFilter)
│       │   │   │   └── WebConfig.java                 # CORS (permissivo em desenvolvimento)
│       │   │   │
│       │   │   ├── health/
│       │   │   │   └── HealthController.java           # GET /health
│       │   │   │
│       │   │   ├── exception/
│       │   │   │   ├── AccessDeniedException.java     # 403 — acesso negado a recurso de outro usuário
│       │   │   │   ├── ApiError.java                  # Modelo de erro padronizado
│       │   │   │   ├── BadRequestException.java       # 400 — validação de negócio
│       │   │   │   ├── GlobalExceptionHandler.java    # @RestControllerAdvice centralizado
│       │   │   │   ├── ResourceNotFoundException.java # 404 — recurso não encontrado
│       │   │   │   └── UnauthorizedException.java     # 401 — não autenticado
│       │   │   │
│       │   │   └── jwt/
│       │   │       ├── JwtAuthenticationFilter.java   # Filtro OncePerRequest (extrai token do header)
│       │   │       ├── JwtTokenProvider.java          # Implementação concreta do TokenProvider
│       │   │       └── TokenProvider.java             # Interface (generate, validate, extract userId)
│       │   │
│       │   └── resources/
│       │       └── application.properties             # Config (DB, JPA, JWT, Swagger, CORS)
│       │
│       └── test/
│           ├── java/com/jtech/tasklist/backend/
│           │   ├── BackendApplicationTests.java
│           │   ├── auth/
│           │   │   └── AuthServiceTest.java
│           │   ├── exception/
│           │   │   └── GlobalExceptionHandlerTest.java
│           │   ├── health/
│           │   │   └── HealthControllerTest.java
│           │   ├── integration/
│           │   │   ├── AuthControllerTest.java
│           │   │   ├── TaskControllerTest.java
│           │   │   └── TaskListControllerTest.java
│           │   ├── jwt/
│           │   │   └── JwtTokenProviderTest.java
│           │   ├── task/
│           │   │   └── TaskServiceTest.java
│           │   └── tasklist/
│           │       └── TaskListServiceTest.java
│           └── resources/
│               └── application.properties             # H2 em modo memória para testes
│
└── frontend/                           # Aplicação Vue 3 + Vite
    ├── .env / .env.example
    ├── .editorconfig / .gitattributes / .gitignore / .prettierrc.json
    ├── .vscode/
    │   └── extensions.json
    ├── Dockerfile                      # Build multi-stage (Node → Nginx)
    ├── nginx.conf                      # Servidor Nginx para produção
    ├── index.html                      # Entry point HTML
    ├── env.d.ts                        # Declarações de tipos Vite
    ├── eslint.config.ts                # ESLint flat config
    ├── package.json / package-lock.json
    ├── tsconfig.json / tsconfig.app.json / tsconfig.node.json / tsconfig.vitest.json
    ├── vite.config.ts                  # Vite + Vue plugin + alias @
    ├── vitest.config.ts                # Vitest (jsdom, vuetify inline, exclusão e2e)
    │
    └── src/
        ├── main.ts                     # Entry point (createApp + plugins)
        ├── App.vue                     # Componente raiz
        │
        ├── features/                   # Módulos por domínio (feature-based)
        │   ├── auth/                   # Autenticação
        │   │   ├── __tests__/
        │   │   │   ├── authStore.spec.ts
        │   │   │   └── LoginForm.spec.ts
        │   │   ├── components/
        │   │   │   └── LoginForm.vue
        │   │   ├── stores/
        │   │   │   └── authStore.ts    # user, token, refreshToken, login, register, refreshTokenAction
        │   │   ├── views/
        │   │   │   └── LoginView.vue
        │   │   └── types.ts            # User, AuthResponse, LoginCredentials
        │   │
        │   ├── tasklists/              # Gerenciamento de listas
        │   │   ├── __tests__/
        │   │   │   ├── ListCard.spec.ts
        │   │   │   └── listStore.spec.ts
        │   │   ├── components/
        │   │   │   ├── DeleteConfirmDialog.vue
        │   │   │   ├── ListCard.vue
        │   │   │   └── ListFormDialog.vue
        │   │   ├── stores/
        │   │   │   └── listStore.ts    # CRUD via API (/tasklists)
        │   │   ├── views/
        │   │   │   └── DashboardView.vue
        │   │   └── types.ts            # TaskList, TaskListRequest, TaskListResponse
        │   │
        │   └── tasks/                  # Gerenciamento de tarefas
        │       ├── __tests__/
        │       │   ├── EmptyState.spec.ts
        │       │   ├── TaskItem.spec.ts
        │       │   └── taskStore.spec.ts
        │       ├── components/
        │       │   ├── EmptyState.vue
        │       │   ├── TaskFormDialog.vue
        │       │   └── TaskItem.vue
        │       ├── stores/
        │       │   └── taskStore.ts    # CRUD via API (/tasks)
        │       ├── views/
        │       │   └── TasksView.vue
        │       └── types.ts            # Task, TaskRequest, TaskResponse
        │
        ├── plugins/
        │   └── vuetify.ts              # Configuração Vuetify 3
        │
        ├── router/
        │   └── index.ts                # Rotas (/, /tasks/:id) + navigation guard
        │
        ├── services/
        │   ├── __tests__/
        │   │   ├── api.spec.ts         # Testes do interceptor de refresh
        │   │   └── errorHandler.spec.ts
        │   ├── api.ts                  # Axios instance + interceptors (JWT, refresh c/ fila)
        │   └── errorHandler.ts         # Tratamento centralizado de erros
        │
        └── shared/
            └── components/
                └── AppHeader.vue       # Componente compartilhado (header da aplicação)
```

---

## 5.1. Schema do Banco de Dados

O esquema é gerenciado pelo Hibernate (`ddl-auto=update`), sem migrations manuais. As principais tabelas:

### `users`

| Coluna | Tipo | Restrições |
|--------|------|------------|
| `id` | UUID | PK, gerado automaticamente |
| `name` | VARCHAR(255) | NOT NULL |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE |
| `password` | VARCHAR(255) | NOT NULL (hash BCrypt) |

### `task_lists`

| Coluna | Tipo | Restrições |
|--------|------|------------|
| `id` | UUID | PK, gerado automaticamente |
| `name` | VARCHAR(255) | NOT NULL |
| `user_id` | UUID | FK → `users(id)`, NOT NULL |
| `created_at` | TIMESTAMP | NOT NULL |

### `tasks`

| Coluna | Tipo | Restrições |
|--------|------|------------|
| `id` | UUID | PK, gerado automaticamente |
| `title` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | NULLABLE |
| `completed` | BOOLEAN | DEFAULT FALSE |
| `user_id` | UUID | FK → `users(id)`, NOT NULL |
| `task_list_id` | UUID | FK → `task_lists(id)`, ON DELETE CASCADE, NULLABLE |

**Relacionamentos:**

- `users` 1:N `task_lists` — um usuário pode ter várias listas
- `users` 1:N `tasks` — um usuário pode ter várias tarefas
- `task_lists` 1:N `tasks` — uma lista pode ter várias tarefas; ao excluir a lista, as tarefas são removidas em cascata

---

## 6. Decisões Técnicas Aprofundadas

### Uso de IA no Desenvolvimento

Usei inteligência artificial para otimizar o desenvolvimento das camadas de **repository**, **service**, **controller** e **DTO**, tanto no backend quanto no frontend. No backend, a IA acelerou a escrita de código boilerplate e a estruturação dos módulos. No frontend, usei IA como apoio para o design visual com Vuetify e para reforçar a sintaxe do Vue 3 com Composition API, já que eu não tinha tanta familiaridade com o ecossistema. Todo o código gerado foi revisado e adaptado por mim para garantir que atendesse aos requisitos do projeto.

### Uso do Docker

Optei por containerizar todos os serviços com Docker. O `docker-compose.yml` na raiz orquestra quatro serviços: PostgreSQL 16, a API Spring Boot, pgAdmin e o frontend Vue.js. O frontend utiliza um build multi-stage (Node.js para compilação → Nginx para servir os arquivos estáticos), resultando em uma imagem leve e otimizada para produção. Essa abordagem garante um ambiente de desenvolvimento consistente entre diferentes máquinas, eliminando a necessidade de instalar e configurar manualmente cada ferramenta.

### Leitura Automática do .env (spring-dotenv)

Adicionei a dependência `me.paulschwarz:spring-dotenv` para que o Spring Boot carregue automaticamente as variáveis do arquivo `.env` na raiz do projeto ao executar localmente com `./mvnw spring-boot:run`. Sem essa biblioteca, os placeholders `${JWT_SECRET}`, `${DB_HOST}`, etc. não seriam resolvidos fora do Docker Compose (que já carrega o `.env` via `env_file:`). A biblioteca lê o arquivo `.env` durante a inicialização da aplicação e expõe as variáveis para o `application.properties`, sem necessidade de export manual ou scripts adicionais.

### Injeção de Dependência (Backend)

Pensando no crescimento do sistema, vejo a injeção de dependência como um passo fundamental. Separei contratos de implementação com interfaces (`AuthService`, `TaskService`, `TaskListService`, `TokenProvider`) e suas implementações concretas (`*Impl`). Isso me dá flexibilidade para trocar implementações sem impacto nas camadas superiores, facilita os testes com mocks e mantém o encapsulamento.

### Implementação da Autenticação

Apesar de o sistema de login ser originalmente um requisito mockado no frontend, implementei ele de forma completa — com comunicação real com o backend via `/auth/login` e `/auth/register`. Isso garante que a integração entre o usuário da sessão e as tasks ocorra sem erros de segurança ou problemas de persistência. O backend usa BCrypt para hash de senhas e gera tokens de acesso (15 min) e refresh (7 dias) via JJWT.

### Documentação da API com Swagger / OpenAPI

Documentei todos os endpoints da API com `@Operation` (summary + description) e `@ApiResponse` para cada código de resposta possível, seguindo um padrão consistente:

- **200 OK** — Operação bem-sucedida com corpo de resposta
- **201 Created** — Recurso criado (POST /tasks, POST /tasklists)
- **204 No Content** — Recurso excluído (DELETE)
- **400 Bad Request** — Erro de validação (@Valid), e-mail duplicado, UUID inválido ou refresh token em branco
- **401 Unauthorized** — Credenciais inválidas ou token expirado/inválido (JWT ou refresh)
- **403 Forbidden** — Tentativa de acessar recurso de outro usuário
- **404 Not Found** — Recurso não encontrado
- **500 Internal Server Error** — Erro inesperado (genérico, não documentado por endpoint)

Também criei o endpoint `GET /health` (público, sem autenticação) que retorna `{"status":"UP"}` — útil para health checks de load balancers e orquestradores.

Usei `@Content(schema = @Schema(implementation = ApiError.class))` em todas as respostas de erro para que o Swagger exiba o modelo padronizado com `status`, `timestamp`, `message` e opcionalmente `errors`. O `401` para JWT ausente/inválido não precisa ser documentado por endpoint porque o `SecurityScheme` bearer já está configurado globalmente no `OpenApiConfig` e o Swagger já adiciona o cadeado automaticamente.

### Refresh Token Automático

Implementei o refresh token de forma transparente no frontend:

1. **Armazenamento separado**: O refresh token é guardado no `localStorage` sob a chave `auth_refresh`, enquanto o access token e os dados do usuário seguem persistidos via `pinia-plugin-persistedstate`. Essa separação evita conflitos de tipo que tive com o `pick` do plugin na versão atual do Pinia 3.
2. **Interceptador Axios**: Ao receber um `401 Unauthorized`, o interceptador de resposta tenta renovar o token automaticamente chamando `POST /auth/refresh` com o refresh token salvo.
3. **Fila de requisições**: Se múltiplas chamadas falharem simultaneamente com 401, apenas uma requisição de refresh é feita. As demais são enfileiradas e retentadas após a renovação — evita várias chamadas de refresh concorrentes.
4. **Fallback**: Se o refresh também falhar (token expirado ou inválido), o usuário é redirecionado ao login e os dados de autenticação são limpos.

### Persistência de Listas de Tarefas (TaskList)

Originalmente as listas de tarefas eram armazenadas apenas no `localStorage` do frontend. Para garantir persistência entre dispositivos e sessões, criei a entidade `TaskList` no backend:

- **Entidade**: `TaskList` com `id` (UUID), `name`, `user` (FK → `users`), `createdAt`.
- **Relação com Task**: Adicionei `task_list_id` (FK) na tabela `tasks` com `ON DELETE CASCADE` — ao excluir uma lista, todas as suas tarefas são removidas automaticamente no banco, sem necessidade de código manual.
- **Endpoints CRUD**: `POST/GET /tasklists`, `PUT/DELETE /tasklists/{id}` — todos protegidos por JWT com verificação de ownership.
- **TaskCount**: O endpoint `GET /tasklists` retorna a contagem de tarefas por lista via `countByTaskListId` no repositório, sem campo adicional na entidade.
- **Criação de tarefas**: O DTO `TaskRequest` aceita `taskListId` opcional. Se informado, a task é vinculada automaticamente à lista correspondente.

No frontend, o `listStore` foi refatorado para consumir esses endpoints via Axios, eliminando completamente a dependência de `localStorage` para listas.

### ESLint + Prettier vs Biome (Frontend)

Mantive o **ESLint** e **Prettier** por já estarem configurados no template inicial do projeto. Porém, reconheço que o **Biome** tem vantagens significativas: unifica linting e formatação em uma única ferramenta, é mais rápido por ser escrito em Rust, e tem configuração mais simples (arquivo único, zero plugins). Considere essa uma migração futura.

### VueJS vs React

Apesar de possuir maior experiência com React, optei por desenvolver o projeto em Vue.js por uma decisão estratégica — os projetos futuros utilizarão Vue.js como principal tecnologia no front-end. Embora minha experiência prática com Vue fosse menor, eu já possuía uma base acadêmica sobre o framework, o que facilitou o aprendizado. A Composition API do Vue 3 é bem parecida com hooks do React, o que ajudou na adaptação.

### CI/CD com GitHub Actions

Configurei um pipeline de integração contínua no GitHub Actions (`.github/workflows/ci.yml`) para validar automaticamente todo código enviado para a `main` ou em pull requests. O pipeline tem dois jobs executados em paralelo:

- **Backend**: JDK 17 (temurin) com cache Maven — roda `./mvnw test` (todos os testes unitários e de integração com H2) e depois `./mvnw package -DskipTests` para verificar o build. Nenhum banco externo é necessário porque os testes usam H2 em memória.
- **Frontend**: Node.js 20 com cache NPM — executa `npm ci`, `npm run lint`, `npm run type-check`, `npm run test:unit` e `npm run build-only` nessa ordem, garantindo que linting, tipos, testes e build estejam todos verdes.

Optei por manter apenas a **validação** sem deploy automático por enquanto — o pipeline falha se algo quebrar, mas a publicação continua sendo manual. Isso evita expor credenciais de deploy desnecessariamente e mantém o controle sobre quando e como publicar.

---

## 7. Melhorias e Roadmap

### Curto prazo

- [ ] Adicionar feedback visual (toast/snackbar) para operações CRUD
- [ ] Adicionar loading skeleton nas telas
- [ ] Dividir a tela de Login da tela de Cadastro

### Médio prazo

- [ ] Paginação no servidor para listas com muitas tarefas
- [ ] Logging estruturado para observabilidade (backend)
- [ ] Adicionar Flyway para manter o histórico de migrations do banco
- [ ] Adicionar drag-and-drop para reordenar tarefas
- [ ] Implementar busca/filtro de tarefas por texto e status
- [ ] Adicionar testes de integração com Vue Test Utils para componentes
- [ ] Migrar ESLint + Prettier para Biome

### Longo prazo

- [ ] Cache de requisições com TanStack Query
- [ ] Modo escuro (dark theme) nativo do Vuetify
- [ ] Suporte a multi-idiomas (i18n)
- [ ] Testes E2E com Playwright ou Cypress
- [ ] Títulos dinâmicos para cada página (document title)

# FullStack2 - JTech Challenge - Backend

## 1. Visão Geral da Arquitetura

* Optou-se por não dar continuidade à arquitetura hexagonal proposta no template do projeto. A decisão foi baseada em dois fatores principais: os requisitos especificam explicitamente a utilização de **controllers**, **repository** e **service**, e eu tenho maior familiariedade com essa arquitetura. Dessa forma, a API segue uma arquitetura em camadas tradicional, organizada por domínio (`auth`, `task`), cada um com suas respectivas classes de controller, service, DTO, domain e repository.

<!-- Descrever estrutura e decisões arquiteturais -->

## 2. Stack Tecnológica

| Tecnologia | Finalidade |
|------------|------------|
| Java | Linguagem principal |
| Spring Boot | Framework de aplicação |
| Spring Web | REST controllers |
| Spring Data JPA / Hibernate | ORM e persistência |
| Spring Security | Autenticação e autorização |
| Spring Validation | Validação de requisições |
| PostgreSQL | Banco de dados relacional (produção) |
| H2 Database | Banco em memória (testes) |
| JWT (jjwt) | Geração e validação de tokens |
| Lombok | Redução de código boilerplate |
| SpringDoc OpenAPI | Documentação Swagger |
| BCrypt | Hash seguro de senhas |
| JUnit 5 + Mockito | Testes unitários |
| MockMvc | Testes de integração REST |
| Docker / Docker Compose | Containerização e ambiente local |

## 3. Como Rodar 

## 3.1 Como Rodar Localmente

**Pré-requisitos:** Java 17, Maven (ou o Maven Wrapper `./mvnw`), PostgreSQL 16 rodando.

1. Clone o repositório e acesse a pasta `backend/`
2. Crie um banco PostgreSQL (padrão: `tasklist`) ou configure via variáveis de ambiente
3. Configure as variáveis de ambiente abaixo (ou edite o arquivo `.env`):

   | Variável     | Padrão       | Descrição                    |
   |--------------|--------------|------------------------------|
   | `DB_HOST`    | `localhost`  | Host do PostgreSQL           |
   | `DB_PORT`    | `5432`       | Porta do PostgreSQL          |
   | `DB_NAME`    | `tasklist`   | Nome do banco de dados       |
   | `DB_USER`    | `postgres`   | Usuário do banco             |
   | `DB_PASS`    | `postgres`   | Senha do banco               |
   | `JWT_SECRET` | *(default)*  | Chave secreta para JWT       |

4. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
5. A API estará disponível em [http://localhost:8000](http://localhost:8000)
6. Swagger UI: [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html)

## 3.2 Como Rodar com Docker

**Pré-requisitos:** Docker e Docker Compose.

1. Clone o repositório e acesse a pasta `backend/`
2. Configure o arquivo `.env` com as seguintes variáveis:

   | Variável                   | Exemplo                                                      | Descrição                  |
   |----------------------------|--------------------------------------------------------------|----------------------------|
   | `POSTGRES_USER`            | `tasklist`                                                   | Usuário do PostgreSQL      |
   | `POSTGRES_PASSWORD`        | `tasklist`                                                   | Senha do PostgreSQL        |
   | `POSTGRES_DB`              | `tasklist`                                                   | Nome do banco de dados     |
   | `JWT_SECRET`               | `ZGV2LXNlY3JldC1rZXkt...`                                   | Chave secreta para JWT     |
   | `PGADMIN_DEFAULT_EMAIL`    | `admin@jtech.com.br`                                         | Email do pgAdmin           |
   | `PGADMIN_DEFAULT_PASSWORD` | `admin`                                                      | Senha do pgAdmin           |

3. Execute o ambiente:
   ```bash
   docker compose up --build
   ```
4. A API estará disponível em [http://localhost:8000](http://localhost:8000)
5. Swagger UI: [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html)
6. pgAdmin: [http://localhost:5050](http://localhost:5050) (email: `admin@jtech.com.br` / senha: `admin`)

## 4. Como Rodar os Testes

<!-- Comandos para executar suite completa de testes -->

## 5. Estrutura de Pastas Detalhada

```
src/
├── main/
│   ├── java/com/jtech/tasklist/backend/
│   │   ├── BackendApplication.java          # Entry point da aplicação
│   │   │
│   │   ├── auth/                            # Módulo de autenticação
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java
│   │   │   ├── domain/
│   │   │   │   └── User.java
│   │   │   ├── dto/
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── RegisterRequest.java
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       └── AuthService.java
│   │   │
│   │   ├── task/                            # Módulo de tarefas
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java
│   │   │   ├── domain/
│   │   │   │   └── Task.java
│   │   │   ├── dto/
│   │   │   │   ├── TaskRequest.java
│   │   │   │   └── TaskResponse.java
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java
│   │   │   └── service/
│   │   │       └── TaskService.java
│   │   │
│   │   ├── config/                          # Configurações globais
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   │
│   │   ├── exception/                       # Tratamento de erros
│   │   │   ├── ApiError.java
│   │   │   ├── BadRequestException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── UnauthorizedException.java
│   │   │
│   │   └── jwt/                             # Autenticação JWT
│   │       ├── JwtAuthenticationFilter.java
│   │       └── JwtTokenProvider.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    ├── java/com/jtech/tasklist/backend/
    │   ├── BackendApplicationTests.java
    │   ├── auth/
    │   │   └── AuthServiceTest.java
    │   ├── integration/
    │   │   ├── AuthControllerTest.java
    │   │   └── TaskControllerTest.java
    │   └── task/
    │       └── TaskServiceTest.java
    └── resources/
        └── application.properties
```

Cada módulo de domínio segue uma arquitetura em camadas:

| Camada       | Finalidade                             |
|--------------|----------------------------------------|
| `controller` | Endpoints REST da feature              |
| `domain`     | Entidade JPA (modelo do banco)         |
| `dto`        | Objetos de requisição e resposta       |
| `repository` | Interface Spring Data JPA              |
| `service`    | Lógica de negócio do módulo            |

## 6. Decisões Técnicas Aprofundadas

<!-- Justificativas sobre escolhas arquiteturais, padrões e bibliotecas -->

## 7. Melhorias e Roadmap

* ### Logging para Observabilidade
* ### Pipeline de CI/CD com GitHub Actions
<!-- Propostas técnicas para evolução e escalabilidade -->

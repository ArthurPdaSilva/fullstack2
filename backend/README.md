# FullStack2 - JTech Challenge - Backend

## 1. Visão Geral da Arquitetura

Eu optei por não dar continuidade à arquitetura hexagonal proposta no template do projeto. Pensei nisso por dois motivos: os requisitos pediam explicitamente **controllers**, **repository** e **service**, e eu tenho mais familiaridade com essa arquitetura em camadas. Organizei a API por domínio (`auth`, `task`), cada um com suas classes de controller, service, DTO, domain e repository.

**Princípios arquiteturais:**

- Arquitetura em camadas por domínio (controller → service → repository)
- Tratamento global de exceções com `@RestControllerAdvice`
- Documentação automática com SpringDoc OpenAPI e Swagger
- Containerização via Docker Compose (frontend + app + PostgreSQL + pgAdmin)

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
2. Copie o arquivo de ambiente de exemplo e ajuste se necessário:
   ```bash
   cp .env.example .env
   ```
3. (Opcional) Crie um banco PostgreSQL ou mantenha o configurado no `.env` (`tasklist`)
   As variáveis do `.env` (já com valores padrão no `application.properties`):

   | Variável                   | Padrão       | Descrição                    |
   |----------------------------|--------------|------------------------------|
   | `POSTGRES_USER`            | `tasklist`   | Usuário do PostgreSQL        |
   | `POSTGRES_PASSWORD`        | `tasklist`   | Senha do PostgreSQL          |
   | `POSTGRES_DB`              | `tasklist`   | Nome do banco de dados       |
   | `DB_HOST`                  | `localhost`  | Host do PostgreSQL           |
   | `DB_PORT`                  | `5432`       | Porta do PostgreSQL          |
   | `JWT_SECRET`               | *(default)*  | Chave secreta para JWT       |

4. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
5. A API estará disponível em [http://localhost:8000](http://localhost:8000)
6. Swagger UI: [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html)

## 3.2 Como Rodar com Docker

**Pré-requisitos:** Docker e Docker Compose.

1. Copie o arquivo de ambiente de exemplo e ajuste se necessário:
   ```bash
   cp backend/.env.example backend/.env
   ```

   As seguintes variáveis estão disponíveis:

   | Variável                   | Exemplo                                                      | Descrição                  |
   |----------------------------|--------------------------------------------------------------|----------------------------|
   | `POSTGRES_USER`            | `tasklist`                                                   | Usuário do PostgreSQL      |
   | `POSTGRES_PASSWORD`        | `tasklist`                                                   | Senha do PostgreSQL        |
   | `POSTGRES_DB`              | `tasklist`                                                   | Nome do banco de dados     |
   | `DB_HOST`                  | `localhost`                                                  | Host do PostgreSQL         |
   | `DB_PORT`                  | `5432`                                                       | Porta do PostgreSQL        |
   | `JWT_SECRET`               | `example_secret_key`                                         | Chave secreta para JWT     |
   | `PGADMIN_DEFAULT_EMAIL`    | `admin@jtech.com.br`                                         | Email do pgAdmin           |
   | `PGADMIN_DEFAULT_PASSWORD` | `admin`                                                      | Senha do pgAdmin           |

2. Execute o ambiente completo (frontend + backend):
   ```bash
   docker compose up --build
   ```
3. A API estará disponível em [http://localhost:8000](http://localhost:8000)
4. Swagger UI: [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html)
5. pgAdmin: [http://localhost:5050](http://localhost:5050) (email: `admin@jtech.com.br` / senha: `admin`)
6. Frontend: [http://localhost:5173](http://localhost:5173)

## 4. Como Rodar os Testes

**Pré-requisito:** JDK 17 instalado e configurado (`java -version` deve mostrar `17`).

```bash
./mvnw test
```

Os testes usam **JUnit 5** + **Mockito** para testes unitários e **MockMvc** para testes de integração dos endpoints REST. O banco **H2** é utilizado automaticamente no lugar do PostgreSQL durante os testes.

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

### Uso de IA no Desenvolvimento

Usei inteligência artificial para otimizar o desenvolvimento das camadas de **repository**, **service**, **controller** e **DTO**, acelerando a escrita de código boilerplate e a estruturação dos módulos. Todo o código gerado foi revisado e adaptado por mim para garantir que atendesse aos requisitos do projeto.

### Uso do Docker

Optei por utilizar Docker para conteinerizar todos os serviços da aplicação, incluindo o frontend Vue.js (servido via Nginx), a API Spring Boot, o PostgreSQL e o pgAdmin. O docker-compose na raiz do projeto orquestra todos os containers, permitindo subir o ambiente completo com um único comando. O frontend utiliza um build multi-stage: a compilação é feita em um container Node.js e os arquivos estáticos são servidos pelo Nginx, resultando em uma imagem leve e otimizada para produção. Essa abordagem garante um ambiente de desenvolvimento consistente entre diferentes máquinas, eliminando a necessidade de instalar e configurar manualmente cada ferramenta, além de facilitar a integração com pipelines de CI/CD.

### Injeção de Dependência

Pensando no crescimento do nosso sistema, vejo a injeção de dependência como um passo fundamental. Ela nos ajuda a manter um bom encapsulamento, garante que os contratos sejam seguidos à risca e dá aquela flexibilidade enorme para trocar uma classe por outra sempre que a gente precisar.


## 7. Melhorias e Roadmap

### Curto prazo
- [ ] Criar uma representação da listagem de tarefas no Domínio e adicionar testes para isso

### Médio prazo
- [ ] Paginação no servidor para listas com muitas tarefas
- [ ] Logging estruturado para observabilidade
- [ ] Adicionar Flyway para manter o histórico das migrations

### Longo prazo
- [ ] Pipeline de CI/CD com GitHub Actions

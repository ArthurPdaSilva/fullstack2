# FullStack2 - JTech Challenge

Monorepo do desafio técnico Fullstack2 da JTech.

## Setup

1. Copie os arquivos de ambiente de exemplo:
   ```bash
   cp backend/.env.example backend/.env
   cp frontend/.env.example frontend/.env
   ```
2. Ajuste as variáveis nos arquivos `.env` conforme necessário.

## Docker (alternativa)

Para rodar a aplicação completa com Docker (PostgreSQL + API + pgAdmin + Frontend):

```bash
docker compose up --build
```

- API: [http://localhost:8000](http://localhost:8000)
- Swagger: [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html)
- Frontend: [http://localhost:5173](http://localhost:5173)
- pgAdmin: [http://localhost:5050](http://localhost:5050) (email: `admin@jtech.com.br` / senha: `admin`)

## README dos Projetos

- [**Backend**](backend/README.md) — API REST com Spring Boot, Spring Security, JPA, PostgreSQL e JWT
- [**Frontend**](frontend/README.md) — Aplicação com Vue 3, TypeScript, Pinia, Vuetify e Axios

## Estrutura

```
fullstack2/
├── backend/   # Aplicação Java + Spring Boot
├── frontend/  # Aplicação Vue 3 + Vite
└── EXPLANATION.md  # Enunciado original do desafio
```

📖 [**EXPLANATION.md**](EXPLANATION.md) — Enunciado original e requisitos completos do desafio.

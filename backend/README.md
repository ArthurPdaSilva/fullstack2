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

## 3. Como Rodar Localmente

<!-- Instruções passo a passo para setup e execução -->

## 4. Como Rodar os Testes

<!-- Comandos para executar suite completa de testes -->

## 5. Estrutura de Pastas Detalhada

<!-- Mapeamento completo da organização modular do código -->

## 6. Decisões Técnicas Aprofundadas

<!-- Justificativas sobre escolhas arquiteturais, padrões e bibliotecas -->

## 7. Melhorias e Roadmap

<!-- Propostas técnicas para evolução e escalabilidade -->

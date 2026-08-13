#  Votação API

Sistema de votação em tempo real, construído em **Java + Spring Boot**, com autenticação JWT,
persistência em PostgreSQL versionada via Flyway, documentação OpenAPI (Swagger) e testes
unitários. Projeto desenvolvido para portfólio, seguindo boas práticas de arquitetura em camadas,
segurança e regras de negócio auditáveis a nível de banco de dados.

---

##  Funcionalidades

- Cadastro e autenticação de usuários (JWT)
- Criação de enquetes com múltiplas opções de voto
- Votação com regras de integridade (um voto por usuário por enquete, apenas em enquetes abertas)
- Resultado em tempo real: total de votos, ranking por opção, percentuais e opção vencedora
- Encerramento de enquetes (restrito ao criador)
- Listagem paginada e filtrável por status

---

##  Tecnologias

| Categoria | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL |
| Versionamento de schema | Flyway |
| Autenticação | Spring Security + JWT (jjwt) |
| Documentação | springdoc-openapi (Swagger UI) |
| Testes | JUnit 5, Mockito, AssertJ |
| Build | Maven |

---

##  Arquitetura

Projeto organizado em camadas, com separação clara de responsabilidades:

```
com.gabrielf.votacao_api
├── config/          → configurações gerais (Security, OpenAPI)
├── controller/       → endpoints REST
├── domain/
│   ├── entity/       → entidades JPA
│   └── enums/        → enums de domínio
├── dto/
│   ├── request/       → DTOs de entrada (com Bean Validation)
│   └── response/      → DTOs de saída (com fromEntity factory methods)
├── exception/        → exceptions customizadas + handler global
├── repository/       → Spring Data JPA repositories
├── security/         → JWT, filtros, UserDetails
└── service/          → regras de negócio
```

**Modelo de dados:**

```
usuarios 1───N enquetes 1───N opcoes_voto
   │                              │
   └──────────1───N votos────────┘
```

---

##  Segurança

- Autenticação stateless via **JWT** (sem sessão, sem cookie)
- Senhas armazenadas com hash **BCrypt**
- Identidade do usuário sempre extraída do token autenticado — nunca aceita via parâmetro do
  cliente, eliminando a possibilidade de um usuário agir em nome de outro
- Regra "um voto por usuário por enquete" garantida em **duas camadas**: validação de aplicação
  (resposta rápida e amigável) e `UNIQUE constraint` no banco (garantia atômica sob concorrência)

---

##  Como rodar o projeto

### Pré-requisitos

- Java 17+
- Maven
- PostgreSQL rodando localmente (porta padrão `5432`)

### Passos

1. Clone o repositório
   ```bash
   git clone <url-do-repositorio>
   cd votacao-api
   ```

2. Crie o banco de dados
   ```sql
   CREATE DATABASE votacao_db;
   ```

3. Ajuste as credenciais em `src/main/resources/application-dev.yml`
   ```yaml
   spring:
     datasource:
       username: postgres
       password: sua_senha_aqui
   ```

4. Rode a aplicação (o Flyway aplica as migrations automaticamente)
   ```bash
   ./mvnw spring-boot:run
   ```

5. Acesse a documentação interativa
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

### Rodando os testes

```bash
./mvnw test
```

---

##  Endpoints principais

| Método | Rota | Descrição | Autenticação |
|---|---|---|---|
| `POST` | `/api/v1/usuarios` | Cadastrar usuário | Pública |
| `POST` | `/api/v1/auth/login` | Login (retorna token JWT) | Pública |
| `POST` | `/api/v1/enquetes` | Criar enquete | Obrigatória |
| `GET` | `/api/v1/enquetes` | Listar enquetes (paginado, filtro por status) | Obrigatória |
| `GET` | `/api/v1/enquetes/{id}` | Buscar enquete por id | Obrigatória |
| `POST` | `/api/v1/enquetes/{id}/votos` | Registrar voto | Obrigatória |
| `GET` | `/api/v1/enquetes/{id}/resultado` | Ver resultado da enquete | Obrigatória |
| `PATCH` | `/api/v1/enquetes/{id}/encerrar` | Encerrar enquete (apenas o criador) | Obrigatória |

Documentação completa e interativa disponível no Swagger UI após rodar a aplicação.

### Exemplo de fluxo

```bash
# 1. Cadastro
curl -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana","email":"ana@teste.com","senha":"123456"}'

# 2. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana@teste.com","senha":"123456"}'
# → retorna { "token": "...", "tipo": "Bearer" }

# 3. Criar enquete (usando o token do passo 2)
curl -X POST http://localhost:8080/api/v1/enquetes \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Linguagem favorita","pergunta":"Qual você escolheria?","opcoes":["Java","Python","JavaScript"]}'
```

---


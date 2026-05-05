# Library Manager API

API REST para gerenciamento de livros, construída com Java 21, Spring Boot, MongoDB e Redis.

## Stack

- Java 21
- Spring Boot 3.x
- Spring Data MongoDB
- Spring Data Redis (Spring Cache)
- Maven Wrapper
- Lombok
- ModelMapper 3.x
- SpringDoc OpenAPI 2.x
- Mongock (migracoes MongoDB)
- JUnit 5 + Mockito
- Testcontainers

## Funcionalidades

- CRUD completo de livros (`POST`, `GET` por id, `GET` paginado, `PUT`, `DELETE`)
- Validações de regra de negocio (ISBN unico, ano de publicacao valido)
- Cache Redis no endpoint de busca por id com TTL de 10 minutos
- Invalidacao de cache em atualizacao e exclusao
- Tratamento global de erros com resposta padronizada
- Documentacao OpenAPI/Swagger

## Estrutura de Camadas

- Controller: orquestracao HTTP
- Service: regras de negocio e cache
- Repository: acesso ao MongoDB
- DTOs: `record` para entrada/saida

## Dominio

Entidade `Livro`:

- `id` (String, gerado pelo MongoDB)
- `titulo` (String, obrigatorio)
- `autor` (String, obrigatorio)
- `isbn` (String, obrigatorio, unico)
- `anoPublicacao` (Integer, > 1000 e <= ano atual)
- `genero` (Enum `GeneroPojo`, obrigatorio)
- `disponivel` (Boolean, obrigatorio)
- `dataInclusao` (LocalDateTime, preenchida na criacao)
- `dataAtualizacao` (LocalDateTime, preenchida na atualizacao)

Enum `GeneroPojo`:

- `FICCAO_CIENTIFICA`
- `FANTASIA`
- `ROMANCE`
- `TERROR`
- `BIOGRAFIA`
- `HISTORIA`
- `TECNOLOGIA`
- `INFANTIL`

## Como subir a infraestrutura

No diretorio do projeto:

```bash
docker compose up -d
```

Servicos:

- MongoDB: `localhost:27017`
- Redis: `localhost:6379`

## Configuracao da aplicacao

Arquivo: `src/main/resources/application.properties`

Variaveis importantes:

- `MONGODB_URI` (opcional)
  - default: `mongodb://admin:admin123@localhost:27017/library_manager?authSource=admin`
- `REDIS_HOST` (opcional)
  - default: `localhost`
- `REDIS_PORT` (opcional)
  - default: `6379`

Migracoes MongoDB:

- `mongock.enabled=true`
- `mongock.migration-scan-package=com.library.library_manager.config.migration`

## Rodando a aplicacao

```bash
./mvnw spring-boot:run
```

## Documentacao da API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Endpoints

### Criar livro

- `POST /livros`
- Retorno: `201 Created`

Exemplo de payload:

```json
{
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "isbn": "9780132350884",
  "anoPublicacao": 2008,
  "genero": "TECNOLOGIA",
  "disponivel": true
}
```

### Buscar livro por id

- `GET /livros/{id}`
- Retorno: `200 OK`
- Usa cache Redis com TTL de 10 minutos

### Listar livros

- `GET /livros?pagina=0&tamanho=10&genero=TECNOLOGIA`
- `genero` e opcional
- Retorno: `200 OK` com pagina

### Atualizar livro

- `PUT /livros/{id}`
- Retorno: `200 OK`
- Invalida cache do livro

### Remover livro

- `DELETE /livros/{id}`
- Retorno: `204 No Content`
- Invalida cache do livro

## Padrao de erro

Exemplo:

```json
{
  "codigo": "LIVRO_NAO_ENCONTRADO",
  "mensagem": "Livro com id '123' nao encontrado.",
  "timestamp": "2025-04-27T14:30:00"
}
```

## Testes

### Unitarios (Service)

- Sucesso em criacao/busca/remocao
- Erros de negocio (ISBN duplicado, livro nao encontrado)

### Integracao (Controller)

- `POST`, `GET`, `PUT`, `DELETE`
- Cenarios de erro `400` e `404`
- MongoDB e Redis reais com Testcontainers

Para executar:

```bash
./mvnw test
```

## Validando cache Redis

Para validar se o cache esta sendo criado e invalidado:

1. Crie ou use um livro existente e pegue o `id`.
2. Chame `GET /livros/{id}` para popular o cache.
3. Execute os comandos abaixo no `redis-cli`.

Comandos uteis:

```bash
redis-cli -h localhost -p 6379
KEYS biblioteca:livro:*
KEYS *livros*
TTL biblioteca:livro:<id>
SCAN 0 MATCH *livro* COUNT 100
```

Fluxo esperado para comprovacao:

1. `GET /livros/{id}` -> chave aparece no Redis.
2. `PUT /livros/{id}` -> chave e invalidada.
3. `GET /livros/{id}` -> chave e recriada.
4. `DELETE /livros/{id}` -> chave e invalidada novamente.

Observacao: com `TTL` de 10 minutos, a chave expira automaticamente apos esse periodo.

## Versionamento de scripts (Mongo)

As migracoes de banco sao executadas com Mongock no startup da aplicacao.

ChangeUnits criadas:

- `V001_create_livros_collection_and_indexes`
  - Cria a collection `livros`
  - Cria indice unico para `isbn`
- `V002_seed_livros_data`
  - Insere livro inicial de exemplo (`Clean Code`)

# book.manager

A Spring Boot book manager for interviews and demos.

## Architecture

The project has **two transport layers that share the same service layer**:

```
Angular / HTTP client
      │
      ├── BookController       (@RestController)  → REST  /api/book/**
      │
      └── BookGraphqlController (@Controller)     → GraphQL /api/graphql
                │
                └── BookService / ReviewService   (business logic)
                          │
                          └── BookRepository / ReviewRepository  (JPA / H2)
```

**GraphQL is the primary interface.** It is used by the frontend and covers all queries and mutations via a typed schema (`schema.graphqls`).

**REST is kept as a secondary interface** — useful for quick curl checks, tooling, or when a consumer prefers plain HTTP. Both controllers call the exact same service methods, so there is no duplicated business logic.

This separation means:
- Adding a new transport (e.g. gRPC) only requires a new controller, not changes to the service layer.
- Each controller is a thin adapter: it translates HTTP/GraphQL input into DTOs and delegates to the service.

## What it shows

- GraphQL queries and mutations (primary)
- REST API for books and reviews (secondary / tooling)
- Service-layer validation and custom exceptions
- JPA persistence with H2 for local/test runs
- Integration tests for both transports
- Centralized error handling via `@RestControllerAdvice`
- Dynamic filtering + pagination with `JpaSpecificationExecutor`

## Run

```zsh
./mvnw spring-boot:run
```

## Test

```zsh
./mvnw test
```

## REST examples

Create a book:

```bash
curl -X POST http://localhost:8443/api/book/ \
  -H 'Content-Type: application/json' \
  -d '{"title":"Effective Java","author":"Joshua Bloch","isbn":"1234567890","publicationYear":2024,"description":"Java best practices"}'
```

Search books:

```bash
curl 'http://localhost:8443/api/book/?author=Joshua%20Bloch&page=0&size=10'
```

Add a review:

```bash
curl -X POST http://localhost:8443/api/book/1/review \
  -H 'Content-Type: application/json' \
  -d '{"content":"Great read","rating":5}'
```

## GraphQL examples

Create a book:

```graphql
mutation {
  createBook(input: {
    title: "Effective Java"
    author: "Joshua Bloch"
    isbn: "1234567890"
    publicationYear: 2024
    description: "Java best practices"
  }) {
    id
    title
  }
}
```

Query reviews:

```graphql
query {
  reviewsByBookId(bookId: 1) {
    id
    content
    rating
  }
}
```

## Interview talking points

- **Dual transport, single service layer** — REST and GraphQL both delegate to the same `BookService`; the transport decision is kept at the controller edge
- **GraphQL-first** — schema-first design with typed queries, mutations, and input types
- **Dynamic filtering** — `JpaSpecificationExecutor` builds predicates at runtime based on optional `author` / `titleContains` params; no multiple repository overloads
- **Centralized error handling** — `@RestControllerAdvice` maps domain exceptions to structured `ApiErrorResponse` (timestamp, status, message, path)
- **Validation at the boundary** — `@Valid` + Bean Validation on every DTO; constraint violations return 400 with the full error message
- **Integration tested** — JDK `HttpClient` tests cover both REST and GraphQL end-to-end without any extra test libraries
- **Natural next steps** — auth (Spring Security + JWT), Docker, CI pipeline, observability (Actuator + Micrometer)


# UTP+Recommends Backend Design

**Date:** 2026-06-30

**Project Root:** `C:\WebProyecto`

## 1. Objective

Build the full backend for UTP+Recommends as a single Spring Boot 3.x application with modular package boundaries, JWT-based security, JPA persistence aligned exactly to the existing MySQL schema, automated tests, and technical documentation. The backend must implement the approved functional requirements for authentication, administrative management, student reviews, moderation, requests, and public listings without modifying the existing database structure.

## 2. Official Constraints

- The backend must be built in `C:\WebProyecto`.
- The final repository destination remains `https://github.com/FelixTDev/UTP-Recommends_SpringBoot.git`.
- The MySQL schema already exists and must not be modified.
- Hibernate must use `spring.jpa.hibernate.ddl-auto=validate`.
- Do not use `ddl-auto=update`, `create`, or `create-drop`.
- Do not alter tables, columns, types, constraints, enums, or relationships.
- Do not use Flyway or Liquibase to change the existing schema.
- Use DTOs for all request and response contracts.
- Do not serialize JPA entities directly in API responses.
- Keep business rules in services, not in controllers or repositories.
- Keep admin, student, and public routes separate.
- JWT must include `userId`, `rol`, and `estado`.
- Any schema inconsistency discovered during implementation must be documented in `OBSERVACIONES_BD.md` without changing the schema.

## 3. Technical Stack

- Java 17
- Maven
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- MySQL 8 driver
- JWT support with a stable library such as `jjwt`
- JUnit 5
- Mockito
- MockMvc
- Testcontainers with MySQL 8

## 4. Architecture Decision

The system will be implemented as a single Spring Boot deployable artifact. Internal organization will follow modular package boundaries by actor and domain, while preserving layered responsibilities inside each module.

### 4.1 Package Structure

```text
src/main/java/com/utp/recommends/
├── common/
│   ├── exception/
│   ├── response/
│   ├── validation/
│   └── util/
├── config/
├── security/
├── domain/
│   ├── entity/
│   └── enums/
├── repository/
├── auth/
│   ├── controller/
│   ├── service/
│   ├── dto/request/
│   ├── dto/response/
│   └── mapper/
├── admin/
│   ├── usuario/
│   ├── carrera/
│   ├── docente/
│   ├── curso/
│   ├── curso_docente/
│   ├── criterio/
│   ├── moderacion_resena/
│   └── moderacion_solicitud/
├── estudiante/
│   ├── resena/
│   └── solicitud/
└── publicapi/
    ├── controller/
    ├── service/
    └── dto/response/
```

### 4.2 Layering Rules

- `controller`: receive requests, validate DTOs, delegate to services, return responses.
- `service`: own business rules, transactional boundaries, and orchestration.
- `repository`: persistence and query access only.
- `dto/request`: validated request payloads.
- `dto/response`: safe response contracts.
- `mapper`: entity to DTO and DTO to entity mapping when it improves clarity.

Controllers must not enforce domain workflows. Repositories must not implement business policies. Services must remain the single source of truth for functional rules.

## 5. Domain Model and Persistence

### 5.1 Entities

The JPA layer will map these entities exactly to the existing schema:

- `Usuario`
- `Carrera`
- `Estudiante`
- `Docente`
- `Curso`
- `CursoDocente`
- `CriterioCalificacion`
- `Resena`
- `ResenaCalificacion`
- `Solicitud`

### 5.2 Enums

- `RolUsuario`: `ADMIN`, `ESTUDIANTE`
- `EstadoUsuario`: `ACTIVO`, `INACTIVO`, `SUSPENDIDO`
- `EstadoSimple`: `ACTIVO`, `INACTIVO`
- `EstadoCarrera`: `ACTIVA`, `INACTIVA`
- `TipoCurso`: `GENERAL`, `CARRERA`
- `EstadoResena`: `PENDIENTE`, `APROBADA`, `RECHAZADA`, `OCULTA`
- `TipoSolicitud`: `CURSO_NUEVO`, `DOCENTE_NUEVO`, `AMBOS`
- `EstadoSolicitud`: `PENDIENTE`, `APROBADA`, `RECHAZADA`

### 5.3 Relationship Mapping

- `Usuario` 1:1 `Estudiante`
- `Carrera` 1:N `Estudiante`
- `Carrera` 1:N `Curso` for `TipoCurso.CARRERA`
- `Curso` N:N `Docente` through `CursoDocente`
- `Estudiante` 1:N `Resena`
- `CursoDocente` 1:N `Resena`
- `Resena` 1:N `ResenaCalificacion`
- `CriterioCalificacion` 1:N `ResenaCalificacion`
- `Resena` self-reference through `resenaAnterior`
- `Estudiante` 1:N `Solicitud`
- `Solicitud` 1:1 `Resena` through `resenaGenerada`
- `Usuario` with role `ADMIN` 1:N `Resena` as moderator
- `Usuario` with role `ADMIN` 1:N `Solicitud` as resolver

### 5.4 Persistence Notes

- Table and column names will follow the existing schema, including snake_case names.
- Schema validation will be enforced with `ddl-auto=validate`.
- Generated and managed database columns must not be written from JPA when the schema owns their value.
- `Resena.claveActiva` will be mapped as read-only:

```java
@Column(name = "clave_activa", insertable = false, updatable = false)
private String claveActiva;
```

- Timestamp columns already managed by MySQL will be mapped without introducing schema changes.
- No schema generation feature will be used in development, tests, or runtime against the existing database.

## 6. Validation Rules

### 6.1 User and Identity Rules

- Student email must match `^U[0-9]{8}@utp\.edu\.pe$`.
- Student code is extracted from the email local part and is never requested separately.
- Admin email must belong to `@utp.edu.pe`.
- Names and surnames accept only letters, spaces, accents, and `ñ`, with length from 2 to 100.

### 6.2 Password Rule

The password rule is closed and mandatory:

- minimum 8 characters
- at least 1 uppercase letter
- at least 1 lowercase letter
- at least 1 number
- at least 1 special character
- no spaces

Recommended backend regex:

```regex
^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%&*?_\-])[A-Za-z\d!@#$%&*?_\-]{8,}$
```

Passwords must always be stored with BCrypt.

### 6.3 Business State Rules

- User states: `ACTIVO`, `INACTIVO`, `SUSPENDIDO`
- Career states: `ACTIVA`, `INACTIVA`
- Course, teacher, course-teacher, and criteria states use active/inactive soft state transitions
- Review states: `PENDIENTE`, `APROBADA`, `RECHAZADA`, `OCULTA`
- Request states: `PENDIENTE`, `APROBADA`, `RECHAZADA`

## 7. Security Design

### 7.1 Authentication

- `POST /api/auth/register` is public.
- `POST /api/auth/login` is public.
- `GET /api/auth/me` requires authentication.
- Successful login returns a JWT bearer token plus minimal user data.
- Login must reject `INACTIVO` and `SUSPENDIDO` users with HTTP 403 and explicit messages.

### 7.2 Authorization

- `/api/auth/**`: public
- `/api/public/**`: public
- `/api/admin/**`: `ADMIN` only
- `/api/estudiante/**`: `ESTUDIANTE` only
- any other route: authenticated

JWT claims must include:

- `userId`
- `rol`
- `estado`

### 7.3 API Security Mechanics

- Stateless session policy
- CSRF disabled for REST API usage
- JWT filter before `UsernamePasswordAuthenticationFilter`
- Custom JSON `AuthenticationEntryPoint`
- Custom JSON `AccessDeniedHandler`
- A dedicated authenticated-user access component to resolve the current `Usuario` and `Estudiante` from the security context

## 8. Module Design

### 8.1 Auth Module

Endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Rules:

- Register validates student email format, password strength, active career, unique email, and unique student code.
- Register creates `Usuario` with role `ESTUDIANTE` and state `ACTIVO`, then creates `Estudiante`.
- Login validates credentials and issues JWT with the required claims.
- `me` returns authenticated user metadata without exposing `password_hash`.

### 8.2 Admin Modules

#### Usuarios

- `GET /api/admin/usuarios`
- `GET /api/admin/usuarios/{id}`
- `PUT /api/admin/usuarios/{id}`
- `PATCH /api/admin/usuarios/{id}/estado`

Rules:

- Optional filters by role and state
- No physical delete
- Never expose `passwordHash`

#### Carreras

- CRUD under `/api/admin/carreras`
- Public `GET /api/public/carreras/activas`

Rules:

- Inactivate instead of physical delete
- Registration uses only active careers

#### Docentes

- CRUD under `/api/admin/docentes`
- Optional active listing for selection forms

Rules:

- Functional delete by `estado = INACTIVO`
- Do not physically remove historical records

#### Cursos

- CRUD under `/api/admin/cursos`
- Active listing for selection forms

Rules:

- `GENERAL` forces `carreraId = null`
- `CARRERA` requires active `carreraId`
- Duplicate `GENERAL` course names are validated manually in service
- Functional delete by inactive state

#### Curso-Docente

- `POST /api/admin/curso-docente`
- `GET /api/admin/curso-docente`
- `GET /api/admin/cursos/{cursoId}/docentes`
- `GET /api/admin/docentes/{docenteId}/cursos`
- `PATCH /api/admin/curso-docente/{id}/estado`

Rules:

- No duplicate course-teacher relation
- Only active relations accept new reviews
- Historical relations remain and are inactivated instead of deleted

#### Criterios

- CRUD under `/api/admin/criterios`
- Public `GET /api/public/criterios/activos`

Rules:

- Activate and deactivate instead of physical deletion when history exists
- Review forms load active criteria dynamically

### 8.3 Estudiante Modules

#### Reseñas

- `POST /api/estudiante/resenas`
- `GET /api/estudiante/resenas/mis-resenas`
- `GET /api/estudiante/resenas/mis-resenas/{id}`

Rules:

- Student identity is resolved from JWT, never from request body
- Review requires an active `CursoDocente`
- Comment minimum length is 10
- Exactly one score per active criterion is required
- Each score must be between 1 and 5
- Incoming review state from client is ignored
- New review starts as `PENDIENTE`
- If an active `PENDIENTE` review exists for the same student and course-teacher pair, update the same row and replace scores
- If an `APROBADA` review exists for the pair, return 409
- If the last review is `RECHAZADA`, create a new version linked by `resenaAnterior`
- Any unique violation on `clave_activa` is translated to a controlled 409 business error

#### Solicitudes

- `POST /api/estudiante/solicitudes`
- `GET /api/estudiante/solicitudes/mis-solicitudes`
- `GET /api/estudiante/solicitudes/mis-solicitudes/{id}`

Rules:

- Student identity is resolved from JWT
- `CURSO_NUEVO` requires `nombreCursoSugerido`
- `DOCENTE_NUEVO` requires `nombreDocenteSugerido`
- `AMBOS` requires both
- Comment minimum length is 10
- Initial state is `PENDIENTE`

### 8.4 Moderation Modules

#### Moderación de Reseñas

- `GET /api/admin/moderacion/resenas?estado=PENDIENTE`
- `POST /api/admin/moderacion/resenas/{id}/aprobar`
- `POST /api/admin/moderacion/resenas/{id}/rechazar`
- `POST /api/admin/moderacion/resenas/{id}/ocultar`

Rules:

- Approve and reject only from `PENDIENTE`
- Reject requires `motivoRechazo`
- Moderation stores admin and moderation timestamp
- Already moderated reviews return 409
- Hide only allows `APROBADA -> OCULTA`

#### Moderación de Solicitudes

- `GET /api/admin/moderacion/solicitudes?estado=PENDIENTE`
- `POST /api/admin/moderacion/solicitudes/{id}/aprobar`
- `POST /api/admin/moderacion/solicitudes/{id}/rechazar`

Rules:

- Approve and reject only from `PENDIENTE`
- Reject requires `motivoRechazo`
- Resolution stores admin and resolution timestamp

Approval flow is fully specified and mandatory:

1. Validate the request is still `PENDIENTE`.
2. Validate the incoming approval request contains explicit scores for the active criteria set.
3. Verify there is exactly one score for every active criterion.
4. Reject duplicate criterion ids.
5. Reject missing active criteria.
6. Validate every score is between 1 and 5.
7. Create teacher if the request type requires a new teacher.
8. Create course if the request type requires a new course.
9. Create or reuse the matching `CursoDocente`.
10. Create a `Resena` directly in `APROBADA` using the request comment and moderator metadata.
11. Create the associated `ResenaCalificacion` rows in the same transaction.
12. Link `Solicitud.resenaGenerada`.
13. Mark the request `APROBADA` and set resolution metadata.
14. Roll back the whole transaction if any step fails.

If the criteria score set is incomplete, duplicated, invalid, or mismatched to active criteria, the service returns HTTP 400 and does not approve the request.

## 9. Public API Design

Endpoints:

- `GET /api/public/resenas`
- `GET /api/public/resenas/curso-docente/{cursoDocenteId}`
- `GET /api/public/resenas/curso/{cursoId}`
- `GET /api/public/resenas/promedios/curso-docente/{cursoDocenteId}`

Rules:

- Only `APROBADA` reviews are returned
- `OCULTA`, `PENDIENTE`, and `RECHAZADA` reviews never appear publicly
- Public DTOs must never expose student identity when `esAnonimo = true`
- Pagination uses query parameters such as `page`, `size`, and `sort`
- Rating averages must be computed through repository aggregated queries, not by loading all rows into memory

## 10. Error Handling Design

A global `@RestControllerAdvice` will return a standard JSON error body:

```json
{
  "timestamp": "2026-06-30T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "mensaje claro",
  "path": "/api/...",
  "fieldErrors": []
}
```

Handled categories:

- DTO validation failures: HTTP 400
- Business conflicts: HTTP 409
- Missing resources: HTTP 404
- Authentication failures: HTTP 401
- Authorization failures: HTTP 403
- Unexpected errors: HTTP 500 with controlled message

Database errors such as unique, foreign key, or check violations must be translated to business-readable messages rather than raw SQL exceptions.

## 11. Repositories and Query Requirements

Required repository capabilities include:

- find user by email
- check user existence by email
- find student by user id
- list active careers
- list active courses
- list active teachers
- list active criteria
- find active course-teacher relation by id
- find active review by student and course-teacher
- find latest review by student and course-teacher
- list pending reviews ordered by creation date
- list pending requests ordered by creation date
- compute criterion averages with aggregated repository queries

## 12. Implementation Phases

### Phase 1: Project Bootstrap

- initialize or prepare the project in `C:\WebProyecto`
- define Maven build and dependencies
- create application configuration files
- establish common response and exception handling
- prepare testing configuration and environment templates

### Phase 2: Domain and Persistence

- map entities and enums exactly to the existing schema
- create repositories and custom queries
- validate schema alignment with Hibernate validate mode

### Phase 3: Security and Auth

- implement JWT infrastructure
- implement register, login, and `me`
- protect routes by role and authentication state

### Phase 4: Admin Catalogs

- implement users, careers, teachers, courses, course-teacher, and criteria modules
- enforce domain rules in services

### Phase 5: Student and Moderation Modules

- implement reviews, requests, review moderation, and request moderation
- implement versioning, conflict detection, and transaction flows

### Phase 6: Public API, Documentation, and Hardening

- implement public listings and aggregated rating endpoints
- complete documentation artifacts
- close verification gaps and final quality review

## 13. Testing Strategy

### 13.1 Unit Tests with Mockito

Minimum required service tests:

1. successful student registration
2. registration fails for non-institutional email
3. registration fails for weak password
4. successful login
5. login blocked for inactive or suspended user
6. `GENERAL` course creation forces null career
7. `CARRERA` course creation requires active career
8. new review is created as `PENDIENTE`
9. existing pending review is updated instead of duplicated
10. approved existing review returns conflict
11. rejected review resend creates a new version
12. rejecting a review requires reason
13. request approval executes the full transactional flow

### 13.2 Integration Tests with Testcontainers MySQL 8

Minimum required persistence tests:

1. persist `Usuario`, `Estudiante`, and `Carrera`
2. enforce unique email
3. enforce unique `curso_docente`
4. enforce unique `clave_activa`
5. enforce score `CHECK` between 1 and 5
6. validate schema startup with `ddl-auto=validate`

### 13.3 Controller Tests with MockMvc

Minimum required API tests:

1. `POST /api/auth/register` returns 201
2. `POST /api/auth/login` returns token
3. `/api/admin/**` without token returns 401
4. `/api/admin/**` with student role returns 403
5. `/api/estudiante/**` with admin role returns 403
6. `GET /api/public/resenas` returns only approved reviews
7. anonymous public review does not expose student identity

## 14. Documentation Artifacts

The project must include:

- `README.md`
- `OBSERVACIONES_BD.md`
- `docs/superpowers/specs/2026-06-30-utp-recommends-backend-design.md`
- `docs/superpowers/plans/`
- optional `.env.example`
- optional `docker-compose.yml` for local MySQL support only, never as a schema replacement mechanism

`README.md` must cover:

- project name
- objective
- technologies
- modular architecture
- directory structure
- prerequisites
- environment variables
- connection to the existing MySQL database
- run instructions
- test instructions
- grouped endpoint summary
- key business rules
- evidence aligned to the rubric

## 15. Risks and Mitigations

- Risk: schema mismatches between JPA and MySQL.
  Mitigation: use exact column mappings early and validate with MySQL-backed integration tests.
- Risk: leaking student identity in public anonymous reviews.
  Mitigation: use dedicated public response DTOs and explicit mapper logic.
- Risk: duplicate active reviews under concurrency.
  Mitigation: validate in service and translate `clave_activa` unique constraint violations.
- Risk: partial request approval side effects.
  Mitigation: keep request approval fully transactional and cover it with unit and integration tests.
- Risk: mixing admin and student responsibilities.
  Mitigation: enforce package separation, route separation, and distinct DTOs per module.

## 16. Acceptance Criteria

- Maven build completes with `mvn clean install`
- Application starts against the existing MySQL database without schema mutation
- Hibernate validates the schema successfully
- JWT authentication works
- Role-based authorization works
- Main CRUD and moderation flows are implemented
- Business rules live in services
- No JPA entities are exposed directly
- No plaintext password is stored or returned
- No stacktrace is exposed to clients
- Required tests exist and pass
- Required documentation artifacts exist

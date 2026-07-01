# UTP+Recommends Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the full UTP+Recommends Spring Boot backend in `C:\WebProyecto` against the existing MySQL schema, with modular architecture, JWT security, business-rule services, tests, and documentation.

**Architecture:** The backend is a single Spring Boot 3.x Maven application organized by actor and domain modules (`auth`, `admin`, `estudiante`, `publicapi`) with shared `security`, `domain`, `repository`, `common`, and `config` packages. All persistence maps exactly to the existing schema, uses DTO-based API contracts, and enforces business logic in services with transactional boundaries where required.

**Tech Stack:** Java 21, Maven, Spring Boot 3.x, Spring Web, Spring Data JPA, Spring Security, Bean Validation, MySQL 8 driver, JWT (`jjwt`), JUnit 5, Mockito, MockMvc, Testcontainers MySQL 8

## Global Constraints

- Build only inside `C:\WebProyecto`.
- Keep the final remote destination as `https://github.com/FelixTDev/UTP-Recommends_SpringBoot.git`.
- Do not modify the existing MySQL schema.
- Use `spring.jpa.hibernate.ddl-auto=validate` exactly.
- Do not use `ddl-auto=update`, `create`, or `create-drop`.
- Do not alter tables, columns, types, constraints, enums, or relationships.
- Do not use Flyway or Liquibase to change the existing schema.
- Use DTOs for all request and response contracts.
- Do not expose JPA entities directly in API responses.
- Keep controllers thin and services responsible for business rules.
- Keep `/api/admin/**`, `/api/estudiante/**`, and `/api/public/**` separated.
- JWT must include `userId`, `rol`, and `estado`.
- Student-owned flows must resolve the student from JWT, never from request body.
- Public review responses must hide student identity when `esAnonimo = true`.
- Password rule: minimum 8 characters, at least 1 uppercase, 1 lowercase, 1 number, 1 special character, and no spaces.
- Map `resena.clave_activa` as read-only with `@Column(name = "clave_activa", insertable = false, updatable = false)`.
- Document any schema inconsistency in `OBSERVACIONES_BD.md` without changing the schema.

---

### Task 1: Bootstrap del proyecto

**Files:**
- Create: `C:\WebProyecto\pom.xml`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\UtpRecommendsApplication.java`
- Create: `C:\WebProyecto\src\main\resources\application.yml`
- Create: `C:\WebProyecto\src\main\resources\application-test.yml`
- Create: `C:\WebProyecto\.gitignore`
- Create: `C:\WebProyecto\.env.example`
- Create: `C:\WebProyecto\docker-compose.yml`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\common\response\ApiErrorResponse.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\common\response\ApiSuccessResponse.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\common\exception\BusinessException.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\common\exception\ResourceNotFoundException.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\common\exception\GlobalExceptionHandler.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\support\AbstractContainerIntegrationTest.java`

**Interfaces:**
- Consumes: none
- Produces:
  - Maven project root with Spring Boot packaging
  - shared error contract `ApiErrorResponse`
  - base integration test support using MySQL Testcontainers

- [ ] **Step 1: Write the failing bootstrap verification test**

```java
package com.utp.recommends;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UtpRecommendsApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn -Dtest=UtpRecommendsApplicationTests test`
Expected: FAIL because `pom.xml` and application bootstrap classes do not exist yet.

- [ ] **Step 3: Create the minimal project structure and build file**

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.1</version>
    </parent>
    <groupId>com.utp</groupId>
    <artifactId>utp-recommends</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <properties>
        <java.version>21</java.version>
        <jjwt.version>0.12.6</jjwt.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mysql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

```java
package com.utp.recommends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UtpRecommendsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtpRecommendsApplication.class, args);
    }
}
```

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/utp_recommends}
    username: ${SPRING_DATASOURCE_USERNAME:root}
    password: ${SPRING_DATASOURCE_PASSWORD:root}
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
```

- [ ] **Step 4: Add common API error handling and test support**

```java
package com.utp.recommends.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
```

```java
package com.utp.recommends.common.exception;

import com.utp.recommends.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
            new ApiErrorResponse(OffsetDateTime.now(), 400, "Bad Request", "Validation failed", request.getRequestURI(), List.of())
        );
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus()).body(
            new ApiErrorResponse(OffsetDateTime.now(), ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage(), request.getRequestURI(), List.of())
        );
    }
}
```

- [ ] **Step 5: Run bootstrap verification**

Run: `mvn -Dtest=UtpRecommendsApplicationTests test`
Expected: PASS

- [ ] **Step 6: Run compile verification**

Run: `mvn -q -DskipTests compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 7: Commit**

```bash
git add pom.xml src/main src/test .gitignore .env.example docker-compose.yml
git commit -m "chore: bootstrap spring boot backend"
```

**Endpoints:** none yet

**Business rules to implement in this task:**
- global API error format
- `ddl-auto=validate` only
- externalized datasource and JWT configuration

**Associated tests:**
- context load test
- future integration test base class compiles

**Acceptance criteria:**
- Maven project builds
- Spring Boot context can start
- `application.yml` uses `ddl-auto=validate`
- base error contract exists

**Verification commands:**
- `mvn -Dtest=UtpRecommendsApplicationTests test`
- `mvn -q -DskipTests compile`

### Task 2: Dominio y persistencia

**Files:**
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\RolUsuario.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\EstadoUsuario.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\EstadoSimple.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\EstadoCarrera.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\TipoCurso.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\EstadoResena.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\TipoSolicitud.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\enums\EstadoSolicitud.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\Usuario.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\Carrera.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\Estudiante.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\Docente.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\Curso.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\CursoDocente.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\CriterioCalificacion.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\Resena.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\ResenaCalificacion.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\domain\entity\Solicitud.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\UsuarioRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\CarreraRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\EstudianteRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\DocenteRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\CursoRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\CursoDocenteRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\CriterioCalificacionRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\ResenaRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\ResenaCalificacionRepository.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\repository\SolicitudRepository.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\repository\SchemaValidationIntegrationTest.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\repository\ConstraintIntegrationTest.java`

**Interfaces:**
- Consumes:
  - `ApiErrorResponse`
  - project bootstrap from Task 1
- Produces:
  - entities `Usuario`, `Carrera`, `Estudiante`, `Docente`, `Curso`, `CursoDocente`, `CriterioCalificacion`, `Resena`, `ResenaCalificacion`, `Solicitud`
  - repositories for all persistence operations listed in the spec

- [ ] **Step 1: Write failing persistence tests**

```java
package com.utp.recommends.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ConstraintIntegrationTest {

    @Test
    void enforcesUniqueEmail() {
        throw new UnsupportedOperationException("write repositories and entities");
    }
}
```

```java
package com.utp.recommends.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SchemaValidationIntegrationTest {

    @Test
    void startsWithHibernateValidateAgainstMysqlSchema() {
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn -Dtest=ConstraintIntegrationTest,SchemaValidationIntegrationTest test`
Expected: FAIL because entities, repositories, and test container wiring are missing.

- [ ] **Step 3: Create enums and entity mappings**

```java
package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.EstadoResena;
import jakarta.persistence.*;

@Entity
@Table(name = "resena")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoResena estado;

    @Column(name = "clave_activa", insertable = false, updatable = false)
    private String claveActiva;
}
```

```java
package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.TipoSolicitud;
import jakarta.persistence.*;

@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoSolicitud tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
}
```

- [ ] **Step 4: Create repository interfaces and query contracts**

```java
package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

```java
package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Resena;
import com.utp.recommends.domain.enums.EstadoResena;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    @Query("""
        select r from Resena r
        where r.estudiante.id = :estudianteId
          and r.cursoDocente.id = :cursoDocenteId
          and r.estado in :estados
    """)
    Optional<Resena> findActiveByStudentAndCursoDocente(Long estudianteId, Long cursoDocenteId, java.util.Collection<EstadoResena> estados);
}
```

- [ ] **Step 5: Implement MySQL Testcontainers base and schema validation test**

```java
package com.utp.recommends.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public abstract class AbstractContainerIntegrationTest {

    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4")
        .withDatabaseName("utp_recommends")
        .withUsername("test")
        .withPassword("test");

    static {
        MYSQL.start();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }
}
```

- [ ] **Step 6: Run persistence verification**

Run: `mvn -Dtest=ConstraintIntegrationTest,SchemaValidationIntegrationTest test`
Expected: PASS after loading the official schema into the test database before context startup.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/utp/recommends/domain src/main/java/com/utp/recommends/repository src/test/java/com/utp/recommends/repository src/test/java/com/utp/recommends/support
git commit -m "feat: map domain entities and repositories"
```

**Endpoints:** none yet

**Business rules to implement in this task:**
- exact JPA mapping to schema
- read-only generated column handling
- repository contracts for active/inactive state queries and review lookups

**Associated tests:**
- schema validation against MySQL
- unique email
- unique `curso_docente`
- unique `clave_activa`
- check `puntaje` range

**Acceptance criteria:**
- Hibernate validates the schema with no mutation
- repositories compile with required queries
- generated column mapping is read-only

**Verification commands:**
- `mvn -Dtest=ConstraintIntegrationTest,SchemaValidationIntegrationTest test`
- `mvn -q -DskipTests compile`

### Task 3: Seguridad y auth

**Files:**
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\security\JwtService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\security\JwtAuthenticationFilter.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\security\SecurityConfig.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\security\CustomAuthenticationEntryPoint.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\security\CustomAccessDeniedHandler.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\security\AuthenticatedUserService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\auth\controller\AuthController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\auth\service\AuthService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\auth\dto\request\RegisterRequest.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\auth\dto\request\LoginRequest.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\auth\dto\response\AuthResponse.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\auth\dto\response\CurrentUserResponse.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\auth\AuthServiceTest.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\auth\AuthControllerTest.java`

**Interfaces:**
- Consumes:
  - `UsuarioRepository`
  - `EstudianteRepository`
  - `CarreraRepository`
  - domain enums and entities
- Produces:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/auth/me`
  - JWT authentication infrastructure with claims `userId`, `rol`, `estado`

- [ ] **Step 1: Write failing auth service and controller tests**

```java
package com.utp.recommends.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AuthServiceTest {

    @Test
    void registerFailsForWeakPassword() {
        throw new UnsupportedOperationException("AuthService not implemented");
    }
}
```

```java
package com.utp.recommends.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest
class AuthControllerTest {

    @Test
    void loginReturnsBearerToken() {
        throw new UnsupportedOperationException("AuthController not implemented");
    }
}
```

- [ ] **Step 2: Run auth tests to verify they fail**

Run: `mvn -Dtest=AuthServiceTest,AuthControllerTest test`
Expected: FAIL because JWT, auth DTOs, controller, and service do not exist.

- [ ] **Step 3: Implement JWT and route security**

```java
package com.utp.recommends.security;

import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String secret;

    @Value("${app.security.jwt.expiration-minutes:30}")
    private long expirationMinutes;

    public String generateToken(Long userId, String rol, String estado, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(subject)
            .claims(Map.of("userId", userId, "rol", rol, "estado", estado))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
            .signWith(KeysProvider.hmacKey(secret))
            .compact();
    }
}
```

```java
package com.utp.recommends.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/estudiante/**").hasRole("ESTUDIANTE")
            .anyRequest().authenticated()
        );
        http.addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

- [ ] **Step 4: Implement register, login, and me**

```java
package com.utp.recommends.auth.service;

import com.utp.recommends.auth.dto.request.RegisterRequest;
import com.utp.recommends.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(String email, String password);
}
```

```java
package com.utp.recommends.auth.controller;

import com.utp.recommends.auth.dto.request.LoginRequest;
import com.utp.recommends.auth.dto.request.RegisterRequest;
import com.utp.recommends.auth.dto.response.AuthResponse;
import com.utp.recommends.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }
}
```

- [ ] **Step 5: Run auth verification**

Run: `mvn -Dtest=AuthServiceTest,AuthControllerTest test`
Expected: PASS

- [ ] **Step 6: Run security regression verification**

Run: `mvn -Dtest=AuthControllerTest test`
Expected: PASS with `register` returning 201 and `login` returning a bearer token contract.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/utp/recommends/security src/main/java/com/utp/recommends/auth src/test/java/com/utp/recommends/auth
git commit -m "feat: add jwt authentication and auth module"
```

**Endpoints:**
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

**Business rules to implement in this task:**
- UTP student email validation
- password strength validation
- active career validation
- unique email and student code checks
- JWT generation with required claims
- 403 for inactive/suspended login

**Associated tests:**
- successful registration
- invalid institutional email
- weak password
- successful login
- inactive/suspended login blocked
- register endpoint 201
- login endpoint returns token

**Acceptance criteria:**
- auth routes work end to end
- JWT contains required claims
- security configuration enforces public/authenticated route policy

**Verification commands:**
- `mvn -Dtest=AuthServiceTest,AuthControllerTest test`
- `mvn -q -DskipTests compile`

### Task 4: Catálogos admin

**Files:**
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\usuario\controller\UsuarioAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\usuario\service\UsuarioAdminService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\usuario\dto\request\UsuarioUpdateRequest.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\usuario\dto\response\UsuarioResponse.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\carrera\controller\CarreraAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\carrera\service\CarreraAdminService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\docente\controller\DocenteAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\docente\service\DocenteAdminService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\curso\controller\CursoAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\curso\service\CursoAdminService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\curso_docente\controller\CursoDocenteAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\curso_docente\service\CursoDocenteAdminService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\criterio\controller\CriterioAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\criterio\service\CriterioAdminService.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\admin\CursoAdminServiceTest.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\admin\AdminSecurityControllerTest.java`

**Interfaces:**
- Consumes:
  - repositories for users, careers, teachers, courses, course-teacher, and criteria
  - `AuthenticatedUserService` for admin identity where needed
- Produces:
  - `/api/admin/usuarios`
  - `/api/admin/carreras`
  - `/api/admin/docentes`
  - `/api/admin/cursos`
  - `/api/admin/curso-docente`
  - `/api/admin/criterios`
  - `/api/public/carreras/activas`
  - `/api/public/criterios/activos`

- [ ] **Step 1: Write failing admin service and security tests**

```java
package com.utp.recommends.admin;

import org.junit.jupiter.api.Test;

class CursoAdminServiceTest {

    @Test
    void generalCourseForcesNullCareer() {
        throw new UnsupportedOperationException("CursoAdminService not implemented");
    }
}
```

```java
package com.utp.recommends.admin;

import org.junit.jupiter.api.Test;

class AdminSecurityControllerTest {

    @Test
    void adminRoutesRejectStudentRole() {
        throw new UnsupportedOperationException("Admin controllers not implemented");
    }
}
```

- [ ] **Step 2: Run admin tests to verify they fail**

Run: `mvn -Dtest=CursoAdminServiceTest,AdminSecurityControllerTest test`
Expected: FAIL because admin modules are not implemented.

- [ ] **Step 3: Implement admin services and DTOs**

```java
package com.utp.recommends.admin.curso.service;

import com.utp.recommends.admin.curso.dto.request.CursoCreateRequest;
import com.utp.recommends.admin.curso.dto.response.CursoResponse;

public interface CursoAdminService {
    CursoResponse create(CursoCreateRequest request);
}
```

```java
package com.utp.recommends.admin.curso.service;

import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.enums.TipoCurso;
import org.springframework.http.HttpStatus;

class CursoRules {
    CursoCreateRequest normalizeAndValidate(CursoCreateRequest request) {
        if (request.tipo() == TipoCurso.GENERAL) {
            return new CursoCreateRequest(request.nombre(), request.tipo(), null, request.estado());
        }
        if (request.tipo() == TipoCurso.CARRERA && request.carreraId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "carreraId is required for career courses");
        }
        return request;
    }
}
```

- [ ] **Step 4: Implement admin controllers and public active listings**

```java
package com.utp.recommends.admin.carrera.controller;

import com.utp.recommends.admin.carrera.service.CarreraAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarreraPublicController {

    private final CarreraAdminService service;

    public CarreraPublicController(CarreraAdminService service) {
        this.service = service;
    }

    @GetMapping("/api/public/carreras/activas")
    public Object listarActivas() {
        return service.listActive();
    }
}
```

- [ ] **Step 5: Run admin verification**

Run: `mvn -Dtest=CursoAdminServiceTest,AdminSecurityControllerTest test`
Expected: PASS

- [ ] **Step 6: Run endpoint-level verification**

Run: `mvn -Dtest=AdminSecurityControllerTest test`
Expected: PASS with unauthorized admin route access producing 401 or 403 as appropriate.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/utp/recommends/admin src/test/java/com/utp/recommends/admin
git commit -m "feat: implement admin catalog modules"
```

**Endpoints:**
- `GET /api/admin/usuarios`
- `GET /api/admin/usuarios/{id}`
- `PUT /api/admin/usuarios/{id}`
- `PATCH /api/admin/usuarios/{id}/estado`
- CRUD `/api/admin/carreras`
- CRUD `/api/admin/docentes`
- CRUD `/api/admin/cursos`
- `POST /api/admin/curso-docente`
- `GET /api/admin/curso-docente`
- `GET /api/admin/cursos/{cursoId}/docentes`
- `GET /api/admin/docentes/{docenteId}/cursos`
- `PATCH /api/admin/curso-docente/{id}/estado`
- CRUD `/api/admin/criterios`
- `GET /api/public/carreras/activas`
- `GET /api/public/criterios/activos`

**Business rules to implement in this task:**
- user filtering by role and state
- no password exposure
- soft state transitions for careers, teachers, courses, criteria, and course-teacher
- `GENERAL` course forces null career
- `CARRERA` course requires active career
- manual duplicate validation for `GENERAL` course names
- no duplicate course-teacher relation

**Associated tests:**
- `GENERAL` course nulls career
- `CARRERA` course requires active career
- admin routes reject student role
- public active lists compile and respond

**Acceptance criteria:**
- all admin catalog routes exist
- business rules are implemented in services
- security boundaries are preserved

**Verification commands:**
- `mvn -Dtest=CursoAdminServiceTest,AdminSecurityControllerTest test`
- `mvn -q -DskipTests compile`

### Task 5: Módulos estudiante y moderación

**Files:**
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\resena\controller\ResenaEstudianteController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\resena\service\ResenaEstudianteService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\resena\dto\request\ResenaCreateRequest.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\resena\dto\response\ResenaResponse.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\solicitud\controller\SolicitudEstudianteController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\solicitud\service\SolicitudEstudianteService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\solicitud\dto\request\SolicitudCreateRequest.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\estudiante\solicitud\dto\response\SolicitudResponse.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\moderacion_resena\controller\ModeracionResenaAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\moderacion_resena\service\ModeracionResenaService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\moderacion_solicitud\controller\ModeracionSolicitudAdminController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\moderacion_solicitud\service\SolicitudModeracionService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\admin\moderacion_solicitud\dto\request\AprobarSolicitudRequest.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\estudiante\ResenaServiceTest.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\estudiante\SolicitudModeracionServiceTest.java`

**Interfaces:**
- Consumes:
  - `AuthenticatedUserService`
  - `ResenaRepository`
  - `ResenaCalificacionRepository`
  - `CriterioCalificacionRepository`
  - `SolicitudRepository`
  - `CursoDocenteRepository`
  - `DocenteRepository`
  - `CursoRepository`
- Produces:
  - `POST /api/estudiante/resenas`
  - `GET /api/estudiante/resenas/mis-resenas`
  - `GET /api/estudiante/resenas/mis-resenas/{id}`
  - `POST /api/estudiante/solicitudes`
  - `GET /api/estudiante/solicitudes/mis-solicitudes`
  - `GET /api/estudiante/solicitudes/mis-solicitudes/{id}`
  - `GET /api/admin/moderacion/resenas`
  - `POST /api/admin/moderacion/resenas/{id}/aprobar`
  - `POST /api/admin/moderacion/resenas/{id}/rechazar`
  - `POST /api/admin/moderacion/resenas/{id}/ocultar`
  - `GET /api/admin/moderacion/solicitudes`
  - `POST /api/admin/moderacion/solicitudes/{id}/aprobar`
  - `POST /api/admin/moderacion/solicitudes/{id}/rechazar`

- [ ] **Step 1: Write failing review and request moderation tests**

```java
package com.utp.recommends.estudiante;

import org.junit.jupiter.api.Test;

class ResenaServiceTest {

    @Test
    void approvedExistingReviewReturnsConflict() {
        throw new UnsupportedOperationException("ResenaService not implemented");
    }
}
```

```java
package com.utp.recommends.estudiante;

import org.junit.jupiter.api.Test;

class SolicitudModeracionServiceTest {

    @Test
    void approvalRequiresExactlyOneScorePerActiveCriterion() {
        throw new UnsupportedOperationException("Solicitud moderation not implemented");
    }
}
```

- [ ] **Step 2: Run student/moderation tests to verify they fail**

Run: `mvn -Dtest=ResenaServiceTest,SolicitudModeracionServiceTest test`
Expected: FAIL because review and moderation flows are not implemented.

- [ ] **Step 3: Implement student review flows**

```java
package com.utp.recommends.estudiante.resena.service;

public interface ResenaEstudianteService {
    ResenaResponse crear(ResenaCreateRequest request);
    Page<ResenaResponse> listarMisResenas(Pageable pageable);
    ResenaResponse obtenerMiResena(Long id);
}
```

```java
package com.utp.recommends.estudiante.resena.service;

import com.utp.recommends.domain.enums.EstadoResena;
import java.util.EnumSet;

class ResenaRules {
    EnumSet<EstadoResena> estadosActivos() {
        return EnumSet.of(EstadoResena.PENDIENTE, EstadoResena.APROBADA);
    }
}
```

- [ ] **Step 4: Implement moderation flows with transactional request approval**

```java
package com.utp.recommends.admin.moderacion_solicitud.service;

import org.springframework.transaction.annotation.Transactional;

public interface SolicitudModeracionService {
    @Transactional
    SolicitudResponse aprobar(Long solicitudId, AprobarSolicitudRequest request);
}
```

```java
package com.utp.recommends.admin.moderacion_solicitud.service;

import com.utp.recommends.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

class SolicitudApprovalValidator {
    void validateCriterionScores(List<CriterioPuntajeRequest> scores, List<Long> activeCriteriaIds) {
        if (scores.size() != activeCriteriaIds.size()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Exactly one score per active criterion is required");
        }
    }
}
```

- [ ] **Step 5: Run student/moderation verification**

Run: `mvn -Dtest=ResenaServiceTest,SolicitudModeracionServiceTest test`
Expected: PASS

- [ ] **Step 6: Run regression verification for critical flows**

Run: `mvn -Dtest=ResenaServiceTest test`
Expected: PASS with coverage for pending update, approved conflict, and rejected resend versioning.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/utp/recommends/estudiante src/main/java/com/utp/recommends/admin/moderacion_resena src/main/java/com/utp/recommends/admin/moderacion_solicitud src/test/java/com/utp/recommends/estudiante
git commit -m "feat: implement student review and moderation flows"
```

**Endpoints:**
- `POST /api/estudiante/resenas`
- `GET /api/estudiante/resenas/mis-resenas`
- `GET /api/estudiante/resenas/mis-resenas/{id}`
- `POST /api/estudiante/solicitudes`
- `GET /api/estudiante/solicitudes/mis-solicitudes`
- `GET /api/estudiante/solicitudes/mis-solicitudes/{id}`
- `GET /api/admin/moderacion/resenas?estado=PENDIENTE`
- `POST /api/admin/moderacion/resenas/{id}/aprobar`
- `POST /api/admin/moderacion/resenas/{id}/rechazar`
- `POST /api/admin/moderacion/resenas/{id}/ocultar`
- `GET /api/admin/moderacion/solicitudes?estado=PENDIENTE`
- `POST /api/admin/moderacion/solicitudes/{id}/aprobar`
- `POST /api/admin/moderacion/solicitudes/{id}/rechazar`

**Business rules to implement in this task:**
- review comment minimum 10 characters
- exactly one score per active criterion
- score range 1..5
- update existing pending review instead of creating a duplicate
- 409 when approved review already exists
- create new version when last review is rejected
- reject review moderation without reason
- request approval validates explicit criterion scores, creates dependent entities if needed, creates approved review, creates review scores, links `resenaGenerada`, and commits all-or-nothing

**Associated tests:**
- new review starts pending
- pending review is updated
- approved review conflicts
- rejected review resend creates version
- rejecting review requires reason
- request approval validates criterion set exactly
- request approval completes transaction flow

**Acceptance criteria:**
- student routes resolve student from JWT only
- request approval is transactional
- no moderation route allows invalid state transitions

**Verification commands:**
- `mvn -Dtest=ResenaServiceTest,SolicitudModeracionServiceTest test`
- `mvn -q -DskipTests compile`

### Task 6: Public API, documentación y hardening

**Files:**
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\publicapi\controller\PublicResenaController.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\publicapi\service\PublicResenaService.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\publicapi\dto\response\PublicResenaResponse.java`
- Create: `C:\WebProyecto\src\main\java\com\utp\recommends\publicapi\dto\response\PromedioCriterioResponse.java`
- Create: `C:\WebProyecto\README.md`
- Create: `C:\WebProyecto\OBSERVACIONES_BD.md`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\publicapi\PublicResenaControllerTest.java`

**Interfaces:**
- Consumes:
  - `ResenaRepository`
  - aggregated repository projections for averages
- Produces:
  - `GET /api/public/resenas`
  - `GET /api/public/resenas/curso-docente/{cursoDocenteId}`
  - `GET /api/public/resenas/curso/{cursoId}`
  - `GET /api/public/resenas/promedios/curso-docente/{cursoDocenteId}`
  - `README.md`
  - `OBSERVACIONES_BD.md`

- [ ] **Step 1: Write failing public API tests**

```java
package com.utp.recommends.publicapi;

import org.junit.jupiter.api.Test;

class PublicResenaControllerTest {

    @Test
    void anonymousReviewDoesNotExposeStudentIdentity() {
        throw new UnsupportedOperationException("PublicResenaController not implemented");
    }
}
```

- [ ] **Step 2: Run public API test to verify it fails**

Run: `mvn -Dtest=PublicResenaControllerTest test`
Expected: FAIL because public review controller and DTOs do not exist.

- [ ] **Step 3: Implement public listing service and sanitized DTOs**

```java
package com.utp.recommends.publicapi.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record PublicResenaResponse(
    Long id,
    String curso,
    String docente,
    String comentario,
    boolean esAnonimo,
    String nombreEstudianteVisible,
    OffsetDateTime fechaCreacion,
    List<CriterioPuntajeVisibleResponse> calificaciones
) {
}
```

```java
package com.utp.recommends.publicapi.service;

public interface PublicResenaService {
    Page<PublicResenaResponse> listar(Pageable pageable, PublicResenaFilter filter);
    List<PromedioCriterioResponse> promediosPorCursoDocente(Long cursoDocenteId);
}
```

- [ ] **Step 4: Write documentation artifacts**

```markdown
# UTP+Recommends Backend

## Objetivo
Backend Spring Boot para reseñas y moderación de cursos/docentes UTP.

## Ejecución
1. Configurar variables `SPRING_DATASOURCE_*`
2. Configurar `APP_SECURITY_JWT_SECRET`
3. Ejecutar `mvn spring-boot:run`
```

```markdown
# OBSERVACIONES_BD

## Política
Este archivo documenta inconsistencias detectadas en la BD existente sin modificar el schema oficial.
```

- [ ] **Step 5: Run public API verification**

Run: `mvn -Dtest=PublicResenaControllerTest test`
Expected: PASS

- [ ] **Step 6: Run compile and docs verification**

Run: `mvn -q -DskipTests compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/utp/recommends/publicapi README.md OBSERVACIONES_BD.md src/test/java/com/utp/recommends/publicapi
git commit -m "feat: add public review api and project documentation"
```

**Endpoints:**
- `GET /api/public/resenas`
- `GET /api/public/resenas/curso-docente/{cursoDocenteId}`
- `GET /api/public/resenas/curso/{cursoId}`
- `GET /api/public/resenas/promedios/curso-docente/{cursoDocenteId}`

**Business rules to implement in this task:**
- public outputs show only approved reviews
- anonymous reviews never expose student identity
- averages are computed with repository aggregation
- documentation reflects actual configuration and endpoint grouping

**Associated tests:**
- public list returns only approved reviews
- anonymous public review hides identity

**Acceptance criteria:**
- public endpoints are paginated and sanitized
- documentation files exist and match implemented behavior

**Verification commands:**
- `mvn -Dtest=PublicResenaControllerTest test`
- `mvn -q -DskipTests compile`

### Task 7: Pruebas y validación final

**Files:**
- Modify: `C:\WebProyecto\src\test\java\com\utp\recommends\auth\AuthServiceTest.java`
- Modify: `C:\WebProyecto\src\test\java\com\utp\recommends\auth\AuthControllerTest.java`
- Modify: `C:\WebProyecto\src\test\java\com\utp\recommends\admin\CursoAdminServiceTest.java`
- Modify: `C:\WebProyecto\src\test\java\com\utp\recommends\admin\AdminSecurityControllerTest.java`
- Modify: `C:\WebProyecto\src\test\java\com\utp\recommends\estudiante\ResenaServiceTest.java`
- Modify: `C:\WebProyecto\src\test\java\com\utp\recommends\estudiante\SolicitudModeracionServiceTest.java`
- Modify: `C:\WebProyecto\src\test\java\com\utp\recommends\publicapi\PublicResenaControllerTest.java`
- Create: `C:\WebProyecto\src\test\java\com\utp\recommends\security\SecurityRoleRegressionTest.java`
- Modify: `C:\WebProyecto\README.md`
- Modify: `C:\WebProyecto\OBSERVACIONES_BD.md`

**Interfaces:**
- Consumes:
  - all previous modules
- Produces:
  - final green test suite
  - final build verification
  - final documentation alignment

- [ ] **Step 1: Add any remaining missing tests from the approved minimum set**

```java
package com.utp.recommends.security;

import org.junit.jupiter.api.Test;

class SecurityRoleRegressionTest {

    @Test
    void estudianteRoutesRejectAdminRole() {
        throw new UnsupportedOperationException("final regression test set incomplete");
    }
}
```

- [ ] **Step 2: Run the full test suite and verify failures if coverage gaps remain**

Run: `mvn test`
Expected: FAIL if any approved mandatory scenario is still missing or broken.

- [ ] **Step 3: Fill the last gaps in tests or documentation**

```markdown
## Evidencias sugeridas

- captura de `mvn test`
- captura de `mvn clean install`
- capturas de login, endpoints 401/403 y listados públicos
```

- [ ] **Step 4: Run the full verification suite**

Run: `mvn test`
Expected: `BUILD SUCCESS`

Run: `mvn clean install`
Expected: `BUILD SUCCESS`

- [ ] **Step 5: Validate git state and artifacts**

Run: `git status --short`
Expected: no unexpected modifications beyond intended tracked files

Run: `git log --oneline --decorate -5`
Expected: recent commits for bootstrap, persistence, auth, admin, student/moderation, public API, and final verification

- [ ] **Step 6: Re-read the approved spec and verify coverage**

Run: `Get-Content -Raw 'C:\WebProyecto\docs\superpowers\specs\2026-06-30-utp-recommends-backend-design.md'`
Expected: each section maps to completed implementation and tests with no scope gaps.

- [ ] **Step 7: Commit**

```bash
git add src/test README.md OBSERVACIONES_BD.md
git commit -m "test: complete verification and documentation hardening"
```

**Endpoints:** no new endpoints; validates all existing endpoints

**Business rules to implement in this task:**
- final verification of all approved auth, admin, student, moderation, and public flows
- documentation alignment with implementation

**Associated tests:**
- full suite
- full security route checks
- full persistence constraints
- full public anonymization checks

**Acceptance criteria:**
- `mvn test` passes
- `mvn clean install` passes
- README and observations file are aligned with actual code
- approved spec is fully covered

**Verification commands:**
- `mvn test`
- `mvn clean install`
- `git status --short`
- `git log --oneline --decorate -5`

# UTP+Recommends Backend

Backend Spring Boot para UTP+Recommends. Implementa autenticación JWT, catálogos administrativos, reseñas versionadas, solicitudes, moderación y listados públicos contra el schema MySQL existente.

## Stack

- Java 21
- Maven
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL 8
- JUnit 5, Mockito, MockMvc
- Testcontainers MySQL 8

## Arquitectura

Proyecto monolítico modular bajo `com.utp.recommends` con separación por `auth`, `admin`, `estudiante`, `publicapi`, `security`, `domain`, `repository`, `common` y `config`.

## Requisitos

- Java 21
- Maven 3.9+
- MySQL 8 con el schema ya implantado
- Opcional: Docker para ejecutar las pruebas Testcontainers

## Variables de entorno

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_SECURITY_JWT_SECRET`
- `APP_SECURITY_JWT_EXPIRATION_MINUTES`

## Configuración de BD

La aplicación usa `spring.jpa.hibernate.ddl-auto=validate`. No crea, actualiza ni elimina schema. El archivo `BD_UTPRECOMMENDS.sql` se usa solo como referencia de mapeo y como base para la inicialización de pruebas.

## Ejecución

```bash
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" spring-boot:run
```

## Pruebas

```bash
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" test
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" clean install
```

En este entorno la suite actual termina con `21` pruebas ejecutadas, `0` fallos, `0` errores y `5` `skipped`. Esos `5` casos corresponden a pruebas de integración con Testcontainers MySQL 8 y se omiten automáticamente cuando Docker no está disponible. Para la validación completa de integración se debe ejecutar la suite con Docker Desktop activo y un runtime Docker operativo.

## Endpoints principales

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/admin/usuarios`
- `GET /api/admin/carreras`
- `GET /api/admin/docentes`
- `GET /api/admin/cursos`
- `GET /api/admin/curso-docente`
- `GET /api/admin/criterios`
- `POST /api/estudiante/resenas`
- `GET /api/estudiante/resenas/mis-resenas`
- `POST /api/estudiante/solicitudes`
- `GET /api/estudiante/solicitudes/mis-solicitudes`
- `GET /api/admin/moderacion/resenas`
- `GET /api/admin/moderacion/solicitudes`
- `GET /api/public/resenas`

## Reglas clave

- Los flujos de estudiante resuelven identidad desde JWT, no desde body.
- Las reseñas públicas anónimas no exponen identidad.
- Los cursos `GENERAL` fuerzan `carrera_id = null`.
- Las reseñas activas no se duplican para la misma combinación estudiante + curso-docente.
- La aprobación de solicitudes exige calificaciones explícitas para todos los criterios activos.

## Evidencias sugeridas

- Resultado de `mvn test`
- Resultado de `mvn clean install`
- Evidencia de 401/403 en rutas protegidas
- Evidencia de reseñas públicas anónimas sin identidad visible

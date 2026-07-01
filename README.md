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

### PowerShell Windows

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/utp_recommends?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD=""
$env:APP_SECURITY_JWT_SECRET="clave_larga_segura_de_minimo_32_caracteres"
$env:APP_SECURITY_JWT_EXPIRATION_MINUTES="30"
```

## Configuración de BD

La aplicación usa `spring.jpa.hibernate.ddl-auto=validate` y `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect`. No crea, actualiza ni elimina schema. El archivo `BD_UTPRECOMMENDS.sql` se usa solo como referencia de mapeo y como base para la inicialización de pruebas.

## Ejecución

```bash
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" spring-boot:run
```

En PowerShell, exporta primero las variables anteriores y luego ejecuta el comando. Si tu instancia local usa otra base, usuario o contraseña, reemplázalos antes de arrancar.

## Pruebas

```bash
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" test
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" clean install
```

En este entorno la suite actual termina con `21` pruebas ejecutadas, `0` fallos, `0` errores y `5` `skipped`. Esos `5` casos corresponden a pruebas de integración con Testcontainers MySQL 8 y se omiten automáticamente cuando Docker no está disponible. Para la validación completa de integración se debe ejecutar la suite con Docker Desktop activo y un runtime Docker operativo.

## Troubleshooting MySQL

### Error: `Unable to determine Dialect without JDBC metadata`

Si Spring Boot falla al crear `EntityManagerFactory` con ese error, valida en este orden:

- Que MySQL esté encendido y escuchando en `localhost:3306`.
- Que el nombre de la base exista y sea exactamente `utp_recommends`.
- Que `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` sean los reales del entorno.
- Que la URL JDBC efectiva incluya el host, puerto y base correctos.
- Que el usuario tenga permisos sobre `utp_recommends`.
- Que `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect` siga configurado.

En este proyecto, un caso común es arrancar sin exportar variables y terminar usando un password distinto al real. Si tu instalación local usa `root` con contraseña vacía, en PowerShell debe quedar ` $env:SPRING_DATASOURCE_PASSWORD="" ` antes de ejecutar `spring-boot:run`.

Si después de corregir variables el error de dialect desaparece pero el arranque sigue fallando con `Schema-validation`, entonces la conexión ya fue resuelta y el siguiente bloqueo está en la validación estricta del schema existente contra los mapeos JPA.

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

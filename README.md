# UTP+Recommends Backend

Backend Spring Boot para UTP+Recommends. Este proyecto implementa autenticacion JWT, seguridad con Spring Security, persistencia con JPA/Hibernate, CRUD administrativo, flujos de estudiante, moderacion y listados publicos.

Este `README` esta organizado para servir como documentacion tecnica del avance y como guia de ejecucion y demostracion frente a las rubricas de Avance de Proyecto Final 01 y 02.

## 1. Objetivo del avance

El backend cubre estos objetivos del proyecto:

- levantar correctamente un proyecto Spring Boot funcional;
- exponer endpoints REST organizados por dominio y rol;
- aplicar separacion por capas con inyeccion de dependencias;
- conectar con MySQL mediante JPA/Hibernate;
- implementar operaciones CRUD sobre recursos principales;
- proteger rutas con Spring Security y roles;
- autenticar usuarios mediante JWT;
- incluir pruebas basicas y documentacion ejecutable.

## 2. Tecnologias usadas

- Java 21
- Maven 3.9+
- Spring Boot 3.3.1
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL 8
- JWT (`jjwt`)
- JUnit 5
- Mockito
- MockMvc
- Testcontainers MySQL 8

## 3. Arquitectura y organizacion por capas

El proyecto sigue una estructura modular bajo `com.utp.recommends`:

- `auth`: login, registro, cambio de password y datos del usuario autenticado.
- `admin`: catalogos, dashboard y moderacion.
- `estudiante`: perfil, dashboard, resenas, solicitudes y busqueda de curso-docente.
- `publicapi`: endpoints publicos para el buscador.
- `security`: configuracion de Spring Security, JWT, filtros y acceso al usuario autenticado.
- `domain`: entidades y enums de negocio.
- `repository`: acceso a datos con JPA.
- `common`: excepciones, respuestas y utilidades comunes.
- `config`: bootstrap y configuracion transversal.

Separacion de responsabilidades:

- controladores: reciben requests HTTP y exponen rutas REST;
- servicios: contienen logica de negocio y transacciones;
- repositorios: acceso a persistencia;
- entidades: mapeo JPA/Hibernate al schema MySQL.

## 4. Requisitos previos

- Java 21
- Maven 3.9 o superior
- MySQL 8 ejecutandose en `localhost:3306`
- Base de datos `utp_recommends`
- Schema ya creado en MySQL
- Opcional: Docker Desktop para ejecutar pruebas con Testcontainers

## 5. Configuracion de base de datos

La aplicacion usa:

- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect`
- `spring.jpa.open-in-view=false`

Implicancias:

- Spring valida el schema existente;
- no crea ni altera tablas automaticamente;
- el schema debe existir antes de levantar la app;
- las relaciones y transacciones deben resolverse desde la capa de servicio.

Archivo base del schema:

- `BD_UTPRECOMMENDS.sql`

## 6. Variables de entorno

Variables soportadas por el backend:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_SECURITY_JWT_SECRET`
- `APP_SECURITY_JWT_EXPIRATION_MINUTES`
- `APP_BOOTSTRAP_ADMIN_ENABLED`
- `APP_BOOTSTRAP_ADMIN_EMAIL`
- `APP_BOOTSTRAP_ADMIN_PASSWORD`
- `APP_BOOTSTRAP_ADMIN_NOMBRES`
- `APP_BOOTSTRAP_ADMIN_APELLIDOS`
- `APP_BOOTSTRAP_DEV_SEED_ENABLED`

Ejemplo en PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/utp_recommends?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD=""
$env:APP_SECURITY_JWT_SECRET="clave_larga_segura_de_minimo_32_caracteres"
$env:APP_SECURITY_JWT_EXPIRATION_MINUTES="30"
$env:APP_BOOTSTRAP_ADMIN_ENABLED="true"
$env:APP_BOOTSTRAP_ADMIN_EMAIL="admin@utp.edu.pe"
$env:APP_BOOTSTRAP_ADMIN_PASSWORD="Admin123!"
$env:APP_BOOTSTRAP_ADMIN_NOMBRES="Administrador"
$env:APP_BOOTSTRAP_ADMIN_APELLIDOS="Sistema"
$env:APP_BOOTSTRAP_DEV_SEED_ENABLED="true"
```

## 7. Como ejecutar el backend

### 7.1. Paso 1: crear o verificar la base de datos

Ejecuta el script:

- `BD_UTPRECOMMENDS.sql`

Debe existir la base:

- `utp_recommends`

### 7.2. Paso 2: exportar variables

En PowerShell, exporta las variables del bloque anterior. Ajusta usuario y password si tu instancia local no usa `root` con password vacio.

### 7.3. Paso 3: levantar la aplicacion

```powershell
mvn spring-boot:run
```

Si tu entorno usa repo local Maven especifico:

```powershell
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" spring-boot:run
```

### 7.4. Verificacion esperada

La aplicacion debe:

- compilar sin errores;
- conectarse a MySQL;
- validar el schema;
- quedar accesible en `http://localhost:8080`.

## 8. Bootstrap admin y datos semilla

### 8.1. Admin local seguro

Si activas:

- `APP_BOOTSTRAP_ADMIN_ENABLED=true`

el backend crea o actualiza el admin configurado con hash BCrypt.

Credenciales de ejemplo:

- correo: `admin@utp.edu.pe`
- password: `Admin123!`

### 8.2. Datos semilla de desarrollo

Si activas:

- `APP_BOOTSTRAP_DEV_SEED_ENABLED=true`

la aplicacion carga datos de desarrollo para poblar el frontend.

La semilla:

- es opcional;
- es idempotente;
- crea carreras, criterios, docentes, cursos y relaciones curso-docente;
- crea estudiantes de prueba;
- crea resenas aprobadas, pendientes y rechazadas;
- crea una solicitud pendiente;
- garantiza un admin utilizable para pruebas locales.

## 9. Seguridad y autenticacion

El backend implementa seguridad con Spring Security y JWT.

Rutas publicas:

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/public/**`

Rutas autenticadas:

- `GET /api/auth/me`
- `PUT /api/auth/change-password`
- `GET /api/admin/**` requiere rol `ADMIN`
- `GET /api/estudiante/**` requiere rol `ESTUDIANTE`

Flujo JWT:

1. el usuario se autentica con `POST /api/auth/login`;
2. el backend devuelve un token JWT;
3. el cliente envia `Authorization: Bearer <token>`;
4. Spring Security valida token, rol y estado del usuario;
5. la identidad autenticada se usa desde `AuthenticatedUserService`.

## 10. Persistencia, transacciones y consistencia

La persistencia se implementa con:

- entidades JPA en `domain/entity`;
- repositorios Spring Data JPA en `repository`;
- servicios con transacciones para operaciones criticas.

Evidencias tecnicas presentes en el proyecto:

- relaciones JPA entre usuario, estudiante, carrera, curso, docente, resena y solicitud;
- consultas derivadas y JPQL en repositorios;
- validacion de restricciones de negocio antes de persistir;
- uso de `@Transactional` en operaciones de escritura y lectura sensible;
- control de consistencia para evitar resenas activas duplicadas por estudiante y curso-docente.

## 11. CRUD y flujos implementados

CRUD administrativos implementados:

- usuarios
- carreras
- cursos
- docentes
- criterios
- curso-docente

Flujos de estudiante:

- ver perfil
- actualizar perfil
- consultar dashboard
- listar opciones activas de curso-docente
- crear resena
- listar mis resenas
- ver detalle de mi resena
- crear solicitud
- listar mis solicitudes
- ver detalle de mi solicitud

Flujos de moderacion:

- aprobar, rechazar y ocultar resenas
- aprobar y rechazar solicitudes

## 12. Rutas del backend

### 12.1. Autenticacion

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `PUT /api/auth/change-password`

### 12.2. Publicas

- `GET /api/public/carreras/activas`
- `GET /api/public/criterios/activos`
- `GET /api/public/resenas`
- `GET /api/public/resenas/curso-docente/{cursoDocenteId}`
- `GET /api/public/resenas/curso/{cursoId}`
- `GET /api/public/resenas/promedios/curso-docente/{cursoDocenteId}`

### 12.3. Estudiante

- `GET /api/estudiante/dashboard`
- `GET /api/estudiante/perfil`
- `PUT /api/estudiante/perfil`
- `GET /api/estudiante/curso-docente/activos`
- `POST /api/estudiante/resenas`
- `GET /api/estudiante/resenas/mis-resenas`
- `GET /api/estudiante/resenas/mis-resenas/{id}`
- `POST /api/estudiante/solicitudes`
- `GET /api/estudiante/solicitudes/mis-solicitudes`
- `GET /api/estudiante/solicitudes/mis-solicitudes/{id}`

### 12.4. Admin

- `GET /api/admin/dashboard`
- `POST /api/admin/usuarios`
- `GET /api/admin/usuarios`
- `GET /api/admin/usuarios/{id}`
- `PUT /api/admin/usuarios/{id}`
- `PATCH /api/admin/usuarios/{id}/estado`
- `POST /api/admin/carreras`
- `GET /api/admin/carreras`
- `PUT /api/admin/carreras/{id}`
- `DELETE /api/admin/carreras/{id}`
- `POST /api/admin/cursos`
- `GET /api/admin/cursos`
- `PUT /api/admin/cursos/{id}`
- `DELETE /api/admin/cursos/{id}`
- `POST /api/admin/docentes`
- `GET /api/admin/docentes`
- `PUT /api/admin/docentes/{id}`
- `DELETE /api/admin/docentes/{id}`
- `POST /api/admin/criterios`
- `GET /api/admin/criterios`
- `PUT /api/admin/criterios/{id}`
- `PATCH /api/admin/criterios/{id}/estado`
- `POST /api/admin/curso-docente`
- `GET /api/admin/curso-docente`
- `GET /api/admin/cursos/{cursoId}/docentes`
- `GET /api/admin/docentes/{docenteId}/cursos`
- `PATCH /api/admin/curso-docente/{id}/estado`
- `GET /api/admin/moderacion/resenas`
- `POST /api/admin/moderacion/resenas/{id}/aprobar`
- `POST /api/admin/moderacion/resenas/{id}/rechazar`
- `POST /api/admin/moderacion/resenas/{id}/ocultar`
- `GET /api/admin/moderacion/solicitudes`
- `POST /api/admin/moderacion/solicitudes/{id}/aprobar`
- `POST /api/admin/moderacion/solicitudes/{id}/rechazar`

## 13. Pruebas

Comandos:

```powershell
mvn test
mvn clean install
```

Si usas repo local Maven:

```powershell
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" test
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" clean install
```

Alcance esperado:

- validacion de endpoints;
- validacion de autenticacion y seguridad;
- validacion de reglas de negocio;
- soporte de pruebas de integracion con Testcontainers cuando Docker esta disponible.

## 14. Evidencias sugeridas para sustento

Para alinearte con las rubricas, conviene mostrar:

- proyecto ejecutando en local sin errores;
- evidencia de conexion exitosa a MySQL;
- evidencia de login JWT y uso del token;
- pruebas ejecutadas con `mvn test`;
- ejemplos de rutas publicas, de estudiante y de admin;
- evidencia de 401 o 403 en rutas protegidas sin token o con rol incorrecto;
- evidencia de CRUD admin funcionando;
- evidencia de persistencia real en base de datos;
- evidencia de moderacion de resenas y solicitudes;
- captura o export de respuestas JSON representativas.

## 15. Troubleshooting

### Error: `Unable to determine Dialect without JDBC metadata`

Revisa:

- que MySQL este encendido;
- que la base `utp_recommends` exista;
- que usuario y password sean correctos;
- que la URL JDBC apunte a host, puerto y base correctos;
- que el usuario tenga permisos;
- que `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect` siga configurado.

### Error de schema validation

Si desaparece el error de conexion pero aparece `Schema-validation`, significa que:

- Spring ya pudo conectarse;
- el problema ahora es diferencia entre el schema real y los mapeos JPA.

### Error 401 o 403

Revisa:

- si el token JWT fue generado correctamente;
- si se envia `Authorization: Bearer <token>`;
- si el usuario tiene el rol adecuado;
- si el usuario esta `ACTIVO`.

## 16. Estado del avance frente a rubricas

Este backend documenta evidencia para:

- APF 01:
  - estructura Spring Boot y configuracion del entorno;
  - controladores y endpoints REST;
  - inyeccion de dependencias y organizacion por capas;
  - pruebas basicas;
  - README tecnico con ejecucion y evidencias.
- APF 02:
  - persistencia con JPA/Hibernate;
  - operaciones CRUD;
  - consultas, transacciones y consistencia;
  - seguridad con Spring Security;
  - autenticacion JWT y documentacion tecnica.

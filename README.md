# UTP+Recommends

Sistema web para recomendaciones academicas de cursos y docentes de la UTP. El proyecto integra un frontend en Angular y un backend en Spring Boot con autenticacion JWT, persistencia en MySQL, CRUD administrativo, flujos para estudiantes, moderacion y listados publicos.

Este `README` esta preparado como documentacion tecnica para sustentar el `Avance de Proyecto Final 03` y el `Proyecto Final`, con enfoque en arquitectura, integracion, seguridad, despliegue, alcance funcional y evidencias de demostracion.

## 1. Enlaces de despliegue

- Frontend desplegado: [https://utp-recommends-angular-ts-8wrn.vercel.app/](https://utp-recommends-angular-ts-8wrn.vercel.app/)
- Backend desplegado: [https://utp-recommends-backend.onrender.com](https://utp-recommends-backend.onrender.com)

## 2. Objetivo del sistema

UTP+Recommends busca centralizar la experiencia de los estudiantes al momento de:

- consultar referencias sobre cursos y docentes;
- publicar resenas academicas con criterios de calificacion;
- solicitar el registro de cursos o docentes que aun no existen en el sistema;
- permitir moderacion administrativa antes de publicar contenido visible;
- ofrecer una base consistente para busqueda, comparacion y consulta academica.

## 3. Cobertura frente a las rubricas

### 3.1. Avance de Proyecto Final 03

Este proyecto documenta evidencia para:

- estructura del front-end en Angular;
- navegacion y rutas por rol;
- formularios reactivos con validaciones;
- consumo de API REST e integracion con backend;
- autenticacion, autorizacion y proteccion de rutas;
- documentacion tecnica del avance con evidencia funcional.

### 3.2. Proyecto Final

Este proyecto documenta evidencia para:

- arquitectura general e integracion completa del sistema;
- cumplimiento del alcance funcional principal;
- gestion de datos, seguridad y control de acceso;
- experiencia de usuario, validaciones y manejo de errores;
- despliegue real, documentacion tecnica y sustento de demostracion.

## 4. Arquitectura general

La solucion esta dividida en dos capas principales:

- `Frontend Angular`: interfaz de usuario, formularios, rutas, guards, interceptor JWT, consumo de API y experiencia de usuario.
- `Backend Spring Boot`: autenticacion, autorizacion, logica de negocio, persistencia, moderacion, CRUD administrativos y endpoints REST.

Arquitectura logica:

1. el usuario interactua con Angular desde el navegador;
2. Angular consume el backend mediante HTTP/JSON;
3. Spring Security valida autenticacion y roles mediante JWT;
4. los servicios de Spring Boot aplican reglas de negocio;
5. JPA/Hibernate persiste y consulta datos en MySQL.

## 5. Tecnologias usadas

### 5.1. Frontend

- Angular
- TypeScript
- Angular Material
- SCSS
- Reactive Forms
- Router Guards
- HTTP Interceptor

### 5.2. Backend

- Java 21
- Maven 3.9+
- Spring Boot 3.3.1
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- MySQL 8

### 5.3. Pruebas y soporte

- JUnit 5
- Mockito
- MockMvc
- Testcontainers MySQL 8
- Docker
- Render
- Vercel

## 6. Modulos del sistema

### 6.1. Modulos para estudiante

- registro e inicio de sesion;
- visualizacion de perfil;
- actualizacion de perfil;
- consulta de dashboard;
- busqueda de curso-docente activo;
- creacion de resenas;
- consulta de mis resenas;
- creacion de solicitudes de curso/docente;
- consulta de mis solicitudes;
- acceso a listados publicos y promedios.

### 6.2. Modulos para admin

- gestion de usuarios;
- gestion de carreras;
- gestion de cursos;
- gestion de docentes;
- gestion de criterios de calificacion;
- gestion de relaciones curso-docente;
- dashboard administrativo;
- moderacion de resenas;
- moderacion de solicitudes.

### 6.3. Modulos publicos

- listado general de resenas aprobadas;
- listado por curso;
- listado por curso-docente;
- consulta de carreras activas;
- consulta de criterios activos;
- consulta de promedios por curso-docente.

## 7. Estructura de frontend e integracion con APF 03

La documentacion funcional del frontend esta respaldada en:

- `03-logica-negocio-frontend.md`

Aspectos cubiertos para la rubrica de avance 3:

- organizacion del frontend por rutas y pantallas;
- separacion de vistas para autenticacion, estudiante y admin;
- uso de formularios reactivos;
- validaciones de correo institucional, nombres, password y formularios de resena/solicitud;
- proteccion de rutas mediante `AuthGuard` y `RoleGuard`;
- manejo de sesion con token JWT;
- interceptor HTTP para enviar `Authorization: Bearer <token>`;
- consumo de servicios REST del backend;
- mensajes de error y retroalimentacion al usuario.

Rutas funcionales documentadas:

- `/auth/login`
- `/auth/registro`
- `/estudiante/inicio`
- `/estudiante/resenas/nueva`
- `/estudiante/resenas/mis-resenas`
- `/estudiante/solicitudes/nueva`
- `/estudiante/listado-general`
- `/estudiante/listado-por-curso`
- `/admin/docentes`
- `/admin/cursos`
- `/admin/usuarios`
- `/admin/moderacion/resenas`
- `/admin/moderacion/solicitudes`
- `/admin/criterios`

## 8. Estructura del backend

El backend sigue una estructura modular bajo `com.utp.recommends`:

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

La logica tecnica del backend esta respaldada en:

- `02-logica-negocio-backend.md`

## 9. Modelo de datos y consistencia

La documentacion del modelo de datos y reglas estructurales esta respaldada en:

- `01-logica-negocio-bd.md`
- `OBSERVACIONES_BD.md`
- `BD_UTPRECOMMENDS.sql`

La aplicacion usa:

- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect`
- `spring.jpa.open-in-view=false`

Implicancias:

- Spring valida el schema existente;
- no crea ni altera tablas automaticamente;
- el schema debe existir antes de levantar la app;
- las reglas complejas viven en la capa de servicio;
- la BD garantiza relaciones, claves y restricciones estructurales.

Entidades de negocio principales:

- usuario
- estudiante
- carrera
- curso
- docente
- curso_docente
- criterio_calificacion
- resena
- resena_calificacion
- solicitud

## 10. Seguridad, autenticacion y control de acceso

El sistema implementa autenticacion JWT y autorizacion por roles en frontend y backend.

### 10.1. Flujo de autenticacion

1. el usuario se registra o inicia sesion;
2. el backend valida credenciales y estado del usuario;
3. el backend devuelve un token JWT;
4. el frontend guarda la sesion y adjunta el token en cada request protegida;
5. el backend valida token, rol y permisos antes de acceder al recurso;
6. las rutas del frontend tambien se protegen con guards.

### 10.2. Roles

- `ADMIN`
- `ESTUDIANTE`

### 10.3. Rutas publicas del backend

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/public/**`

### 10.4. Rutas protegidas del backend

- `GET /api/auth/me`
- `PUT /api/auth/change-password`
- `GET /api/admin/**` requiere rol `ADMIN`
- `GET /api/estudiante/**` requiere rol `ESTUDIANTE`

### 10.5. Evidencia esperada de seguridad

Durante la sustentacion conviene mostrar:

- login exitoso con token JWT;
- acceso permitido a rutas del rol correcto;
- rechazo `401` sin token;
- rechazo `403` con rol incorrecto;
- proteccion de vistas del frontend para usuario no autenticado;
- ocultamiento de opciones segun rol como mejora UX, sin reemplazar la seguridad real.

## 11. Integracion frontend-backend

La integracion entre capas cubre:

- autenticacion desde Angular contra `POST /api/auth/login`;
- registro de estudiante contra `POST /api/auth/register`;
- consumo de endpoints publicos para listados y filtros;
- consumo de endpoints de estudiante para resenas, perfil y solicitudes;
- consumo de endpoints administrativos para CRUD y moderacion;
- sincronizacion del flujo visual con las reglas de negocio del backend.

Flujos integrados principales:

### 11.1. Flujo de estudiante

1. el estudiante inicia sesion;
2. accede a su dashboard;
3. consulta opciones activas de curso-docente;
4. registra una resena con criterios de calificacion;
5. consulta el estado de sus resenas;
6. si no encuentra curso o docente, crea una solicitud;
7. consulta el estado de sus solicitudes.

### 11.2. Flujo de admin

1. el admin inicia sesion;
2. accede al dashboard administrativo;
3. administra catalogos y relaciones academicas;
4. revisa la cola de moderacion;
5. aprueba, rechaza u oculta resenas;
6. aprueba o rechaza solicitudes.

## 12. CRUD y flujos implementados

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

## 13. Rutas del backend

### 13.1. Autenticacion

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `PUT /api/auth/change-password`

### 13.2. Publicas

- `GET /api/public/carreras/activas`
- `GET /api/public/criterios/activos`
- `GET /api/public/resenas`
- `GET /api/public/resenas/curso-docente/{cursoDocenteId}`
- `GET /api/public/resenas/curso/{cursoId}`
- `GET /api/public/resenas/promedios/curso-docente/{cursoDocenteId}`

### 13.3. Estudiante

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

### 13.4. Admin

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

## 14. Experiencia de usuario, validaciones y manejo de errores

Aspectos considerados para la rubrica del avance 3 y del proyecto final:

- formularios reactivos con validacion de campos obligatorios;
- validacion de correo institucional con formato UTP;
- validacion de password con reglas minimas;
- validacion dinamica en solicitudes segun tipo;
- carga dinamica de criterios de calificacion;
- mensajes de retroalimentacion en errores de formulario;
- manejo visual de estados como `PENDIENTE`, `APROBADA` y `RECHAZADA`;
- respuesta adecuada frente a `401`, `403`, validaciones y conflictos de negocio;
- coherencia entre lo que el frontend permite y lo que el backend valida.

## 15. Requisitos previos para ejecucion local del backend

- Java 21
- Maven 3.9 o superior
- MySQL 8 ejecutandose en `localhost:3306`
- Base de datos `utp_recommends`
- schema ya creado en MySQL
- opcional: Docker Desktop para ejecutar pruebas con Testcontainers

## 16. Variables de entorno

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

## 17. Como ejecutar el backend en local

### 17.1. Paso 1: crear o verificar la base de datos

Ejecuta el script:

- `BD_UTPRECOMMENDS.sql`

Debe existir la base:

- `utp_recommends`

### 17.2. Paso 2: exportar variables

En PowerShell, exporta las variables del bloque anterior. Ajusta usuario y password si tu instancia local no usa `root` con password vacio.

### 17.3. Paso 3: levantar la aplicacion

```powershell
mvn spring-boot:run
```

Si tu entorno usa repo local Maven especifico:

```powershell
mvn "-Dmaven.repo.local=C:\WebProyecto\.m2repo" spring-boot:run
```

### 17.4. Verificacion esperada

La aplicacion debe:

- compilar sin errores;
- conectarse a MySQL;
- validar el schema;
- quedar accesible en `http://localhost:8080`.

## 18. Bootstrap admin y datos semilla

### 18.1. Admin local seguro

Si activas:

- `APP_BOOTSTRAP_ADMIN_ENABLED=true`

el backend crea o actualiza el admin configurado con hash BCrypt.

Credenciales de ejemplo:

- correo: `admin@utp.edu.pe`
- password: `Admin123!`

### 18.2. Datos semilla de desarrollo

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

## 19. Pruebas

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

## 20. Evidencias recomendadas para la sustentacion

Para obtener una sustentacion mas solida frente a ambas rubricas, conviene presentar:

- pantalla inicial del frontend desplegado;
- flujo completo de login de estudiante;
- flujo completo de login de admin;
- evidencia de `AuthGuard`, `RoleGuard` o restriccion de vistas;
- formulario de registro con validaciones visibles;
- formulario de resena con criterios cargados desde API;
- consumo exitoso de endpoints publicos y privados;
- ejemplo de respuesta JSON del login y de un endpoint protegido;
- evidencia de `401` sin token;
- evidencia de `403` con rol incorrecto;
- CRUD administrativo funcionando;
- moderacion de resenas y solicitudes;
- persistencia real en base de datos;
- despliegue funcional en Vercel y Render;
- explicacion de arquitectura, decisiones tecnicas y alcance implementado.

## 21. Troubleshooting

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

## 22. Estado actual del proyecto frente a las rubricas

### 22.1. Avance 3

El proyecto ya documenta:

- estructura funcional del frontend;
- rutas y navegacion por rol;
- validaciones de formularios;
- integracion con backend REST;
- autenticacion y autorizacion en frontend y backend;
- documentacion tecnica del flujo general.

### 22.2. Proyecto final

El proyecto ya documenta:

- arquitectura integral del sistema;
- integracion entre Angular, Spring Boot y MySQL;
- alcance funcional principal por modulos;
- control de acceso y seguridad;
- despliegue real del frontend y backend;
- guia de demostracion y evidencias sugeridas.

## 23. Documentos de apoyo del repositorio

Para la sustentacion tecnica, este repositorio incluye documentos complementarios:

- `01-logica-negocio-bd.md`
- `02-logica-negocio-backend.md`
- `03-logica-negocio-frontend.md`
- `OBSERVACIONES_BD.md`
- `BD_UTPRECOMMENDS.sql`
- `Dockerfile`
- `docker-compose.yml`
- `render.yaml`

# Render Deployment Design

**Goal**
Preparar el backend Spring Boot para despliegue en Render conectado a MySQL en Railway, manteniendo desarrollo local simple y sin hardcodear configuración de producción.

**Approach**
Se mantendrá `application.yml` como fuente base, pero con valores resueltos desde variables de entorno. El puerto del servidor se hará dinámico para Render mediante `PORT`. La configuración CORS dejará de estar fija a localhost y pasará a leer una lista configurable de orígenes, conservando defaults de desarrollo local. El empaquetado de despliegue se resolverá con un `Dockerfile` multi-stage y una definición `render.yaml` para despliegue reproducible.

**Key Decisions**
- `server.port` usará `${PORT:8081}` para soportar Render sin romper desarrollo local.
- CORS se configurará con `app.cors.allowed-origins`, usando defaults `http://localhost:4200,http://127.0.0.1:4200`.
- Se agregará una clase de propiedades para CORS en vez de hardcodear valores en la configuración.
- Se incluirá `Dockerfile` multi-stage para Java 21 con Maven.
- Se agregará `render.yaml` con variables requeridas para conectar a Railway.
- Se actualizará `.env.example` para reflejar el nuevo origen configurable y el caso Railway.

**Testing**
- Se agregará una prueba de configuración para verificar:
  - defaults locales cuando no hay override;
  - override por propiedades para orígenes productivos.

**Out of Scope**
- No se migrará el esquema a Flyway/Liquibase en esta iteración.
- No se agregará Actuator ni health endpoint dedicado; se aprovechará un endpoint público existente.

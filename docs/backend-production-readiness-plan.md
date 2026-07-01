# Backend Production Readiness Plan

Fecha: 2026-07-01
Repositorio: `C:\WebProyecto`

## Objetivo

Cerrar las brechas reales del backend Spring Boot para que los flujos principales de UTP+Recommends queden soportados por contratos seguros, validados, documentados y probados, sin degradar la seguridad ni romper compatibilidad con el frontend Angular existente.

## Brechas confirmadas

### 1. Bootstrap admin inseguro o no usable

- `BD_UTPRECOMMENDS.sql` inserta `admin@utp.edu.pe` con hash placeholder.
- No existe mecanismo documentado y reproducible para crear o reponer un admin local usable.
- Impacto funcional:
  - bloquea login admin E2E real;
  - bloquea validación de `/api/auth/me` con rol `ADMIN`;
  - bloquea prueba real de endpoints admin con credencial operativa.
- Riesgo:
  - alto funcional y alto de seguridad si se resuelve con credenciales hardcodeadas.

### 2. Falta endpoint productivo para selector `curso-docente` activo

- Solo existen endpoints admin para listar relaciones `curso-docente`.
- El frontend estudiante no tiene contrato real para poblar nueva reseña.
- Impacto funcional:
  - bloquea creación completa de reseña desde UI.
- Riesgo:
  - medio; requiere exponer datos suficientes sin filtrar información sensible.

### 3. Falta perfil estudiante editable

- Solo existe `GET /api/auth/me` en modo lectura.
- No existe endpoint para edición controlada de nombres y apellidos del estudiante autenticado.
- Impacto funcional:
  - `/estudiante/perfil` queda limitado a lectura.
- Riesgo:
  - medio; requiere usar identidad del JWT y bloquear campos no permitidos.

### 4. Falta cambio de contraseña autenticado

- No existe endpoint para rotación de contraseña con validación de contraseña actual.
- Impacto funcional:
  - no hay flujo seguro de mantenimiento de cuenta.
- Riesgo:
  - alto; toca autenticación, BCrypt y validación de política de contraseña.

### 5. DTOs de moderación insuficientes

- `ModeracionResenaResponse` y `ModeracionSolicitudResponse` hoy devuelven datos mínimos.
- La UI admin no puede moderar con contexto real suficiente.
- Impacto funcional:
  - moderación administrativa incompleta y dependiente de “safe treatments”.
- Riesgo:
  - medio; requiere enriquecer respuestas sin exponer identidad fuera del ámbito admin.

### 6. Aprobación de solicitudes incompleta por tipo

- `SolicitudModeracionServiceImpl` falla explícitamente para `CURSO_NUEVO` y `DOCENTE_NUEVO`.
- El flujo aprobable real hoy es solo `AMBOS`.
- Impacto funcional:
  - inconsistencia entre contrato HTTP, lógica de negocio documentada y comportamiento real.
- Riesgo:
  - alto; requiere definir y documentar comportamiento productivo sin dejar estados ambiguos.

### 7. Falta dashboard agregado básico

- No existen endpoints resumen para estudiante ni admin.
- Impacto funcional:
  - dashboards solo pueden mostrar modo seguro/degradado.
- Riesgo:
  - medio-bajo si se implementa con consultas simples.

### 8. Documentación y manejo de errores incompletos

- README no documenta bootstrap admin seguro.
- `frontend-backend-gaps.md` y `backend-endpoints.md` requieren actualización al cerrar brechas.
- `GlobalExceptionHandler` devuelve mensaje genérico para `DataIntegrityViolationException`.
- Impacto funcional:
  - soporte y operación local menos reproducibles;
  - mensajes de negocio inconsistentes en algunos casos.
- Riesgo:
  - medio.

## Endpoints a crear o modificar

### Auth

- Crear `PUT /api/auth/change-password`
  - autenticado;
  - permitido para `ADMIN` y `ESTUDIANTE`;
  - body con contraseña actual y nueva;
  - valida BCrypt, política fuerte y que no sea igual a la actual.

### Estudiante

- Crear `GET /api/estudiante/curso-docente/activos`
  - protegido para `ESTUDIANTE`;
  - soporta filtros opcionales: `texto`, `carreraId`, `cursoId`, `docenteId`;
  - devuelve solo relaciones activas.
- Crear `GET /api/estudiante/perfil`
  - puede reutilizar la información del usuario autenticado con datos de estudiante/carrera.
- Crear `PUT /api/estudiante/perfil`
  - solo actualiza `nombres` y `apellidos`.
- Crear `GET /api/estudiante/dashboard`
  - resumen agregado básico del estudiante autenticado.

### Admin

- Modificar `GET /api/admin/moderacion/resenas`
  - enriquecer DTO sin cambiar restricción a `ADMIN`.
- Modificar `GET /api/admin/moderacion/solicitudes`
  - enriquecer DTO con estudiante y datos sugeridos.
- Modificar `POST /api/admin/moderacion/solicitudes/{id}/aprobar`
  - soportar claramente `CURSO_NUEVO`, `DOCENTE_NUEVO` y `AMBOS`.
- Crear `GET /api/admin/dashboard`
  - resumen agregado básico de administración.

## DTOs involucrados

### Nuevos DTOs backend

- `auth.dto.request.ChangePasswordRequest`
- `estudiante.perfil.dto.request.StudentProfileUpdateRequest`
- `estudiante.perfil.dto.response.StudentProfileResponse`
- `estudiante.cursodocente.dto.response.ActiveCourseTeacherOptionResponse`
- `estudiante.dashboard.dto.response.StudentDashboardResponse`
- `admin.dashboard.dto.response.AdminDashboardResponse`
- DTOs auxiliares para últimas reseñas/solicitudes y tarjetas de resumen si el diseño actual lo requiere.

### DTOs a ampliar

- `admin.moderacion_resena.dto.response.ModeracionResenaResponse`
- `admin.moderacion_solicitud.dto.response.ModeracionSolicitudResponse`
- `admin.moderacion_solicitud.dto.request.AprobarSolicitudRequest` solo si hace falta completar metadatos de aprobación sin romper compatibilidad.

## Reglas de seguridad

- Mantener `BCryptPasswordEncoder`.
- No hardcodear credenciales reales en código productivo.
- Resolver admin inicial mediante mecanismo documentado y reproducible:
  - preferencia: script o utilitario local documentado para generar hash BCrypt;
  - seed SQL solo con instrucción explícita para reemplazar hash o variante local segura.
- Mantener `/api/admin/**` solo para `ADMIN`.
- Mantener `/api/estudiante/**` solo para `ESTUDIANTE`.
- `PUT /api/auth/change-password` debe usar usuario desde JWT y no exponer hash en ninguna respuesta.
- `PUT /api/estudiante/perfil` debe usar usuario autenticado desde JWT, nunca `usuarioId` en body.
- Moderación enriquecida solo visible para `ADMIN`.
- Ajustar CORS para que siga funcionando en local sin quedar abierto indiscriminadamente a producción.

## Validaciones nuevas o reforzadas

- Política fuerte de contraseña:
  - mínimo 8;
  - mayúscula;
  - minúscula;
  - número;
  - especial;
  - sin espacios.
- Validación de nombres/apellidos editable:
  - letras, espacios, tildes y ñ;
  - longitud 2 a 100.
- Validación de comentario en aprobación de solicitudes:
  - respetar reglas mínimas de reseña aprobada automáticamente.
- Validación de respuestas/moderación:
  - no exponer `password_hash`;
  - no exponer datos fuera del rol admin.

## Pruebas necesarias

### Backend unitarias/integración

- Auth:
  - cambio de contraseña exitoso;
  - contraseña actual incorrecta;
  - nueva contraseña inválida;
  - nueva contraseña igual a la actual.
- Estudiante perfil:
  - update exitoso;
  - validación de nombres/apellidos;
  - `401` sin token;
  - `403` para rol admin si intenta endpoint estudiante.
- `curso-docente` activo:
  - lista básica;
  - filtros;
  - solo activos;
  - seguridad.
- Moderación:
  - responses enriquecidas;
  - visibilidad admin;
  - rechazo con motivo.
- Solicitudes:
  - aprobación `CURSO_NUEVO`;
  - aprobación `DOCENTE_NUEVO`;
  - aprobación `AMBOS`;
  - rechazo de estados no pendientes;
  - validación de criterios activos.
- Dashboards:
  - resumen estudiante;
  - resumen admin;
  - roles `401/403`.
- Bootstrap admin:
  - prueba/documentación verificable de login admin usable en local.

### Frontend mínimos

- `npm run build`
- `npm test -- --watch=false` si la suite sigue estable
- compilación de servicios y modelos actualizados contra DTOs reales.

## Riesgo por cambio

- Alto:
  - cambio de contraseña;
  - bootstrap admin;
  - aprobación de solicitudes por tipo.
- Medio:
  - perfil editable;
  - selector `curso-docente`;
  - moderación enriquecida;
  - dashboards.
- Bajo:
  - documentación;
  - ajuste de servicios frontend si backend ya está estable.

## Orden de implementación

1. Asegurar bootstrap admin local seguro y documentado.
2. Agregar cobertura de tests base para auth/perfil/password/solicitudes/moderación.
3. Implementar endpoint `curso-docente` activo para estudiante.
4. Implementar perfil estudiante editable.
5. Implementar cambio de contraseña autenticado.
6. Enriquecer DTOs de moderación de reseñas y solicitudes.
7. Completar aprobación productiva de solicitudes por tipo.
8. Implementar dashboards agregados mínimos.
9. Actualizar documentación backend y documento de endpoints compartido con frontend.
10. Ajustar integración mínima del frontend para consumir los contratos reales.
11. Ejecutar `mvn test`, `npm run build`, `npm test -- --watch=false` y validación E2E manual.

## Decisiones pendientes solo si aparecen durante implementación

- Si `CURSO_NUEVO` o `DOCENTE_NUEVO` requieren datos adicionales no representables sin tocar esquema, se documentará la limitación exacta antes de considerar un cambio de BD.
- Si el dashboard exige consultas pesadas o cambios de schema para ser correcto, se dejará documentado como mejora futura y se mantendrá un contrato mínimo seguro.

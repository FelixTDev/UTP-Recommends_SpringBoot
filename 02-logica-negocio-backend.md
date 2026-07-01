# UTP+Recommends — Lógica de negocio del backend (Spring Boot)

Este documento describe el comportamiento esperado del backend: reglas de negocio, flujos, autenticación/autorización y estrategia de pruebas. El modelo de datos está en `01-logica-negocio-bd.md` — este documento asume que ya lo leíste.

## 0. Stack y decisiones técnicas confirmadas

- Spring Boot + Spring Data JPA (Hibernate) sobre MySQL 8.
- Arquitectura por capas: `controller` → `service` → `repository`, con DTOs de entrada/salida separados de las entidades (nunca serializar entidades JPA directamente en las respuestas).
- Autenticación: **JWT stateless** con Spring Security. Access token de vida corta (15-30 min); si se implementa refresh token, vida larga (7 días) revocable.
- Pruebas: **Testcontainers con MySQL real** (no H2), corriendo sobre Docker — la misma imagen de MySQL usada en tests es la que se usará para desarrollo local y, eventualmente, producción. Esto da consistencia entre "lo que se prueba" y "lo que se despliega".
- Empaquetado: la aplicación corre en Docker (Dockerfile propio), pensada para desplegarse como contenedor independiente del contenedor de MySQL.
- Hosting de MySQL en producción: pendiente de decidir (Railway es la opción que se está evaluando). El backend debe leer la conexión a BD por variables de entorno (`SPRING_DATASOURCE_URL`, `_USERNAME`, `_PASSWORD`) para no acoplarse a ningún proveedor específico.

## 1. Autenticación y registro

**Registro de estudiante**
- El email debe cumplir el formato completo `^U[0-9]{8}@utp\.edu\.pe$` — no solo el dominio. El `codigo_estudiante` se extrae de la parte local del email, nunca se pide como campo independiente (evita que queden desincronizados).
- `nombres`/`apellidos`: solo letras y espacios (incluye tildes y ñ), 2-100 caracteres.
- `carrera_id` debe existir y estar en estado `ACTIVA`.
- Password: mínimo 8 caracteres, al menos 1 mayúscula y 1 número. Se persiste con BCrypt.
- Verificar unicidad de email y código antes de insertar; si la BD rechaza por constraint, traducir a un mensaje de negocio claro (409), no un error crudo.
- Al crear: `rol = ESTUDIANTE`, `estado = ACTIVO`.
- (Mejora recomendada, no bloqueante para el MVP) Verificación de correo institucional antes de activar la cuenta.

**Login**
- Si `usuario.estado != ACTIVO` (INACTIVO o SUSPENDIDO), rechazar con 403 y mensaje específico según el estado, no un 401 genérico.
- El JWT lleva como claims: id de usuario, rol, estado. Firmado con una clave desde variables de entorno.

**Autorización**
- Reglas por prefijo de ruta: `/api/admin/**` solo rol ADMIN, `/api/estudiante/**` solo rol ESTUDIANTE.
- Ningún endpoint de "mis reseñas" o "mis solicitudes" debe confiar en un `estudiante_id` que venga en el body — siempre se extrae del JWT autenticado.

## 2. Módulo de cursos y docentes (CRUD admin)

- **Curso**: si `tipo = GENERAL`, el service fuerza `carrera_id = null` sin importar lo que venga en el DTO. Si `tipo = CARRERA`, `carrera_id` es obligatorio y debe existir y estar activa.
- Duplicados de nombre en cursos GENERAL: como el UNIQUE de BD no los detecta (por el manejo de NULLs en MySQL), el service debe validar manualmente antes de insertar.
- **Docente**: al "eliminar" desde el admin, si tiene registros en `curso_docente` con reseñas asociadas, bloquear el borrado físico y forzar `estado = INACTIVO` (soft delete). El mensaje de error debe ser de negocio, no un stacktrace de violación de FK.
- **curso_docente**: al "quitar" un docente de un curso, cambiar `estado = INACTIVO` en la fila, nunca borrarla si ya tiene reseñas.

## 3. Módulo de reseñas (estudiante)

**Crear reseña**
- Verificar que `curso_docente_id` exista y esté `ACTIVO`.
- Buscar si ya existe una fila "activa" (`PENDIENTE` o `APROBADA`) para esa combinación estudiante + curso_docente:
  - Si existe y está `PENDIENTE` → la request se trata como actualización de esa misma fila (no se crea una nueva).
  - Si existe y está `APROBADA` → rechazar con 409 ("ya tienes una reseña aprobada para este curso y docente").
  - Si la última reseña de esa combinación está `RECHAZADA` (no hay ninguna activa) → es un **reenvío**: se crea una fila nueva con `version = anterior.version + 1` y `resena_anterior_id = anterior.id`, estado `PENDIENTE`. La fila anterior queda intacta como historial permanente.
  - El índice único sobre `clave_activa` en BD es la garantía de última instancia; el service debe capturar la violación y traducirla a un mensaje de negocio (nunca dejar pasar el error crudo de MySQL al cliente).
- Debe venir al menos 1 calificación (`resena_calificacion`) por cada `criterio_calificacion` activo, con puntaje entre 1 y 5.
- `es_anonimo` lo decide el estudiante en el payload.
- El estado inicial siempre es `PENDIENTE`, ignorando cualquier valor de estado que venga en el DTO de entrada.

**Listar mis reseñas**
- Filtrar siempre por el `estudiante_id` extraído del JWT.
- Agrupar por combinación curso_docente y mostrar por defecto solo la última versión (opcionalmente exponer el historial completo si el estudiante quiere verlo).
- Incluir las `PENDIENTE` y `RECHAZADA` con su motivo — el estudiante necesita ver en qué estado está lo suyo.

## 4. Módulo de moderación (admin)

**Aprobar/rechazar reseña**
- Solo es válida la transición desde `PENDIENTE`. Si ya está `APROBADA` o `RECHAZADA`, devolver 409 (no permitir doble moderación silenciosa).
- Al rechazar, `motivo_rechazo` es obligatorio.
- Registrar `admin_moderador_id` (del JWT) y `fecha_moderacion` (ahora).

**Ocultar reseña ya aprobada**
- Transición `APROBADA` → `OCULTA`, para casos de reportes/abuso posteriores sin perder el historial.

## 5. Módulo de solicitudes (curso/docente inexistente)

**Crear solicitud**
- El `tipo` determina qué campos son obligatorios (`CURSO_NUEVO` requiere nombre de curso, `DOCENTE_NUEVO` requiere nombre de docente, `AMBOS` requiere ambos).
- Antes de guardar, sugerir (no bloquear) si ya existe un curso/docente con nombre muy similar, para reducir duplicados por error de tipeo.

**Aprobar solicitud** — flujo transaccional (`@Transactional`), todo o nada:
1. Si el tipo incluye `DOCENTE_NUEVO`, crear el `docente`.
2. Si el tipo incluye `CURSO_NUEVO`, crear el `curso`.
3. Crear o reutilizar la fila `curso_docente` correspondiente.
4. Crear la `resena` directamente en estado **`APROBADA`** (no pasa por moderación otra vez — el admin la está formalizando y aprobando en el mismo acto), usando el `comentario` de la solicitud, vinculada al estudiante original. `admin_moderador_id` = el mismo admin, `fecha_moderacion` = ahora.
5. Guardar `resena_generada_id` en la solicitud y marcarla `APROBADA`.
- Como la reseña se publica de inmediato sin pasar por el flujo normal, el service debe validar el `comentario` con las mismas reglas mínimas que cualquier reseña (no vacío, longitud mínima) antes de dejar aprobar la solicitud — es la única revisión que va a tener.

**Rechazar solicitud**: `motivo_rechazo` obligatorio, no se crea nada.

## 6. Listados públicos (admin + estudiante)

- Listado general y listado por curso+docente: solo `resena.estado = APROBADA`, paginados y ordenables (por fecha o por promedio de calificación).
- El DTO de salida nunca incluye identidad del estudiante si `es_anonimo = true` — se construye el DTO campo por campo, nunca serializando la entidad completa.
- El promedio por criterio se calcula con una consulta agregada en BD, no trayendo todo a memoria para promediar en Java.

## 7. Manejo de errores

- Handler global que traduce:
  - Violación de UNIQUE/FK/CHECK → 409 con mensaje de negocio específico según el contexto (reseña duplicada, curso con docentes activos, etc.), nunca el mensaje crudo de MySQL.
  - Fallo de validación de DTO → 400 con la lista de campos inválidos.
  - Excepciones de negocio propias (reseña ya existe activa, transición de estado inválida, etc.) → códigos HTTP específicos y mensajes claros para el frontend.

## 8. Estrategia de pruebas (TDD)

- Tests de servicio (lógica de negocio pura: transiciones de estado, versionado de reseñas, validaciones de curso GENERAL/CARRERA) con mocks de los repositorios.
- Tests de integración con Testcontainers levantando un contenedor real de MySQL 8 para cada suite — así se prueban también los constraints de BD (CHECK, UNIQUE sobre `clave_activa`, etc.), no solo la lógica de Java.
- Tests de controlador (MockMvc) para verificar códigos de estado HTTP y forma de los DTOs de respuesta, incluyendo el caso de `es_anonimo` (que el nombre del estudiante nunca se filtre).
- Casos de prueba mínimos que la rúbrica espera ver evidenciados: creación de reseña, intento de reseña duplicada activa, reenvío tras rechazo, aprobación de solicitud con creación en cascada de curso/docente/reseña.

# UTP+Recommends — Lógica de negocio de la base de datos (MySQL 8)

Este documento describe el modelo de datos y las reglas que la propia base de datos garantiza. Cualquier regla que no aparezca aquí como constraint vive en el backend (ver `02-logica-negocio-backend.md`) — la BD solo impone lo que estructuralmente puede.

Decisiones de diseño ya cerradas:
- Llaves primarias: `BIGINT AUTO_INCREMENT` en todas las tablas.
- La BD solo tiene constraints estructurales (FK, UNIQUE, CHECK, NOT NULL). No hay triggers ni stored procedures — toda regla de negocio compleja vive en el backend.
- Motor `InnoDB`, charset `utf8mb4`.

## Entidades y campos

### usuario
Tabla base para ambos roles (ADMIN y ESTUDIANTE); no hay tablas separadas por rol.
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| email | VARCHAR(150) UNIQUE | Debe terminar en `@utp.edu.pe` (CHECK) |
| password_hash | VARCHAR(255) | BCrypt, nunca texto plano |
| nombres, apellidos | VARCHAR(100) | |
| rol | ENUM(ADMIN, ESTUDIANTE) | |
| estado | ENUM(ACTIVO, INACTIVO, SUSPENDIDO) | default ACTIVO |
| created_at, updated_at | TIMESTAMP | |

### carrera
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| nombre | VARCHAR(150) UNIQUE | |
| estado | ENUM(ACTIVA, INACTIVA) | default ACTIVA |

### estudiante
Extiende `usuario` en relación 1:1 (no herencia de tablas, es una FK única).
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| usuario_id | BIGINT UNIQUE FK → usuario | ON DELETE CASCADE |
| codigo_estudiante | VARCHAR(9) UNIQUE | Debe matchear `^U[0-9]{8}$` (CHECK). Es la parte local del email, no un campo independiente. |
| carrera_id | BIGINT FK → carrera | ON DELETE RESTRICT |

### docente
No tiene login, es gestionado por el admin (CRUD).
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| nombres, apellidos | VARCHAR(100) | |
| email | VARCHAR(150) UNIQUE NULL | |
| estado | ENUM(ACTIVO, INACTIVO) | Soft delete: nunca se borra un docente con reseñas asociadas, se inactiva |
| created_at, updated_at | TIMESTAMP | |

### curso
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| nombre | VARCHAR(150) | |
| tipo | ENUM(GENERAL, CARRERA) | |
| carrera_id | BIGINT FK → carrera, NULL | Obligatorio solo si tipo = CARRERA (CHECK combinado) |
| estado | ENUM(ACTIVO, INACTIVO) | |

**Importante**: hay un índice `UNIQUE(nombre, carrera_id)`, pero MySQL trata cada `NULL` como distinto dentro de un índice único. Como los cursos GENERAL siempre tienen `carrera_id = NULL`, ese índice **no evita duplicados entre cursos GENERAL** — esa validación de nombre duplicado debe hacerla el backend antes de insertar.

### curso_docente (relación N:N)
Un curso puede tener N docentes; un docente puede dictar N cursos.
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| curso_id | BIGINT FK → curso | ON DELETE RESTRICT |
| docente_id | BIGINT FK → docente | ON DELETE RESTRICT |
| estado | ENUM(ACTIVO, INACTIVO) | Se inactiva (no se borra) cuando se "quita" la asignación, para no perder historial de reseñas |
| — | UNIQUE(curso_id, docente_id) | No se puede asignar el mismo docente al mismo curso dos veces |

### criterio_calificacion
Gestionable por el admin — no está hardcodeado, para poder agregar/desactivar criterios sin tocar código (ej: Claridad, Puntualidad, Exigencia justa, Disponibilidad).
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| nombre | VARCHAR(100) UNIQUE | |
| descripcion | VARCHAR(255) NULL | |
| estado | ENUM(ACTIVO, INACTIVO) | |

### resena
La entidad central del sistema. Soporta **versionado**: cuando una reseña es rechazada, el estudiante puede reenviarla, pero la fila original nunca se edita ni se borra (queda como historial).
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| estudiante_id | BIGINT FK → estudiante | ON DELETE CASCADE |
| curso_docente_id | BIGINT FK → curso_docente | ON DELETE RESTRICT |
| comentario | TEXT | |
| es_anonimo | BOOLEAN | El estudiante elige al publicar. Si es true, el backend nunca debe devolver su nombre en el DTO público (sí se muestra siempre al admin, para moderar) |
| estado | ENUM(PENDIENTE, APROBADA, RECHAZADA, OCULTA) | default PENDIENTE |
| motivo_rechazo | VARCHAR(255) NULL | Obligatorio si estado = RECHAZADA (CHECK) |
| admin_moderador_id | BIGINT FK → usuario, NULL | ON DELETE SET NULL |
| version | INT | default 1, se incrementa en cada reenvío |
| resena_anterior_id | BIGINT FK → resena (self), NULL | Encadena a la versión previa |
| fecha_creacion, fecha_moderacion | TIMESTAMP | |
| clave_activa | VARCHAR(60) GENERATED | Ver regla de unicidad abajo |

**Regla de unicidad activa (el truco central del esquema)**: `clave_activa` es una columna generada (`GENERATED ALWAYS AS (...) STORED`) que solo tiene valor cuando `estado IN (PENDIENTE, APROBADA)` — su valor es `CONCAT(estudiante_id, '-', curso_docente_id)`; si el estado es RECHAZADA u OCULTA, es `NULL`. Hay un `UNIQUE(clave_activa)`. Como MySQL no soporta índices únicos parciales (a diferencia de Postgres), este es el mecanismo para lograr el mismo efecto: **un estudiante solo puede tener una reseña "viva" (pendiente o aprobada) por combinación curso+docente, pero puede acumular múltiples reseñas RECHAZADA históricas** de reenvíos sucesivos.

### resena_calificacion
Puntaje por criterio, tabla puente entre `resena` y `criterio_calificacion`.
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| resena_id | BIGINT FK → resena | ON DELETE CASCADE |
| criterio_id | BIGINT FK → criterio_calificacion | ON DELETE RESTRICT |
| puntaje | TINYINT | CHECK entre 1 y 5 |
| — | UNIQUE(resena_id, criterio_id) | Un puntaje por criterio, no repetido |

### solicitud
Cuando el estudiante no encuentra un curso o docente en el sistema, lo solicita junto con su reseña.
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGINT PK | |
| estudiante_id | BIGINT FK → estudiante | ON DELETE CASCADE |
| tipo | ENUM(CURSO_NUEVO, DOCENTE_NUEVO, AMBOS) | |
| nombre_curso_sugerido | VARCHAR(150) NULL | Obligatorio si tipo incluye curso (CHECK) |
| carrera_sugerida_id | BIGINT FK → carrera, NULL | |
| nombre_docente_sugerido | VARCHAR(150) NULL | Obligatorio si tipo incluye docente (CHECK) |
| comentario | TEXT | La reseña que acompaña la solicitud |
| estado | ENUM(PENDIENTE, APROBADA, RECHAZADA) | |
| admin_id | BIGINT FK → usuario, NULL | Quien resuelve |
| resena_generada_id | BIGINT FK → resena, NULL | Se llena cuando se aprueba (ver flujo en el doc de backend) |
| motivo_rechazo | VARCHAR(255) NULL | |
| fecha_creacion, fecha_resolucion | TIMESTAMP | |

## Relaciones (resumen)

- `usuario` 1:1 `estudiante` (un admin no tiene fila en `estudiante`)
- `carrera` 1:N `estudiante`, `carrera` 1:N `curso` (para tipo CARRERA)
- `curso` N:N `docente` a través de `curso_docente`
- `estudiante` 1:N `resena`, `curso_docente` 1:N `resena`
- `resena` 1:N `resena_calificacion`, `criterio_calificacion` 1:N `resena_calificacion`
- `resena` 1:N `resena` (self-reference vía `resena_anterior_id`, cadena de versiones)
- `estudiante` 1:N `solicitud`, `solicitud` 1:1 `resena` (cuando se aprueba)
- `usuario` (rol ADMIN) 1:N `resena` (modera) y 1:N `solicitud` (resuelve)

## Decisiones pendientes de infraestructura (no afectan el modelo, sí el despliegue)

- Hosting de MySQL para producción: aún no decidido entre Railway, MySQL propio en Render con disco persistente, u otro proveedor. El esquema es agnóstico a esto.

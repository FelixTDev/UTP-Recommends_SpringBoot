# UTP+Recommends - Logica de negocio de la base de datos

Este documento describe el modelo de datos del proyecto `UTP+Recommends`, las reglas que se garantizan desde MySQL 8 y los criterios de diseno adoptados para mantener consistencia entre persistencia, backend y frontend.

Su objetivo es servir como anexo tecnico para la sustentacion final del proyecto. Las reglas funcionales complejas que no pueden expresarse de forma estructural en la base de datos se implementan en la capa backend y se detallan en `02-logica-negocio-backend.md`.

## 1. Criterios de diseno del esquema

Decisiones cerradas del modelo:

- todas las tablas usan llave primaria `BIGINT AUTO_INCREMENT`;
- el motor de almacenamiento es `InnoDB`;
- el charset definido es `utf8mb4`;
- la base de datos aplica restricciones estructurales mediante `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `CHECK` y `NOT NULL`;
- no se utilizan triggers ni stored procedures;
- la logica de negocio compleja se resuelve en la capa de servicios del backend.

## 2. Entidades principales

### 2.1. `usuario`

Tabla base para cuentas autenticadas del sistema. Centraliza credenciales y datos comunes para `ADMIN` y `ESTUDIANTE`.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador interno |
| `email` | `VARCHAR(150)` UNIQUE | Correo institucional |
| `password_hash` | `VARCHAR(255)` | Password cifrada con BCrypt |
| `nombres` | `VARCHAR(100)` | Nombres del usuario |
| `apellidos` | `VARCHAR(100)` | Apellidos del usuario |
| `rol` | `ENUM(ADMIN, ESTUDIANTE)` | Rol de acceso |
| `estado` | `ENUM(ACTIVO, INACTIVO, SUSPENDIDO)` | Estado de la cuenta |
| `created_at` | `TIMESTAMP` | Fecha de creacion |
| `updated_at` | `TIMESTAMP` | Fecha de actualizacion |

Consideraciones:

- el sistema no separa usuarios en tablas por rol;
- el control de acceso se resuelve por `rol` y `estado`;
- el correo debe pertenecer al dominio institucional.

### 2.2. `carrera`

Representa una carrera academica activa o inactiva.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `nombre` | `VARCHAR(150)` UNIQUE | Nombre de la carrera |
| `estado` | `ENUM(ACTIVA, INACTIVA)` | Estado funcional |

### 2.3. `estudiante`

Extiende el perfil de `usuario` en relacion 1:1.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `usuario_id` | `BIGINT` UNIQUE FK | Referencia a `usuario` |
| `codigo_estudiante` | `VARCHAR(9)` UNIQUE | Codigo institucional |
| `carrera_id` | `BIGINT` FK | Carrera a la que pertenece |

Consideraciones:

- no se usa herencia de tablas;
- `usuario_id` garantiza que un estudiante tenga una sola extension academica;
- `codigo_estudiante` se deriva del correo y debe respetar el patron `U########`.

### 2.4. `docente`

Entidad administrada por el rol `ADMIN`. No posee autenticacion propia.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `nombres` | `VARCHAR(100)` | Nombres del docente |
| `apellidos` | `VARCHAR(100)` | Apellidos del docente |
| `email` | `VARCHAR(150)` UNIQUE NULL | Correo de contacto opcional |
| `estado` | `ENUM(ACTIVO, INACTIVO)` | Estado funcional |
| `created_at` | `TIMESTAMP` | Fecha de creacion |
| `updated_at` | `TIMESTAMP` | Fecha de actualizacion |

Consideracion de negocio:

- cuando un docente ya tiene historial asociado, se privilegia inactivarlo antes que eliminarlo fisicamente.

### 2.5. `curso`

Representa cursos generales o de carrera.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `nombre` | `VARCHAR(150)` | Nombre del curso |
| `tipo` | `ENUM(GENERAL, CARRERA)` | Clasificacion academica |
| `carrera_id` | `BIGINT` FK NULL | Obligatorio si `tipo = CARRERA` |
| `estado` | `ENUM(ACTIVO, INACTIVO)` | Estado funcional |

Observacion importante:

- existe un `UNIQUE(nombre, carrera_id)`;
- en MySQL los valores `NULL` no colisionan en indices unicos;
- por ello, los cursos `GENERAL` con `carrera_id = NULL` pueden duplicarse si el backend no los valida antes;
- esta validacion se resuelve en la capa de servicio.

### 2.6. `curso_docente`

Tabla intermedia para la relacion N:N entre cursos y docentes.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `curso_id` | `BIGINT` FK | Referencia a `curso` |
| `docente_id` | `BIGINT` FK | Referencia a `docente` |
| `estado` | `ENUM(ACTIVO, INACTIVO)` | Estado de la asignacion |

Restriccion clave:

- `UNIQUE(curso_id, docente_id)` impide registrar la misma dupla mas de una vez.

### 2.7. `criterio_calificacion`

Catalogo administrable de criterios usados para puntuar resenas.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `nombre` | `VARCHAR(100)` UNIQUE | Nombre del criterio |
| `descripcion` | `VARCHAR(255)` NULL | Descripcion opcional |
| `estado` | `ENUM(ACTIVO, INACTIVO)` | Disponibilidad del criterio |

Ventaja del diseno:

- los criterios no estan hardcodeados;
- el admin puede activarlos o desactivarlos sin modificar codigo.

### 2.8. `resena`

Entidad central del sistema. Almacena la experiencia del estudiante sobre una combinacion `curso-docente`.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `estudiante_id` | `BIGINT` FK | Autor de la resena |
| `curso_docente_id` | `BIGINT` FK | Curso y docente evaluados |
| `comentario` | `TEXT` | Texto principal |
| `es_anonimo` | `BOOLEAN` | Control de anonimato |
| `estado` | `ENUM(PENDIENTE, APROBADA, RECHAZADA, OCULTA)` | Estado de moderacion |
| `motivo_rechazo` | `VARCHAR(255)` NULL | Motivo obligatorio si fue rechazada |
| `admin_moderador_id` | `BIGINT` FK NULL | Admin que modera |
| `version` | `INT` | Version de la resena |
| `resena_anterior_id` | `BIGINT` FK NULL | Referencia a version previa |
| `fecha_creacion` | `TIMESTAMP` | Fecha de registro |
| `fecha_moderacion` | `TIMESTAMP` NULL | Fecha de resolucion |
| `clave_activa` | `VARCHAR(60)` GENERATED | Soporte para unicidad activa |

Reglas relevantes:

- el sistema conserva historial de versiones;
- una resena rechazada no se sobrescribe, se conserva y el reenvio genera una nueva fila;
- el anonimato solo aplica para vistas publicas o estudiantiles, no para moderacion administrativa.

### 2.9. `resena_calificacion`

Tabla puente entre `resena` y `criterio_calificacion`.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `resena_id` | `BIGINT` FK | Referencia a `resena` |
| `criterio_id` | `BIGINT` FK | Referencia a `criterio_calificacion` |
| `puntaje` | `TINYINT` | Valor entre 1 y 5 |

Restriccion clave:

- `UNIQUE(resena_id, criterio_id)` impide duplicar el mismo criterio dentro de una resena.

### 2.10. `solicitud`

Representa pedidos de creacion de cursos, docentes o ambos cuando aun no existen en el sistema.

| Campo | Tipo | Descripcion |
|---|---|---|
| `id` | `BIGINT` PK | Identificador |
| `estudiante_id` | `BIGINT` FK | Estudiante solicitante |
| `tipo` | `ENUM(CURSO_NUEVO, DOCENTE_NUEVO, AMBOS)` | Tipo de solicitud |
| `nombre_curso_sugerido` | `VARCHAR(150)` NULL | Nombre del curso propuesto |
| `carrera_sugerida_id` | `BIGINT` FK NULL | Carrera sugerida |
| `nombre_docente_sugerido` | `VARCHAR(150)` NULL | Nombre del docente propuesto |
| `comentario` | `TEXT` | Comentario o resena asociada |
| `estado` | `ENUM(PENDIENTE, APROBADA, RECHAZADA)` | Estado de la solicitud |
| `admin_id` | `BIGINT` FK NULL | Admin que resuelve |
| `resena_generada_id` | `BIGINT` FK NULL | Resena creada al aprobar |
| `motivo_rechazo` | `VARCHAR(255)` NULL | Sustento del rechazo |
| `fecha_creacion` | `TIMESTAMP` | Fecha de registro |
| `fecha_resolucion` | `TIMESTAMP` NULL | Fecha de respuesta |

## 3. Regla central de unicidad activa

El diseno de `resena` resuelve una necesidad importante del negocio:

- un estudiante no debe tener mas de una resena activa para la misma combinacion `curso-docente`;
- al mismo tiempo, si una resena fue rechazada, se debe conservar el historial y permitir reenvios.

Para ello se utiliza `clave_activa`, una columna generada que:

- toma el valor `CONCAT(estudiante_id, '-', curso_docente_id)` cuando la resena esta `PENDIENTE` o `APROBADA`;
- toma `NULL` cuando la resena esta `RECHAZADA` u `OCULTA`.

Luego se aplica `UNIQUE(clave_activa)`. Con esto se logra un efecto equivalente a un indice unico parcial:

- una sola resena viva por estudiante y curso-docente;
- multiples resenas historicas rechazadas sin romper la consistencia.

## 4. Relaciones principales del modelo

- `usuario` 1:1 `estudiante`
- `carrera` 1:N `estudiante`
- `carrera` 1:N `curso`
- `curso` N:N `docente` mediante `curso_docente`
- `estudiante` 1:N `resena`
- `curso_docente` 1:N `resena`
- `resena` 1:N `resena_calificacion`
- `criterio_calificacion` 1:N `resena_calificacion`
- `resena` 1:N `resena` mediante `resena_anterior_id`
- `estudiante` 1:N `solicitud`
- `solicitud` 1:1 `resena` cuando se aprueba
- `usuario` con rol `ADMIN` 1:N `resena` y 1:N `solicitud` como moderador o resolutor

## 5. Responsabilidades de la base de datos

La base de datos garantiza:

- integridad referencial;
- unicidad de claves de negocio estructurales;
- consistencia minima de estados y relaciones;
- soporte para historico de resenas;
- soporte para moderacion y trazabilidad.

La base de datos no resuelve por si sola:

- autorizacion por rol;
- validacion completa de formularios;
- traduccion de errores a mensajes de negocio;
- flujos de aprobacion o rechazo;
- reglas dinamicas de integracion con frontend.

## 6. Riesgos controlados desde backend

Estas situaciones dependen de validacion adicional en Spring Boot:

- evitar nombres duplicados en cursos `GENERAL`;
- impedir transiciones de estado invalidas;
- verificar criterios activos requeridos al crear una resena;
- decidir cuando corresponde soft delete o bloqueo funcional;
- controlar que un estudiante solo opere sobre sus propios recursos;
- traducir errores SQL en respuestas HTTP comprensibles.

## 7. Conclusiones tecnicas

El modelo de datos de `UTP+Recommends` fue disenado para soportar:

- autenticacion y separacion de roles;
- gestion academica administrable;
- moderacion de contenido;
- trazabilidad de resenas y solicitudes;
- integracion estable con una arquitectura `Angular + Spring Boot + MySQL`.

El esquema privilegia consistencia, claridad de relaciones y soporte para reglas reales del dominio academico, manteniendo en la base solo aquello que corresponde garantizar de forma estructural.

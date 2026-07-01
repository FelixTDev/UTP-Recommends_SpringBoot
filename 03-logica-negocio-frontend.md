# UTP+Recommends — Lógica de negocio del frontend (Angular)

Este documento describe rutas, validaciones, guards y comportamiento de UI esperado. Depende de los flujos definidos en `02-logica-negocio-backend.md` — el frontend no reimplementa reglas de negocio, solo las refleja para dar buena UX y hace su propia validación de formularios como primera barrera (no la única).

## 0. Stack y decisiones técnicas confirmadas

- Angular con **standalone components** (sin NgModules).
- Estilos: **Angular Material + SCSS**. Se eligió Material sobre PrimeNG/SCSS puro porque se integra nativo con Reactive Forms (errores vía `mat-error` sin código extra), incluye `mat-table` con paginación/ordenamiento ya resuelto (útil para los listados de admin), tiene accesibilidad por defecto, y lo mantiene el mismo equipo de Angular (menor riesgo de incompatibilidad de versiones a futuro).
- Formularios: Reactive Forms en todo el proyecto (no template-driven), con validadores custom para las reglas específicas del dominio.

## 1. Estructura de rutas y guards

```
/auth/login
/auth/registro
/estudiante/inicio
/estudiante/resenas/nueva
/estudiante/resenas/mis-resenas
/estudiante/solicitudes/nueva
/estudiante/listado-general
/estudiante/listado-por-curso
/admin/docentes
/admin/cursos
/admin/usuarios
/admin/moderacion/resenas
/admin/moderacion/solicitudes
/admin/criterios
```

- `AuthGuard`: verifica que exista un JWT válido (no expirado). Si no, redirige a `/auth/login`.
- `RoleGuard`: recibe el rol requerido por la ruta y lo compara contra el rol decodificado del JWT (guardado en el estado de `AuthService`). Si no coincide, redirige a una página de "no autorizado" — esto protege aunque el usuario escriba la URL de admin a mano.
- Ocultar botones/links según el rol es solo UX; la seguridad real la dan el guard de ruta y la validación del backend. Nunca asumir que ocultar un botón es suficiente.

## 2. Validaciones de formularios (primera barrera, no la única)

**Correo institucional / código de estudiante**
- Formato esperado: `U` + 8 dígitos + `@utp.edu.pe` exacto. El mensaje de error debe ser específico ("Debe ser un correo institucional con formato U + 8 dígitos + @utp.edu.pe"), no un genérico "campo inválido".

**Nombres y apellidos**
- Solo letras, espacios, tildes y ñ. Rechazar números o símbolos con mensaje claro.

**Password**
- Mínimo 8 caracteres, al menos 1 mayúscula y 1 número. Opcional: indicador visual de fuerza.

**Formulario de reseña**
- Los criterios de calificación se cargan dinámicamente desde el backend (no están hardcodeados en el frontend) — si el admin agrega o desactiva un criterio, el formulario debe reflejarlo sin necesidad de tocar código.
- Todos los criterios activos son obligatorios antes de habilitar el botón de envío.
- El checkbox de "publicar como anónimo" debe llevar un texto de ayuda aclarando que el admin siempre verá la identidad del estudiante para efectos de moderación; el anonimato solo aplica frente a otros estudiantes.
- Si el estudiante ya tiene una reseña `RECHAZADA` para esa combinación curso+docente, el formulario de "nueva reseña" debe comportarse como un reenvío: precargar el comentario/calificaciones anteriores y dejar claro que se está reemplazando la versión rechazada.

**Formulario de solicitud**
- El campo `tipo` (CURSO_NUEVO / DOCENTE_NUEVO / AMBOS) determina qué campos son obligatorios — usar `valueChanges` del control de tipo para togglear validadores requeridos dinámicamente.
- Antes de enviar, un autocomplete contra cursos/docentes existentes ayuda a reducir duplicados por error de tipeo (mismo espíritu que la sugerencia de similitud que hace el backend).

## 3. Autenticación y sesión

- Interceptor HTTP que agrega el JWT a cada request hacia la API, excepto login/registro.
- Maneja 401 (token expirado): si hay refresh token, intenta renovar una sola vez; si falla, limpia la sesión y redirige a login. Evitar loops de refresh.
- Maneja 403: mostrar mensaje de "no tienes permiso", no un error crudo — puede pasar si el rol cambió en el servidor mientras la sesión seguía activa en el cliente.
- `AuthService` es la única fuente de verdad del estado de sesión (rol, nombre, id decodificados del JWT); ningún componente decodifica el token por su cuenta.

## 4. Servicios y consumo de API

- Un servicio por dominio (reseñas, solicitudes, cursos, docentes, moderación), cada uno responsable de la forma exacta de sus DTOs — los componentes nunca arman el payload a mano.
- Evitar duplicar lógica de negocio del backend en el cliente más allá de la validación de formularios: por ejemplo, no calcular el promedio de calificaciones en el frontend si el backend ya lo devuelve calculado (evita inconsistencias si cambian las reglas de agregación).
- Los listados públicos se paginan desde el backend (parámetros `page`/`size`), nunca traer todo y paginar en el cliente.
- Los filtros de listado (carrera, curso, docente, rating) se sincronizan con los query params de la URL, para que se puedan compartir o recargar sin perderse.

## 5. UX de estados

- En "mis reseñas", badge de color por estado: `PENDIENTE` (ámbar), `APROBADA` (verde), `RECHAZADA` (rojo, con el motivo visible).
- Si una reseña está `RECHAZADA`, ofrecer un botón directo "Editar y reenviar" que precarga el formulario (ver regla de reenvío arriba) — no obligar a empezar de cero.
- Las solicitudes del estudiante muestran su estado con la misma lógica de badges.

## 6. Rol admin — módulos de gestión

- CRUD de docentes y cursos con confirmación antes de "eliminar" (recordar que en el backend muchas eliminaciones en realidad son soft-delete a `INACTIVO`; el mensaje de confirmación debe reflejar eso, no prometer un borrado físico).
- Cola de moderación de reseñas y solicitudes: mostrar primero las más antiguas pendientes, con acceso rápido a aprobar/rechazar y campo obligatorio de motivo al rechazar.
- Gestión de criterios de calificación: activar/desactivar, no borrar (por la relación con `resena_calificacion` histórica).

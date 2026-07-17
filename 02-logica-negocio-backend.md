# UTP+Recommends - Logica de negocio del backend

Este documento resume el comportamiento esperado del backend en Spring Boot, sus reglas funcionales principales, criterios de seguridad, manejo de datos y estrategia de pruebas. Su finalidad es respaldar tecnicamente la sustentacion del proyecto final y dejar trazables las decisiones de implementacion.

El modelo de datos base se detalla en `01-logica-negocio-bd.md`. Este documento se enfoca en la capa de aplicacion: controladores, servicios, autorizacion y validaciones de negocio.

## 1. Stack y decisiones tecnicas

El backend se construye con:

- Spring Boot;
- Spring Web;
- Spring Data JPA;
- Spring Security;
- JWT stateless;
- MySQL 8;
- Maven;
- Docker;
- Testcontainers para integracion.

Decisiones tecnicas principales:

- arquitectura por capas `controller -> service -> repository`;
- DTOs separados de las entidades para entrada y salida;
- entidades JPA no expuestas directamente al cliente;
- autenticacion basada en JWT con control de roles;
- persistencia validada contra schema existente;
- pruebas de integracion con MySQL real, no con H2.

## 2. Responsabilidad de cada capa

### 2.1. Controladores

Se encargan de:

- recibir requests HTTP;
- validar formato basico de entrada;
- delegar la logica al servicio correspondiente;
- devolver respuestas y codigos HTTP adecuados.

### 2.2. Servicios

Se encargan de:

- aplicar reglas del dominio;
- gestionar transacciones;
- coordinar multiples repositorios;
- validar permisos funcionales;
- traducir estados del sistema en respuestas coherentes.

### 2.3. Repositorios

Se encargan de:

- acceso a persistencia;
- consultas derivadas y personalizadas;
- soporte para filtros, busquedas y agregaciones.

## 3. Autenticacion y autorizacion

## 3.1. Registro de estudiante

Reglas principales:

- el correo debe cumplir el formato `^U[0-9]{8}@utp.edu.pe$`;
- `codigo_estudiante` se extrae del correo y no se solicita por separado;
- `nombres` y `apellidos` aceptan solo letras y espacios;
- `carrera_id` debe existir y estar en estado activo;
- la password debe tener al menos 8 caracteres, una mayuscula y un numero;
- la password se guarda con BCrypt;
- el registro se crea con `rol = ESTUDIANTE` y `estado = ACTIVO`.

Validaciones funcionales:

- no se permite duplicar correo;
- no se permite duplicar codigo institucional;
- los errores de constraint deben traducirse a mensajes claros para frontend.

## 3.2. Login

Reglas principales:

- si el usuario no existe o la password no coincide, se rechaza la autenticacion;
- si el usuario esta `INACTIVO` o `SUSPENDIDO`, se devuelve rechazo con mensaje funcional;
- el JWT incluye al menos `id`, `rol` y `estado`;
- el token se firma con una clave configurada por variables de entorno.

## 3.3. Autorizacion por roles

Reglas generales:

- `/api/admin/**` solo para `ADMIN`;
- `/api/estudiante/**` solo para `ESTUDIANTE`;
- `/api/public/**` accesible sin autenticacion;
- los endpoints de recursos propios toman identidad desde el JWT, no desde el body del cliente.

## 4. Modulo administrativo

## 4.1. Gestion de cursos

Reglas:

- si `tipo = GENERAL`, el sistema fuerza `carrera_id = null`;
- si `tipo = CARRERA`, `carrera_id` es obligatorio;
- la carrera asociada debe existir y estar activa;
- para cursos generales se valida manualmente duplicidad de nombre.

## 4.2. Gestion de docentes

Reglas:

- el admin puede crear, listar y actualizar docentes;
- si un docente ya tiene historial relacionado, se prioriza inactivarlo antes que borrarlo;
- no deben exponerse errores crudos de clave foranea al usuario.

## 4.3. Gestion de curso-docente

Reglas:

- no se debe repetir la misma combinacion `curso-docente`;
- si una asignacion ya tiene historial, al retirarla se cambia a `INACTIVO` en lugar de eliminarla;
- se preserva la trazabilidad de resenas antiguas.

## 4.4. Gestion de criterios

Reglas:

- los criterios son administrables;
- pueden activarse o desactivarse;
- no deben eliminarse si ya participaron en resenas historicas.

## 5. Modulo de resenas

## 5.1. Creacion de resena

Reglas obligatorias:

- `curso_docente_id` debe existir y estar activo;
- la resena debe registrar al menos un puntaje por cada criterio activo;
- todos los puntajes deben estar entre 1 y 5;
- el estado inicial siempre es `PENDIENTE`;
- `es_anonimo` lo define el estudiante;
- el backend no confia en estados enviados desde el cliente.

## 5.2. Regla de resena activa unica

Antes de crear una resena se verifica si ya existe una resena activa para esa combinacion estudiante + curso-docente.

Casos posibles:

- si ya existe una resena `PENDIENTE`, se actualiza esa misma fila;
- si ya existe una resena `APROBADA`, la operacion se rechaza;
- si la ultima fue `RECHAZADA`, se permite reenviar creando una nueva version;
- la base de datos actua como ultima barrera con la columna `clave_activa`.

## 5.3. Historial y versionado

Cuando una resena es rechazada:

- no se edita la fila original;
- se conserva el historial;
- el estudiante puede reenviar;
- el reenvio crea una nueva fila con `version + 1` y referencia a la anterior.

## 5.4. Consulta de resenas propias

Reglas:

- el estudiante solo puede ver sus propias resenas;
- la identidad del estudiante se obtiene del JWT;
- el sistema puede mostrar la ultima version y, si se requiere, el historial;
- se debe mostrar motivo de rechazo cuando exista.

## 6. Modulo de moderacion

## 6.1. Aprobar resena

Reglas:

- solo se aprueban resenas `PENDIENTE`;
- se registra admin moderador;
- se registra fecha de moderacion;
- no se permite doble aprobacion silenciosa.

## 6.2. Rechazar resena

Reglas:

- solo se rechazan resenas `PENDIENTE`;
- `motivo_rechazo` es obligatorio;
- se registra admin moderador y fecha;
- el rechazo conserva la posibilidad de reenvio futuro.

## 6.3. Ocultar resena

Reglas:

- solo aplica sobre resenas ya aprobadas;
- permite retirar contenido visible sin perder el historial;
- se usa para reportes, abuso o control posterior.

## 7. Modulo de solicitudes

## 7.1. Creacion de solicitud

Reglas:

- `tipo` define que campos son obligatorios;
- `CURSO_NUEVO` exige nombre de curso;
- `DOCENTE_NUEVO` exige nombre de docente;
- `AMBOS` exige ambos;
- el comentario asociado debe validarse antes de persistir.

## 7.2. Aprobacion de solicitud

Es un flujo transaccional de todo o nada:

1. crear docente si corresponde;
2. crear curso si corresponde;
3. crear o reutilizar relacion `curso_docente`;
4. generar la resena resultante;
5. registrar `resena_generada_id`;
6. marcar la solicitud como `APROBADA`.

Reglas:

- la resena generada puede quedar directamente `APROBADA` como parte de la resolucion administrativa;
- el comentario debe cumplir validaciones minimas;
- el proceso debe ser atomico para no dejar datos a medio camino.

## 7.3. Rechazo de solicitud

Reglas:

- `motivo_rechazo` es obligatorio;
- no se crean entidades derivadas;
- debe quedar trazabilidad de quien resolvio y cuando.

## 8. Listados publicos

Reglas:

- solo se muestran resenas `APROBADA`;
- el listado debe ser paginable;
- puede ordenarse por fecha o promedio;
- si la resena es anonima, no se expone la identidad del estudiante;
- los promedios por criterio deben calcularse desde base de datos o consultas agregadas.

## 9. Manejo de errores

El backend debe tener un handler global que traduzca:

- errores de validacion a `400`;
- conflictos de negocio a `409`;
- falta de autenticacion a `401`;
- falta de autorizacion a `403`;
- errores inesperados a respuestas controladas.

Criterios de calidad:

- nunca devolver mensajes crudos de MySQL;
- siempre devolver mensajes utiles para el frontend;
- mantener consistencia en formato de respuesta.

## 10. Pruebas

La estrategia de pruebas considera:

- pruebas unitarias de servicios;
- pruebas de controladores con MockMvc;
- pruebas de integracion con Testcontainers y MySQL real;
- validacion de autenticacion y rutas protegidas;
- validacion de flujos de resena, moderacion y solicitudes.

Casos minimos valiosos para sustentar:

- registro e inicio de sesion;
- acceso permitido y denegado por rol;
- intento de duplicar resena activa;
- reenvio de resena rechazada;
- aprobacion de solicitud con creacion encadenada;
- ocultamiento de resena aprobada;
- obtencion de listados publicos anonimizados.

## 11. Consideraciones de despliegue

El backend esta preparado para:

- ejecutarse localmente con Maven;
- empaquetarse en Docker;
- desplegarse como servicio independiente;
- leer configuracion sensible desde variables de entorno;
- conectarse a MySQL sin acoplarse a un proveedor especifico.

Esto facilita integracion con plataformas como Render y otros entornos equivalentes.

## 12. Conclusiones tecnicas

La logica del backend fue disenada para priorizar:

- seguridad;
- consistencia transaccional;
- separacion de responsabilidades;
- trazabilidad de cambios y moderacion;
- integracion limpia con el frontend Angular.

Con este enfoque, el backend no solo expone endpoints funcionales, sino que protege reglas clave del dominio academico y respalda el alcance completo del proyecto final.

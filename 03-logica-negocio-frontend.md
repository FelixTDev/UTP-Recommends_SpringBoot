# UTP+Recommends - Logica de negocio del frontend

Este documento presenta la logica funcional esperada del frontend en Angular: organizacion de rutas, validaciones de formularios, manejo de sesion, integracion con el backend y criterios de experiencia de usuario. Su funcion es respaldar la sustentacion del `Avance de Proyecto Final 03` y del `Proyecto Final`.

El frontend no reemplaza la logica del backend. Su responsabilidad es ofrecer una experiencia clara, segura y consistente con las reglas del dominio implementadas en Spring Boot.

## 1. Stack y criterios de implementacion

El frontend se construye con:

- Angular;
- TypeScript;
- standalone components;
- Angular Material;
- SCSS;
- Reactive Forms;
- Router Guards;
- HTTP Interceptor.

Decisiones tecnicas principales:

- formularios reactivos en todas las pantallas;
- separacion por vistas de autenticacion, estudiante y administrador;
- proteccion de rutas segun sesion y rol;
- consumo de API desacoplado mediante servicios;
- interfaz alineada a la logica y restricciones del backend.

## 2. Estructura funcional de rutas

Rutas principales documentadas:

```text
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

La estructura busca:

- separar claramente vistas publicas, de estudiante y de administrador;
- facilitar navegacion por rol;
- mantener flujos completos y demostrables en sustentacion.

## 3. Proteccion de rutas y manejo de sesion

## 3.1. `AuthGuard`

Responsabilidad:

- verificar que exista una sesion valida;
- impedir acceso a vistas protegidas sin token;
- redirigir a login cuando corresponda.

## 3.2. `RoleGuard`

Responsabilidad:

- validar el rol requerido por la ruta;
- bloquear acceso manual por URL a pantallas no autorizadas;
- derivar a vista de acceso denegado o flujo equivalente.

## 3.3. Interceptor HTTP

Responsabilidad:

- adjuntar `Authorization: Bearer <token>` en requests protegidas;
- excluir login y registro;
- centralizar el tratamiento de errores `401` y `403`.

## 3.4. `AuthService`

Debe ser la unica fuente de verdad para:

- token actual;
- datos basicos de sesion;
- rol autenticado;
- estado de login/logout.

Ningun componente debe decodificar el JWT por su cuenta.

## 4. Validaciones de formularios

## 4.1. Registro

Validaciones principales:

- correo institucional con formato `U########@utp.edu.pe`;
- nombres y apellidos solo con letras y espacios;
- password con minimo de longitud y complejidad;
- carrera obligatoria;
- mensajes de error claros y visibles.

El frontend valida para mejorar UX, pero la validacion definitiva permanece en backend.

## 4.2. Login

Validaciones principales:

- correo obligatorio;
- password obligatoria;
- presentacion clara de errores de autenticacion;
- retroalimentacion ante credenciales invalidas o cuenta bloqueada.

## 4.3. Formulario de resena

Validaciones principales:

- seleccion obligatoria de `curso-docente`;
- comentario obligatorio;
- carga dinamica de criterios desde API;
- todos los criterios activos deben calificarse;
- puntajes dentro del rango permitido;
- opcion de anonimato visible y explicada.

Comportamientos recomendados:

- si existe una resena rechazada previa, ofrecer edicion y reenvio;
- si hay conflicto por resena activa, mostrar mensaje funcional comprensible.

## 4.4. Formulario de solicitud

Validaciones principales:

- el `tipo` define dinamicamente que campos son requeridos;
- si el tipo incluye curso, exigir nombre del curso;
- si el tipo incluye docente, exigir nombre del docente;
- comentario o detalle obligatorio;
- feedback claro antes y despues del envio.

## 5. Comunicacion entre componentes

La comunicacion en frontend debe permitir:

- propagar estado de sesion entre layout, navbar y vistas;
- sincronizar filtros, tablas y formularios;
- reutilizar servicios compartidos por dominio;
- mantener desacoplamiento entre componente visual y capa HTTP.

Buenas practicas esperadas:

- componentes contenedores para flujo;
- componentes de presentacion para vista y tabla cuando aplique;
- servicios para centralizar llamadas API y DTOs.

## 6. Consumo de API REST

La integracion con el backend cubre:

- autenticacion y registro;
- dashboard del estudiante;
- perfil del estudiante;
- registro y consulta de resenas;
- registro y consulta de solicitudes;
- listados publicos;
- CRUD administrativos;
- moderacion administrativa.

Criterios de calidad:

- los componentes no deben armar payloads complejos a mano;
- la paginacion debe venir del backend cuando corresponda;
- los filtros deben ser consistentes con query params o estado de vista;
- el frontend no debe duplicar logica critica ya resuelta por el backend.

## 7. Manejo de errores y experiencia de usuario

El frontend debe manejar adecuadamente:

- errores de validacion;
- errores de autenticacion;
- errores de autorizacion;
- conflictos funcionales;
- estados vacios;
- carga y espera de datos.

Practicas recomendadas:

- mensajes visibles y comprensibles;
- indicadores de carga en operaciones importantes;
- feedback de exito al registrar o actualizar;
- mensajes claros ante `401`, `403` y `409`;
- no exponer mensajes tecnicos internos al usuario final.

## 8. Experiencia segun rol

## 8.1. Estudiante

La experiencia del estudiante debe permitir:

- ingresar de forma simple;
- navegar por sus modulos principales;
- registrar resenas sin ambiguedad;
- visualizar estados de resenas y solicitudes;
- comprender si una accion fue aprobada, rechazada o esta pendiente.

Elementos UX utiles:

- badges por estado;
- mensajes de motivo de rechazo;
- acceso rapido a reenviar resena rechazada;
- filtros de consulta claros.

## 8.2. Administrador

La experiencia del administrador debe permitir:

- gestionar catalogos;
- moderar rapidamente;
- entender cuando una accion desactiva y no elimina;
- visualizar primero pendientes o registros mas relevantes.

Elementos UX utiles:

- tablas con acciones claras;
- confirmaciones antes de cambios sensibles;
- campo obligatorio de motivo al rechazar;
- orden de trabajo por prioridad o antiguedad.

## 9. Aporte del frontend al APF 03

Este documento respalda especificamente los criterios de:

- estructura del front-end;
- navegacion y comunicacion entre componentes;
- formularios, validaciones y manejo de errores;
- consumo de API REST e integracion con backend;
- autenticacion y autorizacion en frontend.

## 10. Aporte del frontend al proyecto final

El frontend aporta al resultado integral del sistema mediante:

- una capa visual alineada al alcance funcional;
- control de acceso por rol;
- integracion estable con backend desplegado;
- experiencia de usuario coherente con reglas academicas;
- soporte de demostracion funcional en despliegue real.

## 11. Conclusiones tecnicas

La logica del frontend fue pensada para que el sistema:

- navegue correctamente;
- consuma datos reales del backend;
- valide formularios antes de enviar;
- proteja vistas segun sesion y rol;
- brinde una experiencia clara tanto a estudiantes como a administradores.

Con ello, el frontend no solo cumple una funcion visual, sino que se convierte en una capa esencial para demostrar integracion, usabilidad y coherencia funcional en la entrega final.

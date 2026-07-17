# Observaciones sobre base de datos

Este documento registra observaciones tecnicas relevantes detectadas durante la revision del esquema, la validacion del mapeo JPA y las pruebas de arranque local. Su finalidad es dejar trazabilidad clara para sustentacion y mantenimiento.

## 1. Estado general de consistencia

A nivel de diseno, no se identificaron inconsistencias conceptuales graves entre:

- el esquema de referencia de la base de datos;
- las entidades del dominio;
- la intencion funcional del proyecto.

El modelo sigue siendo coherente para los flujos principales de:

- autenticacion;
- gestion academica;
- resenas;
- moderacion;
- solicitudes.

## 2. Limitaciones de verificacion en el entorno revisado

Durante la revision se tuvo en cuenta lo siguiente:

- el proyecto ya contempla soporte para pruebas con Testcontainers;
- dichas pruebas dependen de un runtime Docker disponible;
- en el entorno revisado esa validacion automatizada no siempre estuvo operativa;
- por ello, algunas verificaciones quedaron sujetas a entorno local o despliegue real.

Esto no invalida el diseno, pero si debe mencionarse con transparencia en una sustentacion tecnica.

## 3. Hallazgo tecnico en arranque local

En pruebas de arranque contra MySQL local se observo un punto relevante:

- la conexion JDBC si logra avanzar cuando la configuracion de acceso es correcta;
- superado ese punto, el siguiente bloqueo aparece en la fase `ddl-auto=validate`;
- el problema reportado corresponde a una discrepancia entre el schema existente y el mapeo esperado por Hibernate.

Detalle del hallazgo:

- tabla: `resena_calificacion`
- columna: `puntaje`
- tipo real reportado por MySQL: `tinyint`
- tipo esperado por Hibernate segun el mapeo actual: `integer`

## 4. Impacto del hallazgo

La implicancia tecnica es la siguiente:

- el problema no radica en la conectividad inicial;
- el conflicto aparece al validar la estructura de datos;
- mientras la discrepancia exista, el arranque puede detenerse en validacion de schema;
- esto afecta principalmente ejecucion local estricta y pruebas que dependan del mismo contrato exacto.

## 5. Interpretacion para sustentacion

Si este punto se menciona en exposicion, conviene explicarlo de esta forma:

- la arquitectura y el modelo de negocio estan definidos;
- la conexion con MySQL fue alcanzada;
- el hallazgo corresponde a un ajuste fino entre el tipo SQL real y el tipo esperado por JPA/Hibernate;
- el problema esta acotado y documentado, no es una inconsistencia total del proyecto.

## 6. Recomendacion de presentacion

Para una entrega final, este documento debe usarse como respaldo tecnico y no como foco principal de la demo. En la sustentacion conviene priorizar:

- despliegue funcional;
- flujos completos del sistema;
- seguridad y roles;
- integracion frontend-backend;
- alcance implementado.

Si se pregunta por riesgos o detalles tecnicos, esta observacion permite responder con claridad y trazabilidad.

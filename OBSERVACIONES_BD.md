# OBSERVACIONES_BD

## Estado actual

No se detectaron inconsistencias estructurales entre el mapeo JPA implementado y el schema de referencia utilizado para el diseño.

## Restricciones de verificación

- La validación automática con Testcontainers quedó preparada en el proyecto.
- En este entorno actual no hay Docker disponible, por lo que esas pruebas se ejecutan como `skipped` hasta contar con runtime Docker operativo.
- No se modificó el schema oficial para resolver esta limitación.

## Hallazgo en arranque real local

- Al ejecutar la aplicación contra MySQL local con `root` y contraseña vacía, la conexión JDBC sí avanza y el error de dialect deja de ser el bloqueo principal.
- El arranque queda detenido por `spring.jpa.hibernate.ddl-auto=validate` debido a una inconsistencia entre el schema existente y el mapeo JPA:
  - Tabla `resena_calificacion`
  - Columna `puntaje`
  - MySQL real reporta `tinyint`
  - Hibernate espera `integer`
- Este hallazgo se documenta sin alterar el schema, en cumplimiento de la restricción del proyecto.

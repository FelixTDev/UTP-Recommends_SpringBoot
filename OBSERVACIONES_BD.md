# OBSERVACIONES_BD

## Estado actual

No se detectaron inconsistencias estructurales entre el mapeo JPA implementado y el schema de referencia utilizado para el diseño.

## Restricciones de verificación

- La validación automática con Testcontainers quedó preparada en el proyecto.
- En este entorno actual no hay Docker disponible, por lo que esas pruebas se ejecutan como `skipped` hasta contar con runtime Docker operativo.
- No se modificó el schema oficial para resolver esta limitación.

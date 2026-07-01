package com.utp.recommends.estudiante.solicitud.dto.response;

import java.time.OffsetDateTime;

public record SolicitudResponse(
    Long id,
    String tipo,
    String estado,
    String nombreCursoSugerido,
    String nombreDocenteSugerido,
    String comentario,
    String motivoRechazo,
    OffsetDateTime fechaCreacion
) {
}

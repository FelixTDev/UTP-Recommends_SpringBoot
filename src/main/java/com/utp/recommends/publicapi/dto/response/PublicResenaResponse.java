package com.utp.recommends.publicapi.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record PublicResenaResponse(
    Long id,
    String curso,
    String docente,
    String comentario,
    boolean esAnonimo,
    String nombreEstudianteVisible,
    OffsetDateTime fechaCreacion,
    List<PublicResenaCalificacionResponse> calificaciones
) {
}

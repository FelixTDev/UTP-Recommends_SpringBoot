package com.utp.recommends.estudiante.resena.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record ResenaResponse(
    Long id,
    Long cursoDocenteId,
    String curso,
    String docente,
    String comentario,
    boolean esAnonimo,
    String estado,
    Integer version,
    String motivoRechazo,
    OffsetDateTime fechaCreacion,
    List<ResenaCalificacionResponse> calificaciones
) {
}

package com.utp.recommends.estudiante.resena.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ResenaCreateRequest(
    @NotNull Long cursoDocenteId,
    @NotBlank String comentario,
    boolean esAnonimo,
    @NotEmpty List<@Valid CriterioPuntajeRequest> calificaciones
) {
}

package com.utp.recommends.estudiante.resena.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CriterioPuntajeRequest(
    @NotNull Long criterioId,
    @NotNull @Min(1) @Max(5) Integer puntaje
) {
}

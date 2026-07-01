package com.utp.recommends.admin.criterio.dto.request;

import com.utp.recommends.domain.enums.EstadoSimple;
import jakarta.validation.constraints.NotNull;

public record CriterioEstadoRequest(@NotNull EstadoSimple estado) {
}

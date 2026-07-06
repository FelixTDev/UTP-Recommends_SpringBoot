package com.utp.recommends.admin.curso.dto.request;

import com.utp.recommends.domain.enums.EstadoSimple;
import jakarta.validation.constraints.NotNull;

public record CursoEstadoRequest(@NotNull EstadoSimple estado) {
}

package com.utp.recommends.admin.docente.dto.request;

import com.utp.recommends.domain.enums.EstadoSimple;
import jakarta.validation.constraints.NotNull;

public record DocenteEstadoRequest(@NotNull EstadoSimple estado) {
}

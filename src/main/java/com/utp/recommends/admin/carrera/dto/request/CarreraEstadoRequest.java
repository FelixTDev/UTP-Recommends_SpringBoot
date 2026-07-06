package com.utp.recommends.admin.carrera.dto.request;

import com.utp.recommends.domain.enums.EstadoCarrera;
import jakarta.validation.constraints.NotNull;

public record CarreraEstadoRequest(@NotNull EstadoCarrera estado) {
}

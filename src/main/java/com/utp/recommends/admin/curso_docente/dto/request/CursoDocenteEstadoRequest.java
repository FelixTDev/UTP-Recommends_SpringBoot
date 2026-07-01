package com.utp.recommends.admin.curso_docente.dto.request;

import com.utp.recommends.domain.enums.EstadoSimple;
import jakarta.validation.constraints.NotNull;

public record CursoDocenteEstadoRequest(@NotNull EstadoSimple estado) {
}

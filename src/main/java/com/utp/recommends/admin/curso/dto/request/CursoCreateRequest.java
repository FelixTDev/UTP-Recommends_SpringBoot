package com.utp.recommends.admin.curso.dto.request;

import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.TipoCurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CursoCreateRequest(
    @NotBlank String nombre,
    @NotNull TipoCurso tipo,
    Long carreraId,
    EstadoSimple estado
) {
}

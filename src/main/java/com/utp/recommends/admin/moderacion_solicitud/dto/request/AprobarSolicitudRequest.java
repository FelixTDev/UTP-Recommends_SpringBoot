package com.utp.recommends.admin.moderacion_solicitud.dto.request;

import com.utp.recommends.domain.enums.TipoCurso;
import com.utp.recommends.estudiante.resena.dto.request.CriterioPuntajeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AprobarSolicitudRequest(
    TipoCurso tipoCurso,
    Long carreraId,
    Long cursoExistenteId,
    Long docenteExistenteId,
    @NotEmpty List<@Valid CriterioPuntajeRequest> calificaciones
) {
}

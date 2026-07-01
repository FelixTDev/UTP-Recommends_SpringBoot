package com.utp.recommends.estudiante.solicitud.dto.request;

import com.utp.recommends.domain.enums.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SolicitudCreateRequest(
    @NotNull TipoSolicitud tipo,
    String nombreCursoSugerido,
    Long carreraSugeridaId,
    String nombreDocenteSugerido,
    @NotBlank String comentario
) {
}

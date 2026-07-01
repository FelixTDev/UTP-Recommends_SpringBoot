package com.utp.recommends.admin.moderacion_solicitud.dto.response;

import java.time.OffsetDateTime;

public record ModeracionSolicitudResponse(
    Long idSolicitud,
    String tipo,
    String estado,
    OffsetDateTime fechaCreacion,
    String comentario,
    StudentSummary estudiante,
    RequestedData requestedData,
    Long resenaGeneradaId,
    String motivoRechazo
) {
    public record StudentSummary(Long id, String nombreCompleto, String correo, Long carreraId, String carreraNombre) {
    }

    public record RequestedData(
        String nombreCursoSugerido,
        Long carreraSugeridaId,
        String carreraSugeridaNombre,
        String nombreDocenteSugerido
    ) {
    }
}

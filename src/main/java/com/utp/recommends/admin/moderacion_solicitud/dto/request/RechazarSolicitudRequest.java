package com.utp.recommends.admin.moderacion_solicitud.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RechazarSolicitudRequest(@NotBlank String motivoRechazo) {
}

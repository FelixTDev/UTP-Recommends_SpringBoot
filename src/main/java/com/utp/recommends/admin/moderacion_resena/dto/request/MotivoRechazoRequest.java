package com.utp.recommends.admin.moderacion_resena.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MotivoRechazoRequest(@NotBlank String motivoRechazo) {
}

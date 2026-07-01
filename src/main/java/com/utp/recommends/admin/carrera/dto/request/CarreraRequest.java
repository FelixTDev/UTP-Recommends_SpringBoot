package com.utp.recommends.admin.carrera.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CarreraRequest(@NotBlank String nombre) {
}

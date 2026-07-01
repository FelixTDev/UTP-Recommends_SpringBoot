package com.utp.recommends.admin.criterio.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CriterioRequest(@NotBlank String nombre, String descripcion) {
}

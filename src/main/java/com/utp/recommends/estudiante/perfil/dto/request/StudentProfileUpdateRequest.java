package com.utp.recommends.estudiante.perfil.dto.request;

import com.utp.recommends.common.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StudentProfileUpdateRequest(
    @NotBlank
    @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Nombres inválidos")
    String nombres,
    @NotBlank
    @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Apellidos inválidos")
    String apellidos
) {
}

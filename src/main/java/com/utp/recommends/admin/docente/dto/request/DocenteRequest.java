package com.utp.recommends.admin.docente.dto.request;

import com.utp.recommends.common.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DocenteRequest(
    @NotBlank @Pattern(regexp = ValidationPatterns.PERSON_NAME) String nombres,
    @NotBlank @Pattern(regexp = ValidationPatterns.PERSON_NAME) String apellidos,
    String email
) {
}

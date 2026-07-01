package com.utp.recommends.auth.dto.request;

import com.utp.recommends.common.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank
    @Pattern(regexp = ValidationPatterns.UTP_STUDENT_EMAIL, message = "El correo debe tener formato U########@utp.edu.pe")
    String email,
    @NotBlank
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = "La contraseña no cumple la política requerida")
    String password,
    @NotBlank
    @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Nombres inválidos")
    String nombres,
    @NotBlank
    @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Apellidos inválidos")
    String apellidos,
    @NotNull
    Long carreraId
) {
}

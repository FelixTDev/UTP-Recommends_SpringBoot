package com.utp.recommends.admin.usuario.dto.request;

import com.utp.recommends.common.validation.ValidationPatterns;
import com.utp.recommends.domain.enums.EstadoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UsuarioUpdateRequest(
    @NotBlank @Pattern(regexp = ValidationPatterns.PERSON_NAME) String nombres,
    @NotBlank @Pattern(regexp = ValidationPatterns.PERSON_NAME) String apellidos
) {
}

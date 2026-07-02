package com.utp.recommends.admin.usuario.dto.request;

import com.utp.recommends.common.validation.ValidationPatterns;
import com.utp.recommends.domain.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UsuarioCreateRequest(
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = ValidationPatterns.PASSWORD) String password,
    @NotBlank @Pattern(regexp = ValidationPatterns.PERSON_NAME) String nombres,
    @NotBlank @Pattern(regexp = ValidationPatterns.PERSON_NAME) String apellidos,
    @NotNull RolUsuario rol,
    Long carreraId
) {
}

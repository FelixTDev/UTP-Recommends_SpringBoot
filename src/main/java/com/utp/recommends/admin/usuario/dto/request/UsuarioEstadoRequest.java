package com.utp.recommends.admin.usuario.dto.request;

import com.utp.recommends.domain.enums.EstadoUsuario;
import jakarta.validation.constraints.NotNull;

public record UsuarioEstadoRequest(@NotNull EstadoUsuario estado) {
}

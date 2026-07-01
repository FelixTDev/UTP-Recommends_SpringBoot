package com.utp.recommends.admin.usuario.dto.response;

public record UsuarioResponse(
    Long id,
    String email,
    String nombres,
    String apellidos,
    String rol,
    String estado
) {
}

package com.utp.recommends.auth.dto.response;

public record CurrentUserResponse(
    Long userId,
    String email,
    String rol,
    String estado,
    String nombres,
    String apellidos
) {
}

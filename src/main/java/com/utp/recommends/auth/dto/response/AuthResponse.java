package com.utp.recommends.auth.dto.response;

public record AuthResponse(
    String token,
    String tokenType,
    long expiresInMinutes,
    String rol,
    Long userId,
    String nombreCompleto
) {
}

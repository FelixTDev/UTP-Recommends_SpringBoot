package com.utp.recommends.estudiante.perfil.dto.response;

public record StudentProfileResponse(
    Long estudianteId,
    Long usuarioId,
    String email,
    String rol,
    String estado,
    String codigoEstudiante,
    Long carreraId,
    String carreraNombre,
    String nombres,
    String apellidos
) {
}

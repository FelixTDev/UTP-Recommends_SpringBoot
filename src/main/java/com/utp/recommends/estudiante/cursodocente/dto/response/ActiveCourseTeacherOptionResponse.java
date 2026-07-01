package com.utp.recommends.estudiante.cursodocente.dto.response;

public record ActiveCourseTeacherOptionResponse(
    Long idCursoDocente,
    Long idCurso,
    String nombreCurso,
    String codigoCurso,
    String tipoCurso,
    Long carreraId,
    String carreraNombre,
    Long idDocente,
    String nombreDocente,
    String estado
) {
}

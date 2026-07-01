package com.utp.recommends.admin.moderacion_resena.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record ModeracionResenaResponse(
    Long idResena,
    String estado,
    String comentario,
    OffsetDateTime fechaCreacion,
    boolean esAnonimo,
    StudentSummary estudiante,
    CourseSummary curso,
    TeacherSummary docente,
    List<ScoreSummary> calificaciones,
    String motivoRechazo
) {
    public record StudentSummary(Long id, String nombreCompleto, String correo, Long carreraId, String carreraNombre) {
    }

    public record CourseSummary(Long id, String nombre, String tipoCurso, Long carreraId, String carreraNombre) {
    }

    public record TeacherSummary(Long id, String nombreCompleto) {
    }

    public record ScoreSummary(Long criterioId, String criterio, Integer puntaje) {
    }
}

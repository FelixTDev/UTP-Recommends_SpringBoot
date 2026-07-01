package com.utp.recommends.estudiante.dashboard.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record StudentDashboardResponse(
    long totalResenas,
    long resenasPendientes,
    long resenasAprobadas,
    long resenasRechazadas,
    long totalSolicitudes,
    List<RecentReviewItem> ultimasResenas,
    List<RecentRequestItem> ultimasSolicitudes
) {
    public record RecentReviewItem(
        Long id,
        String curso,
        String docente,
        String estado,
        OffsetDateTime fechaCreacion
    ) {
    }

    public record RecentRequestItem(
        Long id,
        String tipo,
        String estado,
        OffsetDateTime fechaCreacion
    ) {
    }
}

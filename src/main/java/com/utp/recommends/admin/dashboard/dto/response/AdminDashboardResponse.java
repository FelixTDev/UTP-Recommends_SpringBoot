package com.utp.recommends.admin.dashboard.dto.response;

public record AdminDashboardResponse(
    long resenasPendientes,
    long solicitudesPendientes,
    long usuariosActivos,
    long cursosActivos,
    long docentesActivos,
    long criteriosActivos
) {
}

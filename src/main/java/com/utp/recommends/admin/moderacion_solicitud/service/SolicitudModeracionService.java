package com.utp.recommends.admin.moderacion_solicitud.service;

import com.utp.recommends.admin.moderacion_solicitud.dto.request.AprobarSolicitudRequest;
import com.utp.recommends.admin.moderacion_solicitud.dto.response.ModeracionSolicitudResponse;
import java.util.List;

public interface SolicitudModeracionService {
    List<ModeracionSolicitudResponse> pendientes();
    ModeracionSolicitudResponse aprobar(Long solicitudId, AprobarSolicitudRequest request);
    ModeracionSolicitudResponse rechazar(Long solicitudId, String motivoRechazo);
}

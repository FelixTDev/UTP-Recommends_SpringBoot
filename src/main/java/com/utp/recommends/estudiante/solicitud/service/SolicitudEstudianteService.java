package com.utp.recommends.estudiante.solicitud.service;

import com.utp.recommends.estudiante.solicitud.dto.request.SolicitudCreateRequest;
import com.utp.recommends.estudiante.solicitud.dto.response.SolicitudResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SolicitudEstudianteService {
    SolicitudResponse crear(SolicitudCreateRequest request);
    Page<SolicitudResponse> listarMisSolicitudes(Pageable pageable);
    SolicitudResponse obtenerMiSolicitud(Long id);
}

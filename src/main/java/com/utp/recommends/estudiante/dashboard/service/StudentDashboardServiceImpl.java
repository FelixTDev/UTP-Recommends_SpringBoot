package com.utp.recommends.estudiante.dashboard.service;

import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.enums.EstadoResena;
import com.utp.recommends.domain.enums.EstadoSolicitud;
import com.utp.recommends.estudiante.dashboard.dto.response.StudentDashboardResponse;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.repository.SolicitudRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private static final PageRequest RECENT_ITEMS = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "fechaCreacion"));

    private final AuthenticatedUserService authenticatedUserService;
    private final ResenaRepository resenaRepository;
    private final SolicitudRepository solicitudRepository;

    public StudentDashboardServiceImpl(
        AuthenticatedUserService authenticatedUserService,
        ResenaRepository resenaRepository,
        SolicitudRepository solicitudRepository
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.resenaRepository = resenaRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponse getDashboard() {
        Estudiante estudiante = authenticatedUserService.getCurrentEstudiante();
        Long estudianteId = estudiante.getId();
        return new StudentDashboardResponse(
            resenaRepository.countByEstudianteId(estudianteId),
            resenaRepository.countByEstudianteIdAndEstado(estudianteId, EstadoResena.PENDIENTE),
            resenaRepository.countByEstudianteIdAndEstado(estudianteId, EstadoResena.APROBADA),
            resenaRepository.countByEstudianteIdAndEstado(estudianteId, EstadoResena.RECHAZADA),
            solicitudRepository.countByEstudianteId(estudianteId),
            resenaRepository.findByEstudianteId(estudianteId, RECENT_ITEMS).stream()
                .map(r -> new StudentDashboardResponse.RecentReviewItem(
                    r.getId(),
                    r.getCursoDocente().getCurso().getNombre(),
                    r.getCursoDocente().getDocente().getNombres() + " " + r.getCursoDocente().getDocente().getApellidos(),
                    r.getEstado().name(),
                    r.getFechaCreacion()
                ))
                .toList(),
            solicitudRepository.findByEstudianteId(estudianteId, RECENT_ITEMS).stream()
                .map(s -> new StudentDashboardResponse.RecentRequestItem(
                    s.getId(),
                    s.getTipo().name(),
                    s.getEstado().name(),
                    s.getFechaCreacion()
                ))
                .toList()
        );
    }
}

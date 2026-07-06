package com.utp.recommends.estudiante.solicitud.service;

import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.common.exception.ResourceNotFoundException;
import com.utp.recommends.common.validation.SuggestedTeacherName;
import com.utp.recommends.common.validation.SuggestedTeacherNameParser;
import com.utp.recommends.domain.entity.Solicitud;
import com.utp.recommends.domain.enums.EstadoSolicitud;
import com.utp.recommends.domain.enums.TipoSolicitud;
import com.utp.recommends.estudiante.solicitud.dto.request.SolicitudCreateRequest;
import com.utp.recommends.estudiante.solicitud.dto.response.SolicitudResponse;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.SolicitudRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SolicitudEstudianteServiceImpl implements SolicitudEstudianteService {

    private final SolicitudRepository solicitudRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final CarreraRepository carreraRepository;

    public SolicitudEstudianteServiceImpl(
        SolicitudRepository solicitudRepository,
        AuthenticatedUserService authenticatedUserService,
        CarreraRepository carreraRepository
    ) {
        this.solicitudRepository = solicitudRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.carreraRepository = carreraRepository;
    }

    @Override
    public SolicitudResponse crear(SolicitudCreateRequest request) {
        if (request.comentario().trim().length() < 10) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El comentario debe tener al menos 10 caracteres");
        }
        if (request.tipo() == TipoSolicitud.CURSO_NUEVO && (request.nombreCursoSugerido() == null || request.nombreCursoSugerido().isBlank())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "nombreCursoSugerido es obligatorio");
        }
        SuggestedTeacherName docenteSugerido = null;
        if (request.tipo() == TipoSolicitud.DOCENTE_NUEVO || request.tipo() == TipoSolicitud.AMBOS) {
            docenteSugerido = SuggestedTeacherNameParser.fromRequestFields(
                request.nombresDocenteSugerido(),
                request.apellidosDocenteSugerido(),
                request.nombreDocenteSugerido(),
                "docente sugerido"
            );
        }
        if (request.tipo() == TipoSolicitud.AMBOS &&
            (request.nombreCursoSugerido() == null || request.nombreCursoSugerido().isBlank())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar curso y docente sugeridos");
        }
        Solicitud solicitud = new Solicitud();
        solicitud.setEstudiante(authenticatedUserService.getCurrentEstudiante());
        solicitud.setTipo(request.tipo());
        solicitud.setNombreCursoSugerido(request.nombreCursoSugerido());
        solicitud.setNombreDocenteSugerido(docenteSugerido == null ? null : docenteSugerido.toLegacyValue());
        solicitud.setComentario(request.comentario());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        if (request.carreraSugeridaId() != null) {
            solicitud.setCarreraSugerida(carreraRepository.findById(request.carreraSugeridaId()).orElse(null));
        }
        return toResponse(solicitudRepository.save(solicitud));
    }

    @Override
    public Page<SolicitudResponse> listarMisSolicitudes(Pageable pageable) {
        return solicitudRepository.findByEstudianteId(authenticatedUserService.getCurrentEstudiante().getId(), pageable).map(this::toResponse);
    }

    @Override
    public SolicitudResponse obtenerMiSolicitud(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
            .filter(s -> s.getEstudiante().getId().equals(authenticatedUserService.getCurrentEstudiante().getId()))
            .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        return toResponse(solicitud);
    }

    private SolicitudResponse toResponse(Solicitud solicitud) {
        return new SolicitudResponse(
            solicitud.getId(),
            solicitud.getTipo().name(),
            solicitud.getEstado().name(),
            solicitud.getNombreCursoSugerido(),
            solicitud.getNombreDocenteSugerido(),
            splitTeacherName(solicitud.getNombreDocenteSugerido(), true),
            splitTeacherName(solicitud.getNombreDocenteSugerido(), false),
            solicitud.getComentario(),
            solicitud.getMotivoRechazo(),
            solicitud.getFechaCreacion()
        );
    }

    private String splitTeacherName(String value, boolean firstPart) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            SuggestedTeacherName parsed = SuggestedTeacherNameParser.fromLegacyValue(value, "docente sugerido");
            return firstPart ? parsed.nombres() : parsed.apellidos();
        } catch (BusinessException ex) {
            return null;
        }
    }
}

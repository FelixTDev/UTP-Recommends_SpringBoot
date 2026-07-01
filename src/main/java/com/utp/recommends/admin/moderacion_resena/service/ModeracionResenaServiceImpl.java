package com.utp.recommends.admin.moderacion_resena.service;

import com.utp.recommends.admin.moderacion_resena.dto.response.ModeracionResenaResponse;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.Resena;
import com.utp.recommends.domain.entity.ResenaCalificacion;
import com.utp.recommends.domain.enums.EstadoResena;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModeracionResenaServiceImpl implements ModeracionResenaService {
    private final ResenaRepository resenaRepository;
    private final AuthenticatedUserService authenticatedUserService;
    public ModeracionResenaServiceImpl(ResenaRepository resenaRepository, AuthenticatedUserService authenticatedUserService) {
        this.resenaRepository = resenaRepository;
        this.authenticatedUserService = authenticatedUserService;
    }
    @Override @Transactional(readOnly = true) public List<ModeracionResenaResponse> pendientes() { return resenaRepository.findByEstadoOrderByFechaCreacionAsc(EstadoResena.PENDIENTE).stream().map(this::toResponse).toList(); }
    @Override @Transactional public ModeracionResenaResponse aprobar(Long id) { Resena r = requirePendiente(id); r.setEstado(EstadoResena.APROBADA); r.setAdminModerador(authenticatedUserService.getCurrentUsuario()); r.setFechaModeracion(OffsetDateTime.now()); return toResponse(resenaRepository.save(r)); }
    @Override @Transactional public ModeracionResenaResponse rechazar(Long id, String motivoRechazo) { if (motivoRechazo == null || motivoRechazo.isBlank()) throw new BusinessException(HttpStatus.BAD_REQUEST, "El motivo de rechazo es obligatorio"); Resena r = requirePendiente(id); r.setEstado(EstadoResena.RECHAZADA); r.setMotivoRechazo(motivoRechazo); r.setAdminModerador(authenticatedUserService.getCurrentUsuario()); r.setFechaModeracion(OffsetDateTime.now()); return toResponse(resenaRepository.save(r)); }
    @Override @Transactional public ModeracionResenaResponse ocultar(Long id) { Resena r = resenaRepository.findById(id).orElseThrow(); if (r.getEstado() != EstadoResena.APROBADA) throw new BusinessException(HttpStatus.CONFLICT, "Solo se puede ocultar una reseña aprobada"); r.setEstado(EstadoResena.OCULTA); r.setAdminModerador(authenticatedUserService.getCurrentUsuario()); r.setFechaModeracion(OffsetDateTime.now()); return toResponse(resenaRepository.save(r)); }
    private Resena requirePendiente(Long id) { Resena r = resenaRepository.findById(id).orElseThrow(); if (r.getEstado() != EstadoResena.PENDIENTE) throw new BusinessException(HttpStatus.CONFLICT, "La reseña ya fue moderada"); return r; }
    private ModeracionResenaResponse toResponse(Resena r) {
        return new ModeracionResenaResponse(
            r.getId(),
            r.getEstado().name(),
            r.getComentario(),
            r.getFechaCreacion(),
            r.isEsAnonimo(),
            new ModeracionResenaResponse.StudentSummary(
                r.getEstudiante().getId(),
                r.getEstudiante().getUsuario().getNombres() + " " + r.getEstudiante().getUsuario().getApellidos(),
                r.getEstudiante().getUsuario().getEmail(),
                r.getEstudiante().getCarrera().getId(),
                r.getEstudiante().getCarrera().getNombre()
            ),
            new ModeracionResenaResponse.CourseSummary(
                r.getCursoDocente().getCurso().getId(),
                r.getCursoDocente().getCurso().getNombre(),
                r.getCursoDocente().getCurso().getTipo().name(),
                r.getCursoDocente().getCurso().getCarrera() == null ? null : r.getCursoDocente().getCurso().getCarrera().getId(),
                r.getCursoDocente().getCurso().getCarrera() == null ? null : r.getCursoDocente().getCurso().getCarrera().getNombre()
            ),
            new ModeracionResenaResponse.TeacherSummary(
                r.getCursoDocente().getDocente().getId(),
                r.getCursoDocente().getDocente().getNombres() + " " + r.getCursoDocente().getDocente().getApellidos()
            ),
            r.getCalificaciones().stream().map(this::toScoreSummary).toList(),
            r.getMotivoRechazo()
        );
    }

    private ModeracionResenaResponse.ScoreSummary toScoreSummary(ResenaCalificacion calificacion) {
        return new ModeracionResenaResponse.ScoreSummary(
            calificacion.getCriterio().getId(),
            calificacion.getCriterio().getNombre(),
            calificacion.getPuntaje().intValue()
        );
    }
}

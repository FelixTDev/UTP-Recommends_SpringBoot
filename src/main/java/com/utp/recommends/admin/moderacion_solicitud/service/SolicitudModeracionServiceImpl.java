package com.utp.recommends.admin.moderacion_solicitud.service;

import com.utp.recommends.admin.moderacion_solicitud.dto.request.AprobarSolicitudRequest;
import com.utp.recommends.admin.moderacion_solicitud.dto.response.ModeracionSolicitudResponse;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.CriterioCalificacion;
import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.domain.entity.Docente;
import com.utp.recommends.domain.entity.Resena;
import com.utp.recommends.domain.entity.ResenaCalificacion;
import com.utp.recommends.domain.entity.Solicitud;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.domain.enums.EstadoResena;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.EstadoSolicitud;
import com.utp.recommends.domain.enums.TipoCurso;
import com.utp.recommends.domain.enums.TipoSolicitud;
import com.utp.recommends.estudiante.resena.dto.request.CriterioPuntajeRequest;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import com.utp.recommends.repository.CursoDocenteRepository;
import com.utp.recommends.repository.CursoRepository;
import com.utp.recommends.repository.DocenteRepository;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.repository.SolicitudRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudModeracionServiceImpl implements SolicitudModeracionService {
    private final SolicitudRepository solicitudRepository;
    private final CriterioCalificacionRepository criterioRepository;
    private final CarreraRepository carreraRepository;
    private final CursoRepository cursoRepository;
    private final DocenteRepository docenteRepository;
    private final CursoDocenteRepository cursoDocenteRepository;
    private final ResenaRepository resenaRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public SolicitudModeracionServiceImpl(
        SolicitudRepository solicitudRepository,
        CriterioCalificacionRepository criterioRepository,
        CarreraRepository carreraRepository,
        CursoRepository cursoRepository,
        DocenteRepository docenteRepository,
        CursoDocenteRepository cursoDocenteRepository,
        ResenaRepository resenaRepository,
        AuthenticatedUserService authenticatedUserService
    ) {
        this.solicitudRepository = solicitudRepository;
        this.criterioRepository = criterioRepository;
        this.carreraRepository = carreraRepository;
        this.cursoRepository = cursoRepository;
        this.docenteRepository = docenteRepository;
        this.cursoDocenteRepository = cursoDocenteRepository;
        this.resenaRepository = resenaRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeracionSolicitudResponse> pendientes() {
        return solicitudRepository.findByEstadoOrderByFechaCreacionAsc(EstadoSolicitud.PENDIENTE).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ModeracionSolicitudResponse aprobar(Long solicitudId, AprobarSolicitudRequest request) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId).orElseThrow();
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new BusinessException(HttpStatus.CONFLICT, "La solicitud ya fue resuelta");
        }

        List<CriterioCalificacion> criteriosActivos = criterioRepository.findByEstado(EstadoSimple.ACTIVO);
        validateScores(request.calificaciones(), criteriosActivos);
        validateSolicitudComment(solicitud.getComentario());

        Docente docente = resolveDocente(solicitud, request);
        Curso curso = resolveCurso(solicitud, request);
        CursoDocente cursoDocente = cursoDocenteRepository.findByCursoIdAndDocenteId(curso.getId(), docente.getId()).orElseGet(() -> {
            CursoDocente created = new CursoDocente();
            created.setCurso(curso);
            created.setDocente(docente);
            created.setEstado(EstadoSimple.ACTIVO);
            return cursoDocenteRepository.save(created);
        });

        Resena resena = new Resena();
        resena.setEstudiante(solicitud.getEstudiante());
        resena.setCursoDocente(cursoDocente);
        resena.setComentario(solicitud.getComentario());
        resena.setEsAnonimo(false);
        resena.setEstado(EstadoResena.APROBADA);
        resena.setVersion(1);
        resena.setAdminModerador(authenticatedUserService.getCurrentUsuario());
        resena.setFechaModeracion(OffsetDateTime.now());

        Map<Long, CriterioCalificacion> criteriosPorId = criteriosActivos.stream().collect(Collectors.toMap(CriterioCalificacion::getId, Function.identity()));
        for (CriterioPuntajeRequest puntaje : request.calificaciones()) {
            ResenaCalificacion calificacion = new ResenaCalificacion();
            calificacion.setResena(resena);
            calificacion.setCriterio(criteriosPorId.get(puntaje.criterioId()));
            calificacion.setPuntaje(puntaje.puntaje().byteValue());
            resena.getCalificaciones().add(calificacion);
        }
        resena = resenaRepository.save(resena);

        solicitud.setResenaGenerada(resena);
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setAdmin(authenticatedUserService.getCurrentUsuario());
        solicitud.setFechaResolucion(OffsetDateTime.now());
        solicitudRepository.save(solicitud);
        return toResponse(solicitud);
    }

    @Override
    @Transactional
    public ModeracionSolicitudResponse rechazar(Long solicitudId, String motivoRechazo) {
        if (motivoRechazo == null || motivoRechazo.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El motivo de rechazo es obligatorio");
        }
        Solicitud solicitud = solicitudRepository.findById(solicitudId).orElseThrow();
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new BusinessException(HttpStatus.CONFLICT, "La solicitud ya fue resuelta");
        }
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(motivoRechazo);
        solicitud.setAdmin(authenticatedUserService.getCurrentUsuario());
        solicitud.setFechaResolucion(OffsetDateTime.now());
        return toResponse(solicitudRepository.save(solicitud));
    }

    private Docente resolveDocente(Solicitud solicitud, AprobarSolicitudRequest request) {
        if (solicitud.getTipo() == TipoSolicitud.CURSO_NUEVO) {
            return docenteRepository.findByIdAndEstado(request.docenteExistenteId(), EstadoSimple.ACTIVO)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar un docente activo para aprobar una solicitud CURSO_NUEVO"));
        }
        if (solicitud.getTipo() == TipoSolicitud.DOCENTE_NUEVO || solicitud.getTipo() == TipoSolicitud.AMBOS) {
            Docente docente = new Docente();
            docente.setNombres(solicitud.getNombreDocenteSugerido().trim());
            docente.setApellidos("Sin Apellido");
            docente.setEstado(EstadoSimple.ACTIVO);
            return docenteRepository.save(docente);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Tipo de solicitud no soportado");
    }

    private Curso resolveCurso(Solicitud solicitud, AprobarSolicitudRequest request) {
        if (solicitud.getTipo() == TipoSolicitud.DOCENTE_NUEVO) {
            return cursoRepository.findByIdAndEstado(request.cursoExistenteId(), EstadoSimple.ACTIVO)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar un curso activo para aprobar una solicitud DOCENTE_NUEVO"));
        }
        Curso curso = new Curso();
        curso.setNombre(solicitud.getNombreCursoSugerido().trim());
        TipoCurso tipoCurso = request.tipoCurso() == null ? TipoCurso.GENERAL : request.tipoCurso();
        curso.setTipo(tipoCurso);
        curso.setEstado(EstadoSimple.ACTIVO);
        if (tipoCurso == TipoCurso.CARRERA) {
            if (request.carreraId() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar una carrera activa");
            }
            Carrera carrera = carreraRepository.findById(request.carreraId())
                .filter(c -> c.getEstado() == EstadoCarrera.ACTIVA)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar una carrera activa"));
            curso.setCarrera(carrera);
        }
        return cursoRepository.save(curso);
    }

    private void validateSolicitudComment(String comentario) {
        if (comentario == null || comentario.trim().length() < 10) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La solicitud debe tener un comentario válido para generar una reseña aprobada");
        }
    }

    private void validateScores(List<CriterioPuntajeRequest> scores, List<CriterioCalificacion> activeCriteria) {
        if (scores == null || scores.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Las calificaciones son obligatorias");
        }
        var requiredIds = activeCriteria.stream().map(CriterioCalificacion::getId).collect(Collectors.toSet());
        var providedIds = scores.stream().map(CriterioPuntajeRequest::criterioId).collect(Collectors.toSet());
        if (scores.size() != activeCriteria.size() || providedIds.size() != activeCriteria.size() || !requiredIds.equals(providedIds)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Debe existir exactamente una calificación por cada criterio activo");
        }
    }

    private ModeracionSolicitudResponse toResponse(Solicitud solicitud) {
        return new ModeracionSolicitudResponse(
            solicitud.getId(),
            solicitud.getTipo().name(),
            solicitud.getEstado().name(),
            solicitud.getFechaCreacion(),
            solicitud.getComentario(),
            new ModeracionSolicitudResponse.StudentSummary(
                solicitud.getEstudiante().getId(),
                solicitud.getEstudiante().getUsuario().getNombres() + " " + solicitud.getEstudiante().getUsuario().getApellidos(),
                solicitud.getEstudiante().getUsuario().getEmail(),
                solicitud.getEstudiante().getCarrera().getId(),
                solicitud.getEstudiante().getCarrera().getNombre()
            ),
            new ModeracionSolicitudResponse.RequestedData(
                solicitud.getNombreCursoSugerido(),
                solicitud.getCarreraSugerida() == null ? null : solicitud.getCarreraSugerida().getId(),
                solicitud.getCarreraSugerida() == null ? null : solicitud.getCarreraSugerida().getNombre(),
                solicitud.getNombreDocenteSugerido()
            ),
            solicitud.getResenaGenerada() == null ? null : solicitud.getResenaGenerada().getId(),
            solicitud.getMotivoRechazo()
        );
    }
}

package com.utp.recommends.estudiante.resena.service;

import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.common.exception.ResourceNotFoundException;
import com.utp.recommends.domain.entity.CriterioCalificacion;
import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.entity.Resena;
import com.utp.recommends.domain.entity.ResenaCalificacion;
import com.utp.recommends.domain.enums.EstadoResena;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.estudiante.resena.dto.request.CriterioPuntajeRequest;
import com.utp.recommends.estudiante.resena.dto.request.ResenaCreateRequest;
import com.utp.recommends.estudiante.resena.dto.response.ResenaCalificacionResponse;
import com.utp.recommends.estudiante.resena.dto.response.ResenaResponse;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import com.utp.recommends.repository.CursoDocenteRepository;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResenaEstudianteServiceImpl implements ResenaEstudianteService {

    private final AuthenticatedUserService authenticatedUserService;
    private final CursoDocenteRepository cursoDocenteRepository;
    private final CriterioCalificacionRepository criterioRepository;
    private final ResenaRepository resenaRepository;

    public ResenaEstudianteServiceImpl(
        AuthenticatedUserService authenticatedUserService,
        CursoDocenteRepository cursoDocenteRepository,
        CriterioCalificacionRepository criterioRepository,
        ResenaRepository resenaRepository
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.cursoDocenteRepository = cursoDocenteRepository;
        this.criterioRepository = criterioRepository;
        this.resenaRepository = resenaRepository;
    }

    @Override
    @Transactional
    public ResenaResponse crear(ResenaCreateRequest request) {
        if (request.comentario().trim().length() < 10) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El comentario debe tener al menos 10 caracteres");
        }

        Estudiante estudiante = authenticatedUserService.getCurrentEstudiante();
        CursoDocente cursoDocente = cursoDocenteRepository.findByIdAndEstado(request.cursoDocenteId(), EstadoSimple.ACTIVO)
            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "La relación curso-docente no existe o está inactiva"));

        List<CriterioCalificacion> criteriosActivos = criterioRepository.findByEstado(EstadoSimple.ACTIVO);
        validateCriteria(request.calificaciones(), criteriosActivos);

        Resena resena = resenaRepository.findActiveByStudentAndCursoDocente(
                estudiante.getId(),
                cursoDocente.getId(),
                EnumSet.of(EstadoResena.PENDIENTE, EstadoResena.APROBADA)
            )
            .map(existing -> {
                if (existing.getEstado() == EstadoResena.APROBADA) {
                    throw new BusinessException(HttpStatus.CONFLICT, "Ya tienes una reseña aprobada para este curso y docente");
                }
                existing.getCalificaciones().clear();
                existing.setComentario(request.comentario());
                existing.setEsAnonimo(request.esAnonimo());
                return existing;
            })
            .orElseGet(() -> buildNewReview(estudiante, cursoDocente));

        if (resena.getId() == null) {
            Resena ultima = resenaRepository.findTopByEstudianteIdAndCursoDocenteIdOrderByVersionDesc(estudiante.getId(), cursoDocente.getId()).orElse(null);
            if (ultima != null && ultima.getEstado() == EstadoResena.RECHAZADA) {
                resena.setVersion(ultima.getVersion() + 1);
                resena.setResenaAnterior(ultima);
            } else if (resena.getVersion() == null) {
                resena.setVersion(1);
            }
            resena.setComentario(request.comentario());
            resena.setEsAnonimo(request.esAnonimo());
            resena.setEstado(EstadoResena.PENDIENTE);
        }

        Map<Long, CriterioCalificacion> criteriosPorId = criteriosActivos.stream().collect(Collectors.toMap(CriterioCalificacion::getId, Function.identity()));
        for (CriterioPuntajeRequest item : request.calificaciones()) {
            ResenaCalificacion calificacion = new ResenaCalificacion();
            calificacion.setResena(resena);
            calificacion.setCriterio(criteriosPorId.get(item.criterioId()));
            calificacion.setPuntaje(item.puntaje().byteValue());
            resena.getCalificaciones().add(calificacion);
        }

        try {
            return toResponse(resenaRepository.saveAndFlush(resena));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe una reseña activa para esta combinación curso-docente");
        }
    }

    @Override
    public Page<ResenaResponse> listarMisResenas(Pageable pageable) {
        Estudiante estudiante = authenticatedUserService.getCurrentEstudiante();
        return resenaRepository.findByEstudianteId(estudiante.getId(), pageable).map(this::toResponse);
    }

    @Override
    public ResenaResponse obtenerMiResena(Long id) {
        Estudiante estudiante = authenticatedUserService.getCurrentEstudiante();
        Resena resena = resenaRepository.findById(id)
            .filter(r -> r.getEstudiante().getId().equals(estudiante.getId()))
            .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));
        return toResponse(resena);
    }

    private Resena buildNewReview(Estudiante estudiante, CursoDocente cursoDocente) {
        Resena resena = new Resena();
        resena.setEstudiante(estudiante);
        resena.setCursoDocente(cursoDocente);
        resena.setEstado(EstadoResena.PENDIENTE);
        resena.setVersion(1);
        return resena;
    }

    private void validateCriteria(List<CriterioPuntajeRequest> incoming, List<CriterioCalificacion> activeCriteria) {
        Set<Long> requiredIds = activeCriteria.stream().map(CriterioCalificacion::getId).collect(Collectors.toSet());
        Set<Long> providedIds = incoming.stream().map(CriterioPuntajeRequest::criterioId).collect(Collectors.toSet());
        if (incoming.size() != activeCriteria.size() || providedIds.size() != activeCriteria.size() || !requiredIds.equals(providedIds)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar exactamente una calificación por cada criterio activo");
        }
    }

    private ResenaResponse toResponse(Resena resena) {
        return new ResenaResponse(
            resena.getId(),
            resena.getCursoDocente().getId(),
            resena.getCursoDocente().getCurso().getNombre(),
            resena.getCursoDocente().getDocente().getNombres() + " " + resena.getCursoDocente().getDocente().getApellidos(),
            resena.getComentario(),
            resena.isEsAnonimo(),
            resena.getEstado().name(),
            resena.getVersion(),
            resena.getMotivoRechazo(),
            resena.getFechaCreacion(),
            resena.getCalificaciones().stream()
                .map(c -> new ResenaCalificacionResponse(c.getCriterio().getId(), c.getCriterio().getNombre(), c.getPuntaje().intValue()))
                .toList()
        );
    }
}

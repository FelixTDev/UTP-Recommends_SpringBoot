package com.utp.recommends.admin.curso.service;

import com.utp.recommends.admin.curso.dto.request.CursoCreateRequest;
import com.utp.recommends.admin.curso.dto.response.CursoResponse;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.TipoCurso;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.CursoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CursoAdminService {
    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;
    public CursoAdminService(CursoRepository cursoRepository, CarreraRepository carreraRepository) {
        this.cursoRepository = cursoRepository;
        this.carreraRepository = carreraRepository;
    }

    public CursoResponse create(CursoCreateRequest request) {
        Curso curso = new Curso();
        curso.setNombre(request.nombre());
        curso.setTipo(request.tipo());
        curso.setEstado(request.estado() == null ? EstadoSimple.ACTIVO : request.estado());
        if (request.tipo() == TipoCurso.GENERAL) {
            curso.setCarrera(null);
            if (cursoRepository.findByNombreIgnoreCaseAndTipoAndCarreraIsNull(request.nombre(), TipoCurso.GENERAL).isPresent()) {
                throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un curso general con ese nombre");
            }
        } else {
            if (request.carreraId() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "carreraId es obligatorio para cursos de carrera");
            }
            Carrera carrera = carreraRepository.findById(request.carreraId())
                .filter(c -> c.getEstado() == EstadoCarrera.ACTIVA)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "La carrera no existe o está inactiva"));
            curso.setCarrera(carrera);
        }
        return toResponse(cursoRepository.save(curso));
    }

    public List<CursoResponse> list() { return cursoRepository.findAll().stream().map(this::toResponse).toList(); }
    public List<CursoResponse> listActive() { return cursoRepository.findByEstado(EstadoSimple.ACTIVO).stream().map(this::toResponse).toList(); }
    public CursoResponse update(Long id, CursoCreateRequest request) {
        Curso curso = cursoRepository.findById(id).orElseThrow();
        curso.setNombre(request.nombre());
        curso.setTipo(request.tipo());
        if (request.tipo() == TipoCurso.GENERAL) {
            curso.setCarrera(null);
        } else {
            Carrera carrera = carreraRepository.findById(request.carreraId()).orElseThrow();
            curso.setCarrera(carrera);
        }
        curso.setEstado(request.estado() == null ? curso.getEstado() : request.estado());
        return toResponse(cursoRepository.save(curso));
    }
    public CursoResponse inactivate(Long id) {
        Curso curso = cursoRepository.findById(id).orElseThrow();
        curso.setEstado(EstadoSimple.INACTIVO);
        return toResponse(cursoRepository.save(curso));
    }
    private CursoResponse toResponse(Curso curso) { return new CursoResponse(curso.getId(), curso.getNombre(), curso.getTipo().name(), curso.getCarrera() == null ? null : curso.getCarrera().getId(), curso.getEstado().name()); }
}

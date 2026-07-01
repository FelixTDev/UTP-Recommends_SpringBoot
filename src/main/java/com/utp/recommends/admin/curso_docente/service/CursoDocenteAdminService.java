package com.utp.recommends.admin.curso_docente.service;

import com.utp.recommends.admin.curso_docente.dto.request.CursoDocenteEstadoRequest;
import com.utp.recommends.admin.curso_docente.dto.request.CursoDocenteRequest;
import com.utp.recommends.admin.curso_docente.dto.response.CursoDocenteResponse;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.repository.CursoDocenteRepository;
import com.utp.recommends.repository.CursoRepository;
import com.utp.recommends.repository.DocenteRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CursoDocenteAdminService {
    private final CursoDocenteRepository repository;
    private final CursoRepository cursoRepository;
    private final DocenteRepository docenteRepository;
    public CursoDocenteAdminService(CursoDocenteRepository repository, CursoRepository cursoRepository, DocenteRepository docenteRepository) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
        this.docenteRepository = docenteRepository;
    }
    public CursoDocenteResponse create(CursoDocenteRequest request) {
        if (repository.findByCursoIdAndDocenteId(request.cursoId(), request.docenteId()).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "La relación curso-docente ya existe");
        }
        CursoDocente entity = new CursoDocente();
        entity.setCurso(cursoRepository.findById(request.cursoId()).orElseThrow());
        entity.setDocente(docenteRepository.findById(request.docenteId()).orElseThrow());
        entity.setEstado(request.estado() == null ? EstadoSimple.ACTIVO : request.estado());
        return toResponse(repository.save(entity));
    }
    public List<CursoDocenteResponse> list() { return repository.findAll().stream().map(this::toResponse).toList(); }
    public List<CursoDocenteResponse> listByCurso(Long cursoId) { return repository.findByCursoId(cursoId).stream().map(this::toResponse).toList(); }
    public List<CursoDocenteResponse> listByDocente(Long docenteId) { return repository.findByDocenteId(docenteId).stream().map(this::toResponse).toList(); }
    public CursoDocenteResponse updateEstado(Long id, CursoDocenteEstadoRequest request) {
        CursoDocente entity = repository.findById(id).orElseThrow();
        entity.setEstado(request.estado());
        return toResponse(repository.save(entity));
    }
    private CursoDocenteResponse toResponse(CursoDocente entity) {
        return new CursoDocenteResponse(entity.getId(), entity.getCurso().getId(), entity.getCurso().getNombre(), entity.getDocente().getId(), entity.getDocente().getNombres() + " " + entity.getDocente().getApellidos(), entity.getEstado().name());
    }
}

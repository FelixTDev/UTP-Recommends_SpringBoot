package com.utp.recommends.admin.criterio.service;

import com.utp.recommends.admin.criterio.dto.request.CriterioEstadoRequest;
import com.utp.recommends.admin.criterio.dto.request.CriterioRequest;
import com.utp.recommends.admin.criterio.dto.response.CriterioResponse;
import com.utp.recommends.domain.entity.CriterioCalificacion;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CriterioAdminService {
    private final CriterioCalificacionRepository repository;
    public CriterioAdminService(CriterioCalificacionRepository repository) { this.repository = repository; }
    public CriterioResponse create(CriterioRequest request) {
        CriterioCalificacion entity = new CriterioCalificacion();
        entity.setNombre(request.nombre());
        entity.setDescripcion(request.descripcion());
        entity.setEstado(EstadoSimple.ACTIVO);
        return toResponse(repository.save(entity));
    }
    public List<CriterioResponse> list() { return repository.findAll().stream().map(this::toResponse).toList(); }
    public List<CriterioResponse> listActive() { return repository.findByEstado(EstadoSimple.ACTIVO).stream().map(this::toResponse).toList(); }
    public CriterioResponse update(Long id, CriterioRequest request) {
        CriterioCalificacion entity = repository.findById(id).orElseThrow();
        entity.setNombre(request.nombre());
        entity.setDescripcion(request.descripcion());
        return toResponse(repository.save(entity));
    }
    public CriterioResponse updateEstado(Long id, CriterioEstadoRequest request) {
        CriterioCalificacion entity = repository.findById(id).orElseThrow();
        entity.setEstado(request.estado());
        return toResponse(repository.save(entity));
    }
    private CriterioResponse toResponse(CriterioCalificacion entity) { return new CriterioResponse(entity.getId(), entity.getNombre(), entity.getDescripcion(), entity.getEstado().name()); }
}

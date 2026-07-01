package com.utp.recommends.admin.docente.service;

import com.utp.recommends.admin.docente.dto.request.DocenteRequest;
import com.utp.recommends.admin.docente.dto.response.DocenteResponse;
import com.utp.recommends.domain.entity.Docente;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.repository.DocenteRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DocenteAdminService {
    private final DocenteRepository docenteRepository;
    public DocenteAdminService(DocenteRepository docenteRepository) { this.docenteRepository = docenteRepository; }
    public DocenteResponse create(DocenteRequest request) {
        Docente docente = new Docente();
        docente.setNombres(request.nombres());
        docente.setApellidos(request.apellidos());
        docente.setEmail(request.email());
        docente.setEstado(EstadoSimple.ACTIVO);
        return toResponse(docenteRepository.save(docente));
    }
    public List<DocenteResponse> list() { return docenteRepository.findAll().stream().map(this::toResponse).toList(); }
    public List<DocenteResponse> listActive() { return docenteRepository.findByEstado(EstadoSimple.ACTIVO).stream().map(this::toResponse).toList(); }
    public DocenteResponse update(Long id, DocenteRequest request) {
        Docente docente = docenteRepository.findById(id).orElseThrow();
        docente.setNombres(request.nombres());
        docente.setApellidos(request.apellidos());
        docente.setEmail(request.email());
        return toResponse(docenteRepository.save(docente));
    }
    public DocenteResponse inactivate(Long id) {
        Docente docente = docenteRepository.findById(id).orElseThrow();
        docente.setEstado(EstadoSimple.INACTIVO);
        return toResponse(docenteRepository.save(docente));
    }
    private DocenteResponse toResponse(Docente docente) { return new DocenteResponse(docente.getId(), docente.getNombres(), docente.getApellidos(), docente.getEmail(), docente.getEstado().name()); }
}

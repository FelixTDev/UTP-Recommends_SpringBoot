package com.utp.recommends.admin.curso_docente.controller;

import com.utp.recommends.admin.curso_docente.dto.request.CursoDocenteEstadoRequest;
import com.utp.recommends.admin.curso_docente.dto.request.CursoDocenteRequest;
import com.utp.recommends.admin.curso_docente.dto.response.CursoDocenteResponse;
import com.utp.recommends.admin.curso_docente.service.CursoDocenteAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class CursoDocenteAdminController {
    private final CursoDocenteAdminService service;
    public CursoDocenteAdminController(CursoDocenteAdminService service) { this.service = service; }
    @PostMapping("/api/admin/curso-docente") @ResponseStatus(HttpStatus.CREATED) public CursoDocenteResponse create(@Valid @RequestBody CursoDocenteRequest request) { return service.create(request); }
    @GetMapping("/api/admin/curso-docente") public List<CursoDocenteResponse> list() { return service.list(); }
    @GetMapping("/api/admin/cursos/{cursoId}/docentes") public List<CursoDocenteResponse> byCurso(@PathVariable Long cursoId) { return service.listByCurso(cursoId); }
    @GetMapping("/api/admin/docentes/{docenteId}/cursos") public List<CursoDocenteResponse> byDocente(@PathVariable Long docenteId) { return service.listByDocente(docenteId); }
    @PatchMapping("/api/admin/curso-docente/{id}/estado") public CursoDocenteResponse updateEstado(@PathVariable Long id, @Valid @RequestBody CursoDocenteEstadoRequest request) { return service.updateEstado(id, request); }
}

package com.utp.recommends.admin.curso.controller;

import com.utp.recommends.admin.curso.dto.request.CursoCreateRequest;
import com.utp.recommends.admin.curso.dto.request.CursoEstadoRequest;
import com.utp.recommends.admin.curso.dto.response.CursoResponse;
import com.utp.recommends.admin.curso.service.CursoAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cursos")
public class CursoAdminController {
    private final CursoAdminService service;
    public CursoAdminController(CursoAdminService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public CursoResponse create(@Valid @RequestBody CursoCreateRequest request) { return service.create(request); }
    @GetMapping public List<CursoResponse> list() { return service.list(); }
    @PutMapping("/{id}") public CursoResponse update(@PathVariable Long id, @Valid @RequestBody CursoCreateRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/estado") public CursoResponse updateEstado(@PathVariable Long id, @Valid @RequestBody CursoEstadoRequest request) { return service.updateEstado(id, request); }
    @DeleteMapping("/{id}") public CursoResponse inactivate(@PathVariable Long id) { return service.inactivate(id); }
}

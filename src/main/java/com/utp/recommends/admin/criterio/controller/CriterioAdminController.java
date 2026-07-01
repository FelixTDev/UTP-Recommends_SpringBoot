package com.utp.recommends.admin.criterio.controller;

import com.utp.recommends.admin.criterio.dto.request.CriterioEstadoRequest;
import com.utp.recommends.admin.criterio.dto.request.CriterioRequest;
import com.utp.recommends.admin.criterio.dto.response.CriterioResponse;
import com.utp.recommends.admin.criterio.service.CriterioAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/criterios")
public class CriterioAdminController {
    private final CriterioAdminService service;
    public CriterioAdminController(CriterioAdminService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public CriterioResponse create(@Valid @RequestBody CriterioRequest request) { return service.create(request); }
    @GetMapping public List<CriterioResponse> list() { return service.list(); }
    @PutMapping("/{id}") public CriterioResponse update(@PathVariable Long id, @Valid @RequestBody CriterioRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/estado") public CriterioResponse updateEstado(@PathVariable Long id, @Valid @RequestBody CriterioEstadoRequest request) { return service.updateEstado(id, request); }
}

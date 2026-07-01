package com.utp.recommends.admin.docente.controller;

import com.utp.recommends.admin.docente.dto.request.DocenteRequest;
import com.utp.recommends.admin.docente.dto.response.DocenteResponse;
import com.utp.recommends.admin.docente.service.DocenteAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/docentes")
public class DocenteAdminController {
    private final DocenteAdminService service;
    public DocenteAdminController(DocenteAdminService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public DocenteResponse create(@Valid @RequestBody DocenteRequest request) { return service.create(request); }
    @GetMapping public List<DocenteResponse> list() { return service.list(); }
    @PutMapping("/{id}") public DocenteResponse update(@PathVariable Long id, @Valid @RequestBody DocenteRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") public DocenteResponse inactivate(@PathVariable Long id) { return service.inactivate(id); }
}

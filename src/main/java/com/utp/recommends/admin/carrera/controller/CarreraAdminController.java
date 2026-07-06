package com.utp.recommends.admin.carrera.controller;

import com.utp.recommends.admin.carrera.dto.request.CarreraEstadoRequest;
import com.utp.recommends.admin.carrera.dto.request.CarreraRequest;
import com.utp.recommends.admin.carrera.dto.response.CarreraResponse;
import com.utp.recommends.admin.carrera.service.CarreraAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/carreras")
public class CarreraAdminController {
    private final CarreraAdminService service;
    public CarreraAdminController(CarreraAdminService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public CarreraResponse create(@Valid @RequestBody CarreraRequest request) { return service.create(request); }
    @GetMapping public List<CarreraResponse> list() { return service.list(); }
    @PutMapping("/{id}") public CarreraResponse update(@PathVariable Long id, @Valid @RequestBody CarreraRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/estado") public CarreraResponse updateEstado(@PathVariable Long id, @Valid @RequestBody CarreraEstadoRequest request) { return service.updateEstado(id, request); }
    @DeleteMapping("/{id}") public CarreraResponse inactivate(@PathVariable Long id) { return service.inactivate(id); }
}

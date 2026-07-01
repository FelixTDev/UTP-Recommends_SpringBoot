package com.utp.recommends.estudiante.solicitud.controller;

import com.utp.recommends.estudiante.solicitud.dto.request.SolicitudCreateRequest;
import com.utp.recommends.estudiante.solicitud.dto.response.SolicitudResponse;
import com.utp.recommends.estudiante.solicitud.service.SolicitudEstudianteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudiante/solicitudes")
public class SolicitudEstudianteController {
    private final SolicitudEstudianteService service;
    public SolicitudEstudianteController(SolicitudEstudianteService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public SolicitudResponse crear(@Valid @RequestBody SolicitudCreateRequest request) { return service.crear(request); }
    @GetMapping("/mis-solicitudes") public Page<SolicitudResponse> list(Pageable pageable) { return service.listarMisSolicitudes(pageable); }
    @GetMapping("/mis-solicitudes/{id}") public SolicitudResponse get(@PathVariable Long id) { return service.obtenerMiSolicitud(id); }
}

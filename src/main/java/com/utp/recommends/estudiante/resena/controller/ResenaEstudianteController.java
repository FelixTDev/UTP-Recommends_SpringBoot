package com.utp.recommends.estudiante.resena.controller;

import com.utp.recommends.estudiante.resena.dto.request.ResenaCreateRequest;
import com.utp.recommends.estudiante.resena.dto.response.ResenaResponse;
import com.utp.recommends.estudiante.resena.service.ResenaEstudianteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudiante/resenas")
public class ResenaEstudianteController {
    private final ResenaEstudianteService service;
    public ResenaEstudianteController(ResenaEstudianteService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ResenaResponse crear(@Valid @RequestBody ResenaCreateRequest request) { return service.crear(request); }
    @GetMapping("/mis-resenas") public Page<ResenaResponse> list(Pageable pageable) { return service.listarMisResenas(pageable); }
    @GetMapping("/mis-resenas/{id}") public ResenaResponse get(@PathVariable Long id) { return service.obtenerMiResena(id); }
}

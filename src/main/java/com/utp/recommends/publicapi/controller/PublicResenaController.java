package com.utp.recommends.publicapi.controller;

import com.utp.recommends.publicapi.dto.response.PromedioCriterioResponse;
import com.utp.recommends.publicapi.dto.response.PublicResenaResponse;
import com.utp.recommends.publicapi.service.PublicResenaService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/resenas")
public class PublicResenaController {
    private final PublicResenaService service;
    public PublicResenaController(PublicResenaService service) { this.service = service; }
    @GetMapping public Page<PublicResenaResponse> list(@RequestParam(required = false) Long cursoId, @RequestParam(required = false) Long cursoDocenteId, Pageable pageable) { return service.listar(cursoId, cursoDocenteId, pageable); }
    @GetMapping("/curso-docente/{cursoDocenteId}") public Page<PublicResenaResponse> byCursoDocente(@PathVariable Long cursoDocenteId, Pageable pageable) { return service.listar(null, cursoDocenteId, pageable); }
    @GetMapping("/curso/{cursoId}") public Page<PublicResenaResponse> byCurso(@PathVariable Long cursoId, Pageable pageable) { return service.listar(cursoId, null, pageable); }
    @GetMapping("/promedios/curso-docente/{cursoDocenteId}") public List<PromedioCriterioResponse> promedios(@PathVariable Long cursoDocenteId) { return service.promediosPorCursoDocente(cursoDocenteId); }
}

package com.utp.recommends.admin.moderacion_resena.controller;

import com.utp.recommends.admin.moderacion_resena.dto.request.MotivoRechazoRequest;
import com.utp.recommends.admin.moderacion_resena.dto.response.ModeracionResenaResponse;
import com.utp.recommends.admin.moderacion_resena.service.ModeracionResenaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/moderacion/resenas")
public class ModeracionResenaAdminController {
    private final ModeracionResenaService service;
    public ModeracionResenaAdminController(ModeracionResenaService service) { this.service = service; }
    @GetMapping public List<ModeracionResenaResponse> pendientes(@RequestParam(required = false) String estado) { return service.pendientes(estado); }
    @PostMapping("/{id}/aprobar") public ModeracionResenaResponse aprobar(@PathVariable Long id) { return service.aprobar(id); }
    @PostMapping("/{id}/rechazar") public ModeracionResenaResponse rechazar(@PathVariable Long id, @Valid @RequestBody MotivoRechazoRequest request) { return service.rechazar(id, request.motivoRechazo()); }
    @PostMapping("/{id}/ocultar") public ModeracionResenaResponse ocultar(@PathVariable Long id) { return service.ocultar(id); }
}

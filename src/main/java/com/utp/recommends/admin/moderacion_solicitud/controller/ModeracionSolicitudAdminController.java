package com.utp.recommends.admin.moderacion_solicitud.controller;

import com.utp.recommends.admin.moderacion_solicitud.dto.request.AprobarSolicitudRequest;
import com.utp.recommends.admin.moderacion_solicitud.dto.request.RechazarSolicitudRequest;
import com.utp.recommends.admin.moderacion_solicitud.dto.response.ModeracionSolicitudResponse;
import com.utp.recommends.admin.moderacion_solicitud.service.SolicitudModeracionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/moderacion/solicitudes")
public class ModeracionSolicitudAdminController {
    private final SolicitudModeracionService service;
    public ModeracionSolicitudAdminController(SolicitudModeracionService service) { this.service = service; }
    @GetMapping public List<ModeracionSolicitudResponse> pendientes(@RequestParam(required = false) String estado) { return service.pendientes(estado); }
    @PostMapping("/{id}/aprobar") public ModeracionSolicitudResponse aprobar(@PathVariable Long id, @Valid @RequestBody AprobarSolicitudRequest request) { return service.aprobar(id, request); }
    @PostMapping("/{id}/rechazar") public ModeracionSolicitudResponse rechazar(@PathVariable Long id, @Valid @RequestBody RechazarSolicitudRequest request) { return service.rechazar(id, request.motivoRechazo()); }
}

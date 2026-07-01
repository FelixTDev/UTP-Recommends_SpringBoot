package com.utp.recommends.admin.usuario.controller;

import com.utp.recommends.admin.usuario.dto.request.UsuarioEstadoRequest;
import com.utp.recommends.admin.usuario.dto.request.UsuarioUpdateRequest;
import com.utp.recommends.admin.usuario.dto.response.UsuarioResponse;
import com.utp.recommends.admin.usuario.service.UsuarioAdminService;
import com.utp.recommends.domain.enums.EstadoUsuario;
import com.utp.recommends.domain.enums.RolUsuario;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {
    private final UsuarioAdminService service;
    public UsuarioAdminController(UsuarioAdminService service) { this.service = service; }
    @GetMapping public List<UsuarioResponse> list(@RequestParam(required = false) RolUsuario rol, @RequestParam(required = false) EstadoUsuario estado) { return service.list(rol, estado); }
    @GetMapping("/{id}") public UsuarioResponse get(@PathVariable Long id) { return service.getById(id); }
    @PutMapping("/{id}") public UsuarioResponse update(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest request) { return service.update(id, request); }
    @PatchMapping("/{id}/estado") public UsuarioResponse updateEstado(@PathVariable Long id, @Valid @RequestBody UsuarioEstadoRequest request) { return service.updateEstado(id, request); }
}

package com.utp.recommends.admin.usuario.service;

import com.utp.recommends.admin.usuario.dto.request.UsuarioEstadoRequest;
import com.utp.recommends.admin.usuario.dto.request.UsuarioUpdateRequest;
import com.utp.recommends.admin.usuario.dto.response.UsuarioResponse;
import com.utp.recommends.domain.enums.EstadoUsuario;
import com.utp.recommends.domain.enums.RolUsuario;
import com.utp.recommends.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioResponse> list(RolUsuario rol, EstadoUsuario estado) {
        return usuarioRepository.findAll().stream()
            .filter(u -> rol == null || u.getRol() == rol)
            .filter(u -> estado == null || u.getEstado() == estado)
            .map(u -> new UsuarioResponse(u.getId(), u.getEmail(), u.getNombres(), u.getApellidos(), u.getRol().name(), u.getEstado().name()))
            .toList();
    }

    public UsuarioResponse getById(Long id) {
        var u = usuarioRepository.findById(id).orElseThrow();
        return new UsuarioResponse(u.getId(), u.getEmail(), u.getNombres(), u.getApellidos(), u.getRol().name(), u.getEstado().name());
    }

    public UsuarioResponse update(Long id, UsuarioUpdateRequest request) {
        var u = usuarioRepository.findById(id).orElseThrow();
        u.setNombres(request.nombres());
        u.setApellidos(request.apellidos());
        u = usuarioRepository.save(u);
        return new UsuarioResponse(u.getId(), u.getEmail(), u.getNombres(), u.getApellidos(), u.getRol().name(), u.getEstado().name());
    }

    public UsuarioResponse updateEstado(Long id, UsuarioEstadoRequest request) {
        var u = usuarioRepository.findById(id).orElseThrow();
        u.setEstado(request.estado());
        u = usuarioRepository.save(u);
        return new UsuarioResponse(u.getId(), u.getEmail(), u.getNombres(), u.getApellidos(), u.getRol().name(), u.getEstado().name());
    }
}

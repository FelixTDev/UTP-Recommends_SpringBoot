package com.utp.recommends.security;

import com.utp.recommends.common.exception.ResourceNotFoundException;
import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.entity.Usuario;
import com.utp.recommends.repository.EstudianteRepository;
import com.utp.recommends.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;

    public AuthenticatedUserService(UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
    }

    public Usuario getCurrentUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioPrincipal principal)) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        return usuarioRepository.findById(principal.getUsuario().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public Estudiante getCurrentEstudiante() {
        return estudianteRepository.findByUsuarioId(getCurrentUsuario().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated student not found"));
    }
}

package com.utp.recommends.estudiante.perfil.service;

import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.entity.Usuario;
import com.utp.recommends.estudiante.perfil.dto.request.StudentProfileUpdateRequest;
import com.utp.recommends.estudiante.perfil.dto.response.StudentProfileResponse;
import com.utp.recommends.repository.UsuarioRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    private final AuthenticatedUserService authenticatedUserService;
    private final UsuarioRepository usuarioRepository;

    public StudentProfileServiceImpl(
        AuthenticatedUserService authenticatedUserService,
        UsuarioRepository usuarioRepository
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile() {
        return toResponse(authenticatedUserService.getCurrentEstudiante());
    }

    @Override
    @Transactional
    public StudentProfileResponse updateProfile(StudentProfileUpdateRequest request) {
        Estudiante estudiante = authenticatedUserService.getCurrentEstudiante();
        Usuario usuario = estudiante.getUsuario();
        usuario.setNombres(request.nombres().trim());
        usuario.setApellidos(request.apellidos().trim());
        usuarioRepository.save(usuario);
        return toResponse(estudiante);
    }

    private StudentProfileResponse toResponse(Estudiante estudiante) {
        Usuario usuario = estudiante.getUsuario();
        return new StudentProfileResponse(
            estudiante.getId(),
            usuario.getId(),
            usuario.getEmail(),
            usuario.getRol().name(),
            usuario.getEstado().name(),
            estudiante.getCodigoEstudiante(),
            estudiante.getCarrera().getId(),
            estudiante.getCarrera().getNombre(),
            usuario.getNombres(),
            usuario.getApellidos()
        );
    }
}

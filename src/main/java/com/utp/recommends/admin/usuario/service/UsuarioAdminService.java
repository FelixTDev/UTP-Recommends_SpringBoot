package com.utp.recommends.admin.usuario.service;

import com.utp.recommends.admin.usuario.dto.request.UsuarioCreateRequest;
import com.utp.recommends.admin.usuario.dto.request.UsuarioEstadoRequest;
import com.utp.recommends.admin.usuario.dto.request.UsuarioUpdateRequest;
import com.utp.recommends.admin.usuario.dto.response.UsuarioResponse;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.common.validation.ValidationPatterns;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.entity.Usuario;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.domain.enums.EstadoUsuario;
import com.utp.recommends.domain.enums.RolUsuario;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.EstudianteRepository;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;
    private final ResenaRepository resenaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdminService(
        UsuarioRepository usuarioRepository,
        EstudianteRepository estudianteRepository,
        CarreraRepository carreraRepository,
        ResenaRepository resenaRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.carreraRepository = carreraRepository;
        this.resenaRepository = resenaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse create(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setNombres(request.nombres());
        usuario.setApellidos(request.apellidos());
        usuario.setRol(request.rol());
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario = usuarioRepository.save(usuario);

        if (request.rol() == RolUsuario.ESTUDIANTE) {
            upsertStudentProfile(usuario, request.email(), request.carreraId());
        }

        return toResponse(usuarioRepository.findById(usuario.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> list(RolUsuario rol, EstadoUsuario estado) {
        return usuarioRepository.findAll().stream()
            .filter(u -> rol == null || u.getRol() == rol)
            .filter(u -> estado == null || u.getEstado() == estado)
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getById(Long id) {
        var u = usuarioRepository.findById(id).orElseThrow();
        return toResponse(u);
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioUpdateRequest request) {
        var u = usuarioRepository.findById(id).orElseThrow();
        if (!u.getEmail().equalsIgnoreCase(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        u.setEmail(request.email());
        u.setNombres(request.nombres());
        u.setApellidos(request.apellidos());
        u = usuarioRepository.save(u);

        if (u.getRol() == RolUsuario.ESTUDIANTE) {
            upsertStudentProfile(u, request.email(), request.carreraId());
        }

        return toResponse(usuarioRepository.findById(u.getId()).orElseThrow());
    }

    @Transactional
    public UsuarioResponse updateEstado(Long id, UsuarioEstadoRequest request) {
        var u = usuarioRepository.findById(id).orElseThrow();
        u.setEstado(request.estado());
        u = usuarioRepository.save(u);
        return toResponse(u);
    }

    private void upsertStudentProfile(Usuario usuario, String email, Long carreraId) {
        if (!email.matches(ValidationPatterns.UTP_STUDENT_EMAIL)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El correo del estudiante debe tener formato U########@utp.edu.pe");
        }
        if (carreraId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "carreraId es obligatorio para usuarios estudiantes");
        }

        Carrera carrera = carreraRepository.findById(carreraId)
            .filter(item -> item.getEstado() == EstadoCarrera.ACTIVA)
            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "La carrera debe existir y estar activa"));

        String codigoEstudiante = email.substring(0, email.indexOf('@'));
        Optional<Estudiante> current = estudianteRepository.findByUsuarioId(usuario.getId());
        if (current.isEmpty() && estudianteRepository.existsByCodigoEstudiante(codigoEstudiante)) {
            throw new BusinessException(HttpStatus.CONFLICT, "El código de estudiante ya existe");
        }

        Estudiante estudiante = current.orElseGet(Estudiante::new);
        estudiante.setUsuario(usuario);
        estudiante.setCodigoEstudiante(codigoEstudiante);
        estudiante.setCarrera(carrera);
        estudianteRepository.save(estudiante);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        Optional<Estudiante> estudiante = estudianteRepository.findByUsuarioId(usuario.getId());
        Long estudianteId = estudiante.map(Estudiante::getId).orElse(null);
        String codigoEstudiante = estudiante.map(Estudiante::getCodigoEstudiante).orElse(null);
        Long carreraId = estudiante.map(item -> item.getCarrera().getId()).orElse(null);
        String carreraNombre = estudiante.map(item -> item.getCarrera().getNombre()).orElse(null);
        long totalResenas = estudianteId == null ? 0L : resenaRepository.countByEstudianteId(estudianteId);

        return new UsuarioResponse(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getNombres(),
            usuario.getApellidos(),
            usuario.getRol().name(),
            usuario.getEstado().name(),
            codigoEstudiante,
            carreraId,
            carreraNombre,
            totalResenas
        );
    }
}

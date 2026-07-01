package com.utp.recommends.auth.service;

import com.utp.recommends.auth.dto.request.LoginRequest;
import com.utp.recommends.auth.dto.request.RegisterRequest;
import com.utp.recommends.auth.dto.request.ChangePasswordRequest;
import com.utp.recommends.auth.dto.response.AuthResponse;
import com.utp.recommends.auth.dto.response.CurrentUserResponse;
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
import com.utp.recommends.repository.UsuarioRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import com.utp.recommends.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticatedUserService authenticatedUserService;

    @Value("${app.security.jwt.expiration-minutes}")
    private long expirationMinutes;

    public AuthServiceImpl(
        UsuarioRepository usuarioRepository,
        EstudianteRepository estudianteRepository,
        CarreraRepository carreraRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticatedUserService authenticatedUserService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.carreraRepository = carreraRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String codigoEstudiante = request.email().substring(0, request.email().indexOf('@'));
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        if (estudianteRepository.existsByCodigoEstudiante(codigoEstudiante)) {
            throw new BusinessException(HttpStatus.CONFLICT, "El código de estudiante ya está registrado");
        }

        Carrera carrera = carreraRepository.findById(request.carreraId())
            .filter(c -> c.getEstado() == EstadoCarrera.ACTIVA)
            .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "La carrera no existe o está inactiva"));

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setNombres(request.nombres());
        usuario.setApellidos(request.apellidos());
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario = usuarioRepository.save(usuario);

        Estudiante estudiante = new Estudiante();
        estudiante.setUsuario(usuario);
        estudiante.setCodigoEstudiante(codigoEstudiante);
        estudiante.setCarrera(carrera);
        estudianteRepository.save(estudiante);

        return buildAuthResponse(usuario);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "El usuario está inactivo");
        }
        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "El usuario está suspendido");
        }
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        return buildAuthResponse(usuario);
    }

    @Override
    public CurrentUserResponse me() {
        Usuario usuario = authenticatedUserService.getCurrentUsuario();
        return new CurrentUserResponse(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getRol().name(),
            usuario.getEstado().name(),
            usuario.getNombres(),
            usuario.getApellidos()
        );
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Usuario usuario = authenticatedUserService.getCurrentUsuario();
        if (!passwordEncoder.matches(request.currentPassword(), usuario.getPasswordHash())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }
        if (!request.newPassword().matches(ValidationPatterns.PASSWORD)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La nueva contraseña no cumple la política requerida");
        }
        if (request.newPassword().contains(" ")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La nueva contraseña no debe contener espacios");
        }
        if (passwordEncoder.matches(request.newPassword(), usuario.getPasswordHash())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la actual");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        usuarioRepository.save(usuario);
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        String token = jwtService.generateToken(usuario.getId(), usuario.getRol().name(), usuario.getEstado().name(), usuario.getEmail());
        return new AuthResponse(
            token,
            "Bearer",
            expirationMinutes,
            usuario.getRol().name(),
            usuario.getId(),
            usuario.getNombres() + " " + usuario.getApellidos()
        );
    }
}

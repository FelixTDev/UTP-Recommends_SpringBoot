package com.utp.recommends.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.utp.recommends.auth.dto.request.ChangePasswordRequest;
import com.utp.recommends.auth.dto.request.LoginRequest;
import com.utp.recommends.auth.dto.request.RegisterRequest;
import com.utp.recommends.auth.service.AuthServiceImpl;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.Usuario;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.domain.enums.EstadoUsuario;
import com.utp.recommends.domain.enums.RolUsuario;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.EstudianteRepository;
import com.utp.recommends.repository.UsuarioRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import com.utp.recommends.security.JwtService;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EstudianteRepository estudianteRepository;
    @Mock private CarreraRepository carreraRepository;
    @Mock private JwtService jwtService;
    @Mock private AuthenticatedUserService authenticatedUserService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() throws Exception {
        authService = new AuthServiceImpl(
            usuarioRepository,
            estudianteRepository,
            carreraRepository,
            new BCryptPasswordEncoder(),
            jwtService,
            authenticatedUserService
        );
        Field expiration = AuthServiceImpl.class.getDeclaredField("expirationMinutes");
        expiration.setAccessible(true);
        expiration.set(authService, 30L);
    }

    @Test
    void registerStudentSuccessfully() {
        RegisterRequest request = new RegisterRequest("U12345678@utp.edu.pe", "Password1!", "Juan", "Perez", 1L);
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setEstado(EstadoCarrera.ACTIVA);
        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(estudianteRepository.existsByCodigoEstudiante("U12345678")).thenReturn(false);
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(carrera));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(10L);
            return usuario;
        });
        when(jwtService.generateToken(10L, "ESTUDIANTE", "ACTIVO", "U12345678@utp.edu.pe")).thenReturn("token");

        var response = authService.register(request);

        assertThat(response.token()).isEqualTo("token");
        assertThat(response.rol()).isEqualTo("ESTUDIANTE");
    }

    @Test
    void registerFailsForWeakPassword() {
        RegisterRequest request = new RegisterRequest("U12345678@utp.edu.pe", "weak", "Juan", "Perez", 1L);
        assertThat(request.password()).doesNotMatch(com.utp.recommends.common.validation.ValidationPatterns.PASSWORD);
    }

    @Test
    void loginBlockedForInactiveUser() {
        Usuario usuario = new Usuario();
        usuario.setEmail("U12345678@utp.edu.pe");
        usuario.setPasswordHash(new BCryptPasswordEncoder().encode("Password1!"));
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuario.setRol(RolUsuario.ESTUDIANTE);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login(new LoginRequest(usuario.getEmail(), "Password1!")))
            .isInstanceOf(BusinessException.class)
            .hasMessage("El usuario está inactivo");
    }

    @Test
    void loginSuccessfully() {
        Usuario usuario = new Usuario();
        usuario.setId(11L);
        usuario.setEmail("U12345678@utp.edu.pe");
        usuario.setPasswordHash(new BCryptPasswordEncoder().encode("Password1!"));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuario.setNombres("Juan");
        usuario.setApellidos("Perez");
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(11L, "ESTUDIANTE", "ACTIVO", usuario.getEmail())).thenReturn("jwt");

        var response = authService.login(new LoginRequest(usuario.getEmail(), "Password1!"));

        assertThat(response.token()).isEqualTo("jwt");
        assertThat(response.userId()).isEqualTo(11L);
    }

    @Test
    void changePasswordRejectsSamePassword() {
        Usuario usuario = new Usuario();
        usuario.setPasswordHash(new BCryptPasswordEncoder().encode("Password1!"));
        when(authenticatedUserService.getCurrentUsuario()).thenReturn(usuario);

        assertThatThrownBy(() -> authService.changePassword(new ChangePasswordRequest("Password1!", "Password1!")))
            .isInstanceOf(BusinessException.class)
            .hasMessage("La nueva contraseña no puede ser igual a la actual");
    }

    @Test
    void changePasswordUpdatesHash() {
        Usuario usuario = new Usuario();
        usuario.setPasswordHash(new BCryptPasswordEncoder().encode("Password1!"));
        when(authenticatedUserService.getCurrentUsuario()).thenReturn(usuario);
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        authService.changePassword(new ChangePasswordRequest("Password1!", "NewPassword1!"));

        verify(usuarioRepository).save(usuario);
        assertThat(new BCryptPasswordEncoder().matches("NewPassword1!", usuario.getPasswordHash())).isTrue();
    }
}

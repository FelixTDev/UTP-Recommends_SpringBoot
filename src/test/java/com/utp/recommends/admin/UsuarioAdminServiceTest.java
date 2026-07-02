package com.utp.recommends.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.utp.recommends.admin.usuario.dto.request.UsuarioCreateRequest;
import com.utp.recommends.admin.usuario.dto.request.UsuarioUpdateRequest;
import com.utp.recommends.admin.usuario.service.UsuarioAdminService;
import com.utp.recommends.common.exception.BusinessException;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioAdminServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EstudianteRepository estudianteRepository;
    @Mock private CarreraRepository carreraRepository;
    @Mock private ResenaRepository resenaRepository;
    private UsuarioAdminService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioAdminService(
            usuarioRepository,
            estudianteRepository,
            carreraRepository,
            resenaRepository,
            new BCryptPasswordEncoder()
        );
    }

    @Test
    void createsStudentUserWithCareerAndDerivedCode() {
        Carrera carrera = new Carrera();
        carrera.setId(3L);
        carrera.setNombre("Ingeniería de Sistemas");
        carrera.setEstado(EstadoCarrera.ACTIVA);
        Estudiante estudiante = new Estudiante();
        estudiante.setId(22L);
        estudiante.setCodigoEstudiante("U20245555");
        estudiante.setCarrera(carrera);

        when(usuarioRepository.existsByEmail("U20245555@utp.edu.pe")).thenReturn(false);
        when(carreraRepository.findById(3L)).thenReturn(Optional.of(carrera));
        when(estudianteRepository.existsByCodigoEstudiante("U20245555")).thenReturn(false);
        when(estudianteRepository.findByUsuarioId(10L)).thenReturn(Optional.empty(), Optional.of(estudiante));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            if (usuario.getId() == null) {
                usuario.setId(10L);
            }
            return usuario;
        });
        when(estudianteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioRepository.findById(10L)).thenAnswer(invocation -> {
            Usuario usuario = new Usuario();
            usuario.setId(10L);
            usuario.setEmail("U20245555@utp.edu.pe");
            usuario.setNombres("Carlos");
            usuario.setApellidos("Mendoza");
            usuario.setRol(RolUsuario.ESTUDIANTE);
            usuario.setEstado(EstadoUsuario.ACTIVO);
            return Optional.of(usuario);
        });
        when(resenaRepository.countByEstudianteId(22L)).thenReturn(4L);

        var response = service.create(new UsuarioCreateRequest(
            "U20245555@utp.edu.pe",
            "Password1!",
            "Carlos",
            "Mendoza",
            RolUsuario.ESTUDIANTE,
            3L
        ));

        assertThat(response.codigoEstudiante()).isEqualTo("U20245555");
        assertThat(response.carreraNombre()).isEqualTo("Ingeniería de Sistemas");
        assertThat(response.totalResenas()).isEqualTo(4L);
    }

    @Test
    void rejectsStudentWithoutCareerOnUpdate() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setEmail("U20240001@utp.edu.pe");
        usuario.setRol(RolUsuario.ESTUDIANTE);

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> service.update(7L, new UsuarioUpdateRequest("U20240001@utp.edu.pe", "Ana", "Lopez", null)))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("carreraId");
    }
}

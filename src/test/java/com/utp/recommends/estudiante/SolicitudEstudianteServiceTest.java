package com.utp.recommends.estudiante;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.entity.Solicitud;
import com.utp.recommends.domain.entity.Usuario;
import com.utp.recommends.domain.enums.EstadoSolicitud;
import com.utp.recommends.domain.enums.TipoSolicitud;
import com.utp.recommends.estudiante.solicitud.dto.request.SolicitudCreateRequest;
import com.utp.recommends.estudiante.solicitud.dto.response.SolicitudResponse;
import com.utp.recommends.estudiante.solicitud.service.SolicitudEstudianteServiceImpl;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.SolicitudRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolicitudEstudianteServiceTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @Mock private CarreraRepository carreraRepository;
    @InjectMocks private SolicitudEstudianteServiceImpl service;

    @Test
    void createsTeacherRequestUsingSeparatedNames() {
        when(authenticatedUserService.getCurrentEstudiante()).thenReturn(estudiante());
        when(solicitudRepository.save(any())).thenAnswer(inv -> {
            Solicitud solicitud = inv.getArgument(0);
            solicitud.setId(1L);
            return solicitud;
        });

        SolicitudResponse response = service.crear(new SolicitudCreateRequest(
            TipoSolicitud.DOCENTE_NUEVO,
            null,
            null,
            null,
            "  Diego   Soca ",
            " Elme  ",
            "Comentario valido"
        ));

        assertThat(response.nombreDocenteSugerido()).isEqualTo("Diego Soca|Elme");
        assertThat(response.nombresDocenteSugerido()).isEqualTo("Diego Soca");
        assertThat(response.apellidosDocenteSugerido()).isEqualTo("Elme");
    }

    @Test
    void keepsTemporaryCompatibilityWithStrictLegacyFormat() {
        when(authenticatedUserService.getCurrentEstudiante()).thenReturn(estudiante());
        when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SolicitudResponse response = service.crear(new SolicitudCreateRequest(
            TipoSolicitud.DOCENTE_NUEVO,
            null,
            null,
            " Armando  |  Paredes ",
            null,
            null,
            "Comentario valido"
        ));

        assertThat(response.nombreDocenteSugerido()).isEqualTo("Armando|Paredes");
        assertThat(response.nombresDocenteSugerido()).isEqualTo("Armando");
        assertThat(response.apellidosDocenteSugerido()).isEqualTo("Paredes");
    }

    @Test
    void rejectsLegacyTeacherNameWithoutSeparator() {
        assertThatThrownBy(() -> service.crear(new SolicitudCreateRequest(
            TipoSolicitud.DOCENTE_NUEVO,
            null,
            null,
            "Dr. Armando Paredes",
            null,
            null,
            "Comentario valido"
        )))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("formato exacto nombres|apellidos");
    }

    @Test
    void rejectsTitlesInSeparatedTeacherFields() {
        assertThatThrownBy(() -> service.crear(new SolicitudCreateRequest(
            TipoSolicitud.DOCENTE_NUEVO,
            null,
            null,
            null,
            "Ing. Diego",
            "Sin Apellido",
            "Comentario valido"
        )))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("títulos profesionales");
    }

    @Test
    void rejectsPlaceholderSurname() {
        assertThatThrownBy(() -> service.crear(new SolicitudCreateRequest(
            TipoSolicitud.DOCENTE_NUEVO,
            null,
            null,
            null,
            "Diego",
            "Sin Apellido",
            "Comentario valido"
        )))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("relleno");
    }

    @Test
    void storesCanonicalTeacherValueForAmbosRequests() {
        when(authenticatedUserService.getCurrentEstudiante()).thenReturn(estudiante());
        when(carreraRepository.findById(2L)).thenReturn(Optional.of(carrera()));
        when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);

        service.crear(new SolicitudCreateRequest(
            TipoSolicitud.AMBOS,
            "Programacion",
            2L,
            null,
            "Ana Maria",
            "Lopez Vega",
            "Comentario valido"
        ));

        org.mockito.Mockito.verify(solicitudRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        assertThat(captor.getValue().getNombreDocenteSugerido()).isEqualTo("Ana Maria|Lopez Vega");
    }

    private Estudiante estudiante() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEmail("U12345678@utp.edu.pe");
        usuario.setNombres("Juan");
        usuario.setApellidos("Perez");

        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setUsuario(usuario);
        estudiante.setCarrera(carrera());
        estudiante.setCodigoEstudiante("U12345678");
        return estudiante;
    }

    private Carrera carrera() {
        Carrera carrera = new Carrera();
        carrera.setId(2L);
        carrera.setNombre("Ingeniería de Sistemas");
        return carrera;
    }
}

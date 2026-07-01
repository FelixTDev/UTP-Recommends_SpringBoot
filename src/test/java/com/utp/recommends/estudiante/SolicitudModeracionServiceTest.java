package com.utp.recommends.estudiante;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.utp.recommends.admin.moderacion_solicitud.dto.request.AprobarSolicitudRequest;
import com.utp.recommends.admin.moderacion_solicitud.service.SolicitudModeracionServiceImpl;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.CriterioCalificacion;
import com.utp.recommends.domain.entity.Solicitud;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.EstadoSolicitud;
import com.utp.recommends.domain.enums.TipoSolicitud;
import com.utp.recommends.estudiante.resena.dto.request.CriterioPuntajeRequest;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import com.utp.recommends.repository.CursoDocenteRepository;
import com.utp.recommends.repository.CursoRepository;
import com.utp.recommends.repository.DocenteRepository;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.repository.SolicitudRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolicitudModeracionServiceTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private CriterioCalificacionRepository criterioRepository;
    @Mock private CarreraRepository carreraRepository;
    @Mock private CursoRepository cursoRepository;
    @Mock private DocenteRepository docenteRepository;
    @Mock private CursoDocenteRepository cursoDocenteRepository;
    @Mock private ResenaRepository resenaRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @InjectMocks private SolicitudModeracionServiceImpl service;

    @Test
    void approvalRequiresExactlyOneScorePerActiveCriterion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(1L);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setTipo(TipoSolicitud.AMBOS);
        solicitud.setNombreCursoSugerido("POO");
        solicitud.setNombreDocenteSugerido("Luis");
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        CriterioCalificacion criterio = new CriterioCalificacion();
        criterio.setId(1L);
        criterio.setEstado(EstadoSimple.ACTIVO);
        when(criterioRepository.findByEstado(EstadoSimple.ACTIVO)).thenReturn(List.of(criterio, new CriterioCalificacion()));

        assertThatThrownBy(() -> service.aprobar(1L, new AprobarSolicitudRequest(null, null, List.of(new CriterioPuntajeRequest(1L, 5)))))
            .isInstanceOf(BusinessException.class);
    }
}

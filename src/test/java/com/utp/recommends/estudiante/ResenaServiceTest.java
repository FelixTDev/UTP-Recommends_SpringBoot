package com.utp.recommends.estudiante;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.CriterioCalificacion;
import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.domain.entity.Docente;
import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.entity.Resena;
import com.utp.recommends.domain.enums.EstadoResena;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.estudiante.resena.dto.request.CriterioPuntajeRequest;
import com.utp.recommends.estudiante.resena.dto.request.ResenaCreateRequest;
import com.utp.recommends.estudiante.resena.service.ResenaEstudianteServiceImpl;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import com.utp.recommends.repository.CursoDocenteRepository;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.security.AuthenticatedUserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock private AuthenticatedUserService authenticatedUserService;
    @Mock private CursoDocenteRepository cursoDocenteRepository;
    @Mock private CriterioCalificacionRepository criterioRepository;
    @Mock private ResenaRepository resenaRepository;
    @InjectMocks private ResenaEstudianteServiceImpl service;

    @Test
    void approvedExistingReviewReturnsConflict() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        CursoDocente cursoDocente = cursoDocente();
        Resena existing = new Resena();
        existing.setEstado(EstadoResena.APROBADA);
        CriterioCalificacion criterio = criterio();
        when(authenticatedUserService.getCurrentEstudiante()).thenReturn(estudiante);
        when(cursoDocenteRepository.findByIdAndEstado(1L, EstadoSimple.ACTIVO)).thenReturn(Optional.of(cursoDocente));
        when(criterioRepository.findByEstado(EstadoSimple.ACTIVO)).thenReturn(List.of(criterio));
        when(resenaRepository.findActiveByStudentAndCursoDocente(1L, 1L, java.util.EnumSet.of(EstadoResena.PENDIENTE, EstadoResena.APROBADA))).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.crear(new ResenaCreateRequest(1L, "Comentario valido", false, List.of(new CriterioPuntajeRequest(1L, 5)))))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectedReviewResendCreatesNewVersion() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        CursoDocente cursoDocente = cursoDocente();
        CriterioCalificacion criterio = criterio();
        Resena last = new Resena();
        last.setEstado(EstadoResena.RECHAZADA);
        last.setVersion(2);
        when(authenticatedUserService.getCurrentEstudiante()).thenReturn(estudiante);
        when(cursoDocenteRepository.findByIdAndEstado(1L, EstadoSimple.ACTIVO)).thenReturn(Optional.of(cursoDocente));
        when(criterioRepository.findByEstado(EstadoSimple.ACTIVO)).thenReturn(List.of(criterio));
        when(resenaRepository.findActiveByStudentAndCursoDocente(1L, 1L, java.util.EnumSet.of(EstadoResena.PENDIENTE, EstadoResena.APROBADA))).thenReturn(Optional.empty());
        when(resenaRepository.findTopByEstudianteIdAndCursoDocenteIdOrderByVersionDesc(1L, 1L)).thenReturn(Optional.of(last));
        when(resenaRepository.saveAndFlush(org.mockito.ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));

        var response = service.crear(new ResenaCreateRequest(1L, "Comentario valido", false, List.of(new CriterioPuntajeRequest(1L, 5))));

        assertThat(response.version()).isEqualTo(3);
    }

    @Test
    void savesReviewScoresAsByteValues() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        CursoDocente cursoDocente = cursoDocente();
        CriterioCalificacion criterio = criterio();
        ArgumentCaptor<Resena> captor = ArgumentCaptor.forClass(Resena.class);

        when(authenticatedUserService.getCurrentEstudiante()).thenReturn(estudiante);
        when(cursoDocenteRepository.findByIdAndEstado(1L, EstadoSimple.ACTIVO)).thenReturn(Optional.of(cursoDocente));
        when(criterioRepository.findByEstado(EstadoSimple.ACTIVO)).thenReturn(List.of(criterio));
        when(resenaRepository.findActiveByStudentAndCursoDocente(1L, 1L, java.util.EnumSet.of(EstadoResena.PENDIENTE, EstadoResena.APROBADA))).thenReturn(Optional.empty());
        when(resenaRepository.findTopByEstudianteIdAndCursoDocenteIdOrderByVersionDesc(1L, 1L)).thenReturn(Optional.empty());
        when(resenaRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        service.crear(new ResenaCreateRequest(1L, "Comentario valido", false, List.of(new CriterioPuntajeRequest(1L, 5))));

        org.mockito.Mockito.verify(resenaRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getCalificaciones()).hasSize(1);
        assertThat(captor.getValue().getCalificaciones().getFirst().getPuntaje()).isEqualTo(Byte.valueOf((byte) 5));
    }

    private CursoDocente cursoDocente() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setNombre("POO");
        Docente docente = new Docente();
        docente.setId(1L);
        docente.setNombres("Luis");
        docente.setApellidos("Perez");
        CursoDocente cursoDocente = new CursoDocente();
        cursoDocente.setId(1L);
        cursoDocente.setCurso(curso);
        cursoDocente.setDocente(docente);
        return cursoDocente;
    }

    private CriterioCalificacion criterio() {
        CriterioCalificacion criterio = new CriterioCalificacion();
        criterio.setId(1L);
        criterio.setNombre("Claridad");
        return criterio;
    }
}

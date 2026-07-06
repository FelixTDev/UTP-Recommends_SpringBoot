package com.utp.recommends.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.utp.recommends.admin.carrera.dto.request.CarreraEstadoRequest;
import com.utp.recommends.admin.carrera.service.CarreraAdminService;
import com.utp.recommends.admin.criterio.service.CriterioAdminService;
import com.utp.recommends.admin.curso.dto.request.CursoEstadoRequest;
import com.utp.recommends.admin.curso.service.CursoAdminService;
import com.utp.recommends.admin.curso_docente.service.CursoDocenteAdminService;
import com.utp.recommends.admin.docente.dto.request.DocenteEstadoRequest;
import com.utp.recommends.admin.docente.service.DocenteAdminService;
import com.utp.recommends.admin.usuario.service.UsuarioAdminService;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.entity.Docente;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.TipoCurso;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import com.utp.recommends.repository.CursoDocenteRepository;
import com.utp.recommends.repository.CursoRepository;
import com.utp.recommends.repository.DocenteRepository;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class AdminEstadoAndTransactionRegressionTest {

    @Mock private CarreraRepository carreraRepository;
    @Mock private CursoRepository cursoRepository;
    @Mock private DocenteRepository docenteRepository;
    @Mock private CriterioCalificacionRepository criterioRepository;
    @Mock private CursoDocenteRepository cursoDocenteRepository;

    @InjectMocks private CarreraAdminService carreraAdminService;
    @InjectMocks private CursoAdminService cursoAdminService;
    @InjectMocks private DocenteAdminService docenteAdminService;

    @Test
    void carreraUpdateEstadoAllowsReactivation() {
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ingenieria");
        carrera.setEstado(EstadoCarrera.INACTIVA);

        when(carreraRepository.findById(1L)).thenReturn(Optional.of(carrera));
        when(carreraRepository.save(carrera)).thenReturn(carrera);

        var response = carreraAdminService.updateEstado(1L, new CarreraEstadoRequest(EstadoCarrera.ACTIVA));

        assertThat(response.estado()).isEqualTo("ACTIVA");
    }

    @Test
    void cursoUpdateEstadoAllowsReactivation() {
        Curso curso = new Curso();
        curso.setId(2L);
        curso.setNombre("POO");
        curso.setTipo(TipoCurso.GENERAL);
        curso.setEstado(EstadoSimple.INACTIVO);

        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(curso)).thenReturn(curso);

        var response = cursoAdminService.updateEstado(2L, new CursoEstadoRequest(EstadoSimple.ACTIVO));

        assertThat(response.estado()).isEqualTo("ACTIVO");
    }

    @Test
    void docenteUpdateEstadoAllowsReactivation() {
        Docente docente = new Docente();
        docente.setId(3L);
        docente.setNombres("Ana");
        docente.setApellidos("Lopez");
        docente.setEmail("ana@utp.edu.pe");
        docente.setEstado(EstadoSimple.INACTIVO);

        when(docenteRepository.findById(3L)).thenReturn(Optional.of(docente));
        when(docenteRepository.save(docente)).thenReturn(docente);

        var response = docenteAdminService.updateEstado(3L, new DocenteEstadoRequest(EstadoSimple.ACTIVO));

        assertThat(response.estado()).isEqualTo("ACTIVO");
    }

    @Test
    void mutationServicesExposeTransactionalBoundaries() throws NoSuchMethodException {
        assertThat(findMethod(UsuarioAdminService.class, "create").isAnnotationPresent(Transactional.class)).isTrue();
        assertThat(findMethod(UsuarioAdminService.class, "update").isAnnotationPresent(Transactional.class)).isTrue();
        assertThat(findMethod(UsuarioAdminService.class, "updateEstado").isAnnotationPresent(Transactional.class)).isTrue();

        assertThat(findMethod(CursoDocenteAdminService.class, "create").isAnnotationPresent(Transactional.class)).isTrue();
        assertThat(findMethod(CursoDocenteAdminService.class, "updateEstado").isAnnotationPresent(Transactional.class)).isTrue();

        assertThat(findMethod(CriterioAdminService.class, "create").isAnnotationPresent(Transactional.class)).isTrue();
        assertThat(findMethod(CriterioAdminService.class, "update").isAnnotationPresent(Transactional.class)).isTrue();
        assertThat(findMethod(CriterioAdminService.class, "updateEstado").isAnnotationPresent(Transactional.class)).isTrue();
    }

    private Method findMethod(Class<?> type, String name) {
        return java.util.Arrays.stream(type.getDeclaredMethods())
            .filter(method -> method.getName().equals(name))
            .findFirst()
            .orElseThrow();
    }
}

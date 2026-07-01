package com.utp.recommends.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.utp.recommends.admin.curso.dto.request.CursoCreateRequest;
import com.utp.recommends.admin.curso.service.CursoAdminService;
import com.utp.recommends.common.exception.BusinessException;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.TipoCurso;
import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.CursoRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CursoAdminServiceTest {

    @Mock private CursoRepository cursoRepository;
    @Mock private CarreraRepository carreraRepository;
    @InjectMocks private CursoAdminService service;

    @Test
    void generalCourseForcesNullCareer() {
        when(cursoRepository.findByNombreIgnoreCaseAndTipoAndCarreraIsNull("Matematica", TipoCurso.GENERAL)).thenReturn(Optional.empty());
        when(cursoRepository.save(any())).thenAnswer(inv -> {
            Curso curso = inv.getArgument(0);
            curso.setId(1L);
            return curso;
        });

        var response = service.create(new CursoCreateRequest("Matematica", TipoCurso.GENERAL, 99L, EstadoSimple.ACTIVO));

        assertThat(response.carreraId()).isNull();
    }

    @Test
    void carreraCourseRequiresActiveCareer() {
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setEstado(EstadoCarrera.INACTIVA);
        when(carreraRepository.findById(1L)).thenReturn(Optional.of(carrera));

        assertThatThrownBy(() -> service.create(new CursoCreateRequest("POO", TipoCurso.CARRERA, 1L, EstadoSimple.ACTIVO)))
            .isInstanceOf(BusinessException.class);
    }
}

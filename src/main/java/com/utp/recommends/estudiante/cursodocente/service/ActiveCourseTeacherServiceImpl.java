package com.utp.recommends.estudiante.cursodocente.service;

import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.estudiante.cursodocente.dto.response.ActiveCourseTeacherOptionResponse;
import com.utp.recommends.repository.CursoDocenteRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ActiveCourseTeacherServiceImpl implements ActiveCourseTeacherService {

    private final CursoDocenteRepository cursoDocenteRepository;

    public ActiveCourseTeacherServiceImpl(CursoDocenteRepository cursoDocenteRepository) {
        this.cursoDocenteRepository = cursoDocenteRepository;
    }

    @Override
    public List<ActiveCourseTeacherOptionResponse> list(String texto, Long carreraId, Long cursoId, Long docenteId) {
        return cursoDocenteRepository.findActiveOptions(texto, carreraId, cursoId, docenteId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private ActiveCourseTeacherOptionResponse toResponse(CursoDocente cursoDocente) {
        Curso curso = cursoDocente.getCurso();
        String carreraNombre = curso.getCarrera() == null ? null : curso.getCarrera().getNombre();
        Long carreraId = curso.getCarrera() == null ? null : curso.getCarrera().getId();
        return new ActiveCourseTeacherOptionResponse(
            cursoDocente.getId(),
            curso.getId(),
            curso.getNombre(),
            null,
            curso.getTipo().name(),
            carreraId,
            carreraNombre,
            cursoDocente.getDocente().getId(),
            cursoDocente.getDocente().getNombres() + " " + cursoDocente.getDocente().getApellidos(),
            cursoDocente.getEstado().name()
        );
    }
}

package com.utp.recommends.estudiante.cursodocente.service;

import com.utp.recommends.estudiante.cursodocente.dto.response.ActiveCourseTeacherOptionResponse;
import java.util.List;

public interface ActiveCourseTeacherService {
    List<ActiveCourseTeacherOptionResponse> list(String texto, Long carreraId, Long cursoId, Long docenteId);
}

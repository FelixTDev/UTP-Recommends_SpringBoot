package com.utp.recommends.estudiante.cursodocente.controller;

import com.utp.recommends.estudiante.cursodocente.dto.response.ActiveCourseTeacherOptionResponse;
import com.utp.recommends.estudiante.cursodocente.service.ActiveCourseTeacherService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estudiante/curso-docente/activos")
public class ActiveCourseTeacherController {

    private final ActiveCourseTeacherService service;

    public ActiveCourseTeacherController(ActiveCourseTeacherService service) {
        this.service = service;
    }

    @GetMapping
    public List<ActiveCourseTeacherOptionResponse> list(
        @RequestParam(required = false) String texto,
        @RequestParam(required = false) Long carreraId,
        @RequestParam(required = false) Long cursoId,
        @RequestParam(required = false) Long docenteId
    ) {
        return service.list(texto, carreraId, cursoId, docenteId);
    }
}

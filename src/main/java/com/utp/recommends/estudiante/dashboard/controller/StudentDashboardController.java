package com.utp.recommends.estudiante.dashboard.controller;

import com.utp.recommends.estudiante.dashboard.dto.response.StudentDashboardResponse;
import com.utp.recommends.estudiante.dashboard.service.StudentDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estudiante/dashboard")
public class StudentDashboardController {

    private final StudentDashboardService service;

    public StudentDashboardController(StudentDashboardService service) {
        this.service = service;
    }

    @GetMapping
    public StudentDashboardResponse getDashboard() {
        return service.getDashboard();
    }
}

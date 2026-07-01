package com.utp.recommends.admin.dashboard.controller;

import com.utp.recommends.admin.dashboard.dto.response.AdminDashboardResponse;
import com.utp.recommends.admin.dashboard.service.AdminDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService service;

    public AdminDashboardController(AdminDashboardService service) {
        this.service = service;
    }

    @GetMapping
    public AdminDashboardResponse getDashboard() {
        return service.getDashboard();
    }
}

package com.utp.recommends.admin.criterio.controller;

import com.utp.recommends.admin.criterio.dto.response.CriterioResponse;
import com.utp.recommends.admin.criterio.service.CriterioAdminService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CriterioPublicController {
    private final CriterioAdminService service;
    public CriterioPublicController(CriterioAdminService service) { this.service = service; }
    @GetMapping("/api/public/criterios/activos") public List<CriterioResponse> list() { return service.listActive(); }
}

package com.utp.recommends.admin.carrera.controller;

import com.utp.recommends.admin.carrera.dto.response.CarreraResponse;
import com.utp.recommends.admin.carrera.service.CarreraAdminService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarreraPublicController {
    private final CarreraAdminService service;
    public CarreraPublicController(CarreraAdminService service) { this.service = service; }
    @GetMapping("/api/public/carreras/activas") public List<CarreraResponse> list() { return service.listActive(); }
}

package com.utp.recommends.estudiante.perfil.controller;

import com.utp.recommends.estudiante.perfil.dto.request.StudentProfileUpdateRequest;
import com.utp.recommends.estudiante.perfil.dto.response.StudentProfileResponse;
import com.utp.recommends.estudiante.perfil.service.StudentProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estudiante/perfil")
public class StudentProfileController {

    private final StudentProfileService service;

    public StudentProfileController(StudentProfileService service) {
        this.service = service;
    }

    @GetMapping
    public StudentProfileResponse getProfile() {
        return service.getProfile();
    }

    @PutMapping
    public StudentProfileResponse updateProfile(@Valid @RequestBody StudentProfileUpdateRequest request) {
        return service.updateProfile(request);
    }
}

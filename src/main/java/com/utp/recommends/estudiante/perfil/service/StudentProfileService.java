package com.utp.recommends.estudiante.perfil.service;

import com.utp.recommends.estudiante.perfil.dto.request.StudentProfileUpdateRequest;
import com.utp.recommends.estudiante.perfil.dto.response.StudentProfileResponse;

public interface StudentProfileService {
    StudentProfileResponse getProfile();
    StudentProfileResponse updateProfile(StudentProfileUpdateRequest request);
}

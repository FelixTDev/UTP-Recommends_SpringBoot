package com.utp.recommends.estudiante.resena.service;

import com.utp.recommends.estudiante.resena.dto.request.ResenaCreateRequest;
import com.utp.recommends.estudiante.resena.dto.response.ResenaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResenaEstudianteService {
    ResenaResponse crear(ResenaCreateRequest request);
    Page<ResenaResponse> listarMisResenas(Pageable pageable);
    ResenaResponse obtenerMiResena(Long id);
}

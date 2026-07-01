package com.utp.recommends.publicapi.service;

import com.utp.recommends.publicapi.dto.response.PromedioCriterioResponse;
import com.utp.recommends.publicapi.dto.response.PublicResenaResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PublicResenaService {
    Page<PublicResenaResponse> listar(Long cursoId, Long cursoDocenteId, Pageable pageable);
    List<PromedioCriterioResponse> promediosPorCursoDocente(Long cursoDocenteId);
}

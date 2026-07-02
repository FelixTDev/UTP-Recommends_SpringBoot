package com.utp.recommends.publicapi.service;

import com.utp.recommends.publicapi.dto.response.PromedioCriterioResponse;
import com.utp.recommends.publicapi.dto.response.PublicResenaCalificacionResponse;
import com.utp.recommends.publicapi.dto.response.PublicResenaResponse;
import com.utp.recommends.repository.ResenaCalificacionRepository;
import com.utp.recommends.repository.ResenaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicResenaServiceImpl implements PublicResenaService {

    private final ResenaRepository resenaRepository;
    private final ResenaCalificacionRepository resenaCalificacionRepository;

    public PublicResenaServiceImpl(ResenaRepository resenaRepository, ResenaCalificacionRepository resenaCalificacionRepository) {
        this.resenaRepository = resenaRepository;
        this.resenaCalificacionRepository = resenaCalificacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PublicResenaResponse> listar(Long cursoId, Long cursoDocenteId, Pageable pageable) {
        return resenaRepository.findPublicApproved(cursoId, cursoDocenteId, pageable)
            .map(resena -> new PublicResenaResponse(
                resena.getId(),
                resena.getCursoDocente().getCurso().getNombre(),
                resena.getCursoDocente().getDocente().getNombres() + " " + resena.getCursoDocente().getDocente().getApellidos(),
                resena.getComentario(),
                resena.isEsAnonimo(),
                resena.isEsAnonimo() ? null : resena.getEstudiante().getUsuario().getNombres() + " " + resena.getEstudiante().getUsuario().getApellidos(),
                resena.getFechaCreacion(),
                resena.getCalificaciones().stream()
                    .map(c -> new PublicResenaCalificacionResponse(c.getCriterio().getNombre(), c.getPuntaje().intValue()))
                    .toList()
            ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromedioCriterioResponse> promediosPorCursoDocente(Long cursoDocenteId) {
        return resenaCalificacionRepository.averageByCursoDocente(cursoDocenteId).stream()
            .map(row -> new PromedioCriterioResponse((String) row[0], ((Number) row[1]).doubleValue()))
            .toList();
    }
}

package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.domain.enums.EstadoSimple;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoDocenteRepository extends JpaRepository<CursoDocente, Long> {
    Optional<CursoDocente> findByIdAndEstado(Long id, EstadoSimple estado);
    Optional<CursoDocente> findByCursoIdAndDocenteId(Long cursoId, Long docenteId);
    List<CursoDocente> findByCursoId(Long cursoId);
    List<CursoDocente> findByDocenteId(Long docenteId);
}

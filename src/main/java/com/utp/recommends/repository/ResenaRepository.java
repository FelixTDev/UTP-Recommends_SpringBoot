package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Resena;
import com.utp.recommends.domain.enums.EstadoResena;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    @Query("""
        select r from Resena r
        where r.estudiante.id = :estudianteId
          and r.cursoDocente.id = :cursoDocenteId
          and r.estado in :estados
    """)
    Optional<Resena> findActiveByStudentAndCursoDocente(Long estudianteId, Long cursoDocenteId, Collection<EstadoResena> estados);

    Optional<Resena> findTopByEstudianteIdAndCursoDocenteIdOrderByVersionDesc(Long estudianteId, Long cursoDocenteId);

    Page<Resena> findByEstudianteId(Long estudianteId, Pageable pageable);

    List<Resena> findByEstadoOrderByFechaCreacionAsc(EstadoResena estado);

    @Query("""
        select r from Resena r
        join r.cursoDocente cd
        where r.estado = com.utp.recommends.domain.enums.EstadoResena.APROBADA
          and (:cursoId is null or cd.curso.id = :cursoId)
          and (:cursoDocenteId is null or cd.id = :cursoDocenteId)
    """)
    Page<Resena> findPublicApproved(Long cursoId, Long cursoDocenteId, Pageable pageable);
}

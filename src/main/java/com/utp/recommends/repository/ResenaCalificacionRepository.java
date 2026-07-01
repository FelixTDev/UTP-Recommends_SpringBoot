package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.ResenaCalificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResenaCalificacionRepository extends JpaRepository<ResenaCalificacion, Long> {
    List<ResenaCalificacion> findByResenaId(Long resenaId);

    @Query("""
        select rc.criterio.nombre, avg(rc.puntaje)
        from ResenaCalificacion rc
        where rc.resena.cursoDocente.id = :cursoDocenteId
          and rc.resena.estado = com.utp.recommends.domain.enums.EstadoResena.APROBADA
        group by rc.criterio.id, rc.criterio.nombre
    """)
    List<Object[]> averageByCursoDocente(Long cursoDocenteId);
}

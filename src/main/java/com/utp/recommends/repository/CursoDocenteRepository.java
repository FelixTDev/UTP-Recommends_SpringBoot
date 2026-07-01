package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.domain.enums.EstadoSimple;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CursoDocenteRepository extends JpaRepository<CursoDocente, Long> {
    Optional<CursoDocente> findByIdAndEstado(Long id, EstadoSimple estado);
    Optional<CursoDocente> findByCursoIdAndDocenteId(Long cursoId, Long docenteId);
    List<CursoDocente> findByCursoId(Long cursoId);
    List<CursoDocente> findByDocenteId(Long docenteId);

    @Query("""
        select cd from CursoDocente cd
        join fetch cd.curso c
        join fetch cd.docente d
        left join fetch c.carrera car
        where cd.estado = com.utp.recommends.domain.enums.EstadoSimple.ACTIVO
          and c.estado = com.utp.recommends.domain.enums.EstadoSimple.ACTIVO
          and d.estado = com.utp.recommends.domain.enums.EstadoSimple.ACTIVO
          and (:carreraId is null or car.id = :carreraId)
          and (:cursoId is null or c.id = :cursoId)
          and (:docenteId is null or d.id = :docenteId)
          and (
              :texto is null
              or lower(c.nombre) like lower(concat('%', :texto, '%'))
              or lower(d.nombres) like lower(concat('%', :texto, '%'))
              or lower(d.apellidos) like lower(concat('%', :texto, '%'))
          )
        order by c.nombre asc, d.apellidos asc, d.nombres asc
    """)
    List<CursoDocente> findActiveOptions(String texto, Long carreraId, Long cursoId, Long docenteId);
}

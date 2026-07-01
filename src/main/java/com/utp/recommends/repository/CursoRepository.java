package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.TipoCurso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByEstado(EstadoSimple estado);
    Optional<Curso> findByNombreIgnoreCaseAndTipoAndCarreraIsNull(String nombre, TipoCurso tipo);
    Optional<Curso> findByIdAndEstado(Long id, EstadoSimple estado);
    long countByEstado(EstadoSimple estado);
}

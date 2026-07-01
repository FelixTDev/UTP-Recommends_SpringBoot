package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.CriterioCalificacion;
import com.utp.recommends.domain.enums.EstadoSimple;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriterioCalificacionRepository extends JpaRepository<CriterioCalificacion, Long> {
    List<CriterioCalificacion> findByEstado(EstadoSimple estado);
    long countByEstado(EstadoSimple estado);
}

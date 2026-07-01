package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Solicitud;
import com.utp.recommends.domain.enums.EstadoSolicitud;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    Page<Solicitud> findByEstudianteId(Long estudianteId, Pageable pageable);
    List<Solicitud> findByEstadoOrderByFechaCreacionAsc(EstadoSolicitud estado);
    long countByEstudianteId(Long estudianteId);
    long countByEstado(EstadoSolicitud estado);
}

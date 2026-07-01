package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.enums.EstadoCarrera;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    List<Carrera> findByEstado(EstadoCarrera estado);
}

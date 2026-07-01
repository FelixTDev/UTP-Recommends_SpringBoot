package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Docente;
import com.utp.recommends.domain.enums.EstadoSimple;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
    List<Docente> findByEstado(EstadoSimple estado);
}

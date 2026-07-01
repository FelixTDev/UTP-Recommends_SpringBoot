package com.utp.recommends.repository;

import com.utp.recommends.domain.entity.Estudiante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByUsuarioId(Long usuarioId);
    boolean existsByCodigoEstudiante(String codigoEstudiante);
}

package com.utp.recommends.admin.carrera.service;

import com.utp.recommends.admin.carrera.dto.request.CarreraEstadoRequest;
import com.utp.recommends.admin.carrera.dto.request.CarreraRequest;
import com.utp.recommends.admin.carrera.dto.response.CarreraResponse;
import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.repository.CarreraRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarreraAdminService {
    private final CarreraRepository carreraRepository;
    public CarreraAdminService(CarreraRepository carreraRepository) { this.carreraRepository = carreraRepository; }
    public CarreraResponse create(CarreraRequest request) {
        Carrera carrera = new Carrera();
        carrera.setNombre(request.nombre());
        carrera.setEstado(EstadoCarrera.ACTIVA);
        carrera = carreraRepository.save(carrera);
        return toResponse(carrera);
    }
    public List<CarreraResponse> list() { return carreraRepository.findAll().stream().map(this::toResponse).toList(); }
    public List<CarreraResponse> listActive() { return carreraRepository.findByEstado(EstadoCarrera.ACTIVA).stream().map(this::toResponse).toList(); }
    public CarreraResponse update(Long id, CarreraRequest request) {
        Carrera carrera = carreraRepository.findById(id).orElseThrow();
        carrera.setNombre(request.nombre());
        return toResponse(carreraRepository.save(carrera));
    }
    public CarreraResponse inactivate(Long id) {
        Carrera carrera = carreraRepository.findById(id).orElseThrow();
        carrera.setEstado(EstadoCarrera.INACTIVA);
        return toResponse(carreraRepository.save(carrera));
    }
    public CarreraResponse updateEstado(Long id, CarreraEstadoRequest request) {
        Carrera carrera = carreraRepository.findById(id).orElseThrow();
        carrera.setEstado(request.estado());
        return toResponse(carreraRepository.save(carrera));
    }
    private CarreraResponse toResponse(Carrera carrera) { return new CarreraResponse(carrera.getId(), carrera.getNombre(), carrera.getEstado().name()); }
}

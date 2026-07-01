package com.utp.recommends.admin.dashboard.service;

import com.utp.recommends.admin.dashboard.dto.response.AdminDashboardResponse;
import com.utp.recommends.domain.enums.EstadoResena;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.EstadoSolicitud;
import com.utp.recommends.domain.enums.EstadoUsuario;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import com.utp.recommends.repository.CursoRepository;
import com.utp.recommends.repository.DocenteRepository;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.repository.SolicitudRepository;
import com.utp.recommends.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ResenaRepository resenaRepository;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final DocenteRepository docenteRepository;
    private final CriterioCalificacionRepository criterioRepository;

    public AdminDashboardServiceImpl(
        ResenaRepository resenaRepository,
        SolicitudRepository solicitudRepository,
        UsuarioRepository usuarioRepository,
        CursoRepository cursoRepository,
        DocenteRepository docenteRepository,
        CriterioCalificacionRepository criterioRepository
    ) {
        this.resenaRepository = resenaRepository;
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.docenteRepository = docenteRepository;
        this.criterioRepository = criterioRepository;
    }

    @Override
    public AdminDashboardResponse getDashboard() {
        return new AdminDashboardResponse(
            resenaRepository.countByEstado(EstadoResena.PENDIENTE),
            solicitudRepository.countByEstado(EstadoSolicitud.PENDIENTE),
            usuarioRepository.countByEstado(EstadoUsuario.ACTIVO),
            cursoRepository.countByEstado(EstadoSimple.ACTIVO),
            docenteRepository.countByEstado(EstadoSimple.ACTIVO),
            criterioRepository.countByEstado(EstadoSimple.ACTIVO)
        );
    }
}

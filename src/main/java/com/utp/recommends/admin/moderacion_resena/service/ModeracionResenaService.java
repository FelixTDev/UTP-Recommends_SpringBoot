package com.utp.recommends.admin.moderacion_resena.service;

import com.utp.recommends.admin.moderacion_resena.dto.response.ModeracionResenaResponse;
import java.util.List;

public interface ModeracionResenaService {
    List<ModeracionResenaResponse> pendientes(String estado);
    ModeracionResenaResponse aprobar(Long id);
    ModeracionResenaResponse rechazar(Long id, String motivoRechazo);
    ModeracionResenaResponse ocultar(Long id);
}

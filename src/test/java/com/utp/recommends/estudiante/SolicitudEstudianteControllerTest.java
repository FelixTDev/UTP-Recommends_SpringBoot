package com.utp.recommends.estudiante;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.recommends.common.exception.GlobalExceptionHandler;
import com.utp.recommends.common.exception.ResourceNotFoundException;
import com.utp.recommends.estudiante.solicitud.controller.SolicitudEstudianteController;
import com.utp.recommends.estudiante.solicitud.dto.response.SolicitudResponse;
import com.utp.recommends.estudiante.solicitud.service.SolicitudEstudianteService;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SolicitudEstudianteControllerTest {

    private MockMvc mockMvc;
    private SolicitudEstudianteService service;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(SolicitudEstudianteService.class);
        mockMvc = MockMvcBuilders
            .standaloneSetup(new SolicitudEstudianteController(service))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void exposesSeparatedTeacherFieldsInCreateResponse() throws Exception {
        when(service.crear(any())).thenReturn(new SolicitudResponse(
            1L,
            "DOCENTE_NUEVO",
            "PENDIENTE",
            null,
            "Armando|Paredes",
            "Armando",
            "Paredes",
            "Comentario valido",
            null,
            OffsetDateTime.parse("2026-07-05T12:00:00Z")
        ));

        mockMvc.perform(post("/api/estudiante/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tipo": "DOCENTE_NUEVO",
                      "nombresDocenteSugerido": "Armando",
                      "apellidosDocenteSugerido": "Paredes",
                      "comentario": "Comentario valido"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombreDocenteSugerido").value("Armando|Paredes"))
            .andExpect(jsonPath("$.nombresDocenteSugerido").value("Armando"))
            .andExpect(jsonPath("$.apellidosDocenteSugerido").value("Paredes"));
    }
}

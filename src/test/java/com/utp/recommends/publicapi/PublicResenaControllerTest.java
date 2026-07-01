package com.utp.recommends.publicapi;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.recommends.publicapi.controller.PublicResenaController;
import com.utp.recommends.publicapi.dto.response.PublicResenaResponse;
import com.utp.recommends.publicapi.service.PublicResenaService;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PublicResenaControllerTest {

    @Mock private PublicResenaService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new PublicResenaController(service))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper().findAndRegisterModules()))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void anonymousReviewDoesNotExposeStudentIdentity() throws Exception {
        when(service.listar(org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.any()))
            .thenReturn(new PageImpl<>(
                List.of(new PublicResenaResponse(1L, "POO", "Luis Perez", "Comentario aprobado", true, null, OffsetDateTime.now(), List.of())),
                PageRequest.of(0, 20),
                1
            ));

        mockMvc.perform(get("/api/public/resenas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].esAnonimo").value(true))
            .andExpect(jsonPath("$.content[0].nombreEstudianteVisible").doesNotExist());
    }
}

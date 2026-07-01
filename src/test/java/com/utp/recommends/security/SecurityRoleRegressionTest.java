package com.utp.recommends.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.utp.recommends.repository.CarreraRepository;
import com.utp.recommends.repository.CriterioCalificacionRepository;
import com.utp.recommends.repository.CursoDocenteRepository;
import com.utp.recommends.repository.CursoRepository;
import com.utp.recommends.repository.DocenteRepository;
import com.utp.recommends.repository.EstudianteRepository;
import com.utp.recommends.repository.ResenaCalificacionRepository;
import com.utp.recommends.repository.ResenaRepository;
import com.utp.recommends.repository.SolicitudRepository;
import com.utp.recommends.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude="
        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
@AutoConfigureMockMvc
class SecurityRoleRegressionTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private UsuarioRepository usuarioRepository;
    @MockBean private EstudianteRepository estudianteRepository;
    @MockBean private CarreraRepository carreraRepository;
    @MockBean private DocenteRepository docenteRepository;
    @MockBean private CursoRepository cursoRepository;
    @MockBean private CursoDocenteRepository cursoDocenteRepository;
    @MockBean private CriterioCalificacionRepository criterioCalificacionRepository;
    @MockBean private ResenaRepository resenaRepository;
    @MockBean private ResenaCalificacionRepository resenaCalificacionRepository;
    @MockBean private SolicitudRepository solicitudRepository;

    @Test
    void estudianteRoutesRejectAdminRole() throws Exception {
        mockMvc.perform(get("/api/estudiante/resenas/mis-resenas").with(user("admin").roles("ADMIN")))
            .andExpect(status().isForbidden());
    }

    @Test
    void authMeRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized());
    }
}

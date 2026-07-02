package com.utp.recommends.admin;

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
import com.utp.recommends.security.CustomUserDetailsService;
import com.utp.recommends.security.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude="
        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
@AutoConfigureMockMvc
class AdminSecurityControllerTest {

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
    @MockBean private JwtService jwtService;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Test
    void adminRoutesRejectWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/carreras"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void adminRoutesRejectStudentRole() throws Exception {
        mockMvc.perform(get("/api/admin/carreras").with(user("student").roles("ESTUDIANTE")))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminRoutesReturnUnauthorizedWhenJwtIsMalformed() throws Exception {
        when(jwtService.extractUsername("bad-token")).thenThrow(new MalformedJwtException("bad token"));

        mockMvc.perform(get("/api/admin/carreras").header("Authorization", "Bearer bad-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void adminRoutesReturnUnauthorizedWhenJwtUserDoesNotExist() throws Exception {
        when(jwtService.extractUsername("ghost-token")).thenReturn("admin@utp.edu.pe");
        when(customUserDetailsService.loadUserByUsername("admin@utp.edu.pe"))
            .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/api/admin/carreras").header("Authorization", "Bearer ghost-token"))
            .andExpect(status().isUnauthorized());
    }
}

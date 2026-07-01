package com.utp.recommends.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.utp.recommends.domain.entity.Carrera;
import com.utp.recommends.domain.entity.Curso;
import com.utp.recommends.domain.entity.CursoDocente;
import com.utp.recommends.domain.entity.Docente;
import com.utp.recommends.domain.entity.Estudiante;
import com.utp.recommends.domain.entity.Resena;
import com.utp.recommends.domain.entity.ResenaCalificacion;
import com.utp.recommends.domain.entity.Usuario;
import com.utp.recommends.domain.enums.EstadoCarrera;
import com.utp.recommends.domain.enums.EstadoResena;
import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.EstadoUsuario;
import com.utp.recommends.domain.enums.RolUsuario;
import com.utp.recommends.domain.enums.TipoCurso;
import com.utp.recommends.support.AbstractContainerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
class ConstraintIntegrationTest extends AbstractContainerIntegrationTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CarreraRepository carreraRepository;
    @Autowired private EstudianteRepository estudianteRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private DocenteRepository docenteRepository;
    @Autowired private CursoDocenteRepository cursoDocenteRepository;
    @Autowired private CriterioCalificacionRepository criterioRepository;
    @Autowired private ResenaRepository resenaRepository;
    @Autowired private ResenaCalificacionRepository resenaCalificacionRepository;

    @Test
    void enforcesUniqueEmail() {
        Usuario first = baseUsuario("U12345678@utp.edu.pe");
        usuarioRepository.saveAndFlush(first);

        Usuario duplicate = baseUsuario("U12345678@utp.edu.pe");

        assertThatThrownBy(() -> usuarioRepository.saveAndFlush(duplicate))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void enforcesUniqueCursoDocente() {
        Carrera carrera = carreraRepository.saveAndFlush(baseCarrera("Ingenieria de Software"));
        Curso curso = cursoRepository.saveAndFlush(baseCurso("Arquitectura de Software", carrera));
        Docente docente = docenteRepository.saveAndFlush(baseDocente("Ana", "Perez"));

        CursoDocente first = new CursoDocente();
        first.setCurso(curso);
        first.setDocente(docente);
        first.setEstado(EstadoSimple.ACTIVO);
        cursoDocenteRepository.saveAndFlush(first);

        CursoDocente duplicate = new CursoDocente();
        duplicate.setCurso(curso);
        duplicate.setDocente(docente);
        duplicate.setEstado(EstadoSimple.ACTIVO);

        assertThatThrownBy(() -> cursoDocenteRepository.saveAndFlush(duplicate))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void enforcesUniqueClaveActiva() {
        Carrera carrera = carreraRepository.saveAndFlush(baseCarrera("Ingenieria Civil"));
        Usuario usuario = usuarioRepository.saveAndFlush(baseUsuario("U87654321@utp.edu.pe"));
        Estudiante estudiante = new Estudiante();
        estudiante.setUsuario(usuario);
        estudiante.setCodigoEstudiante("U87654321");
        estudiante.setCarrera(carrera);
        estudiante = estudianteRepository.saveAndFlush(estudiante);

        Curso curso = cursoRepository.saveAndFlush(baseCurso("Fisica General", null));
        Docente docente = docenteRepository.saveAndFlush(baseDocente("Luis", "Quispe"));
        CursoDocente cursoDocente = new CursoDocente();
        cursoDocente.setCurso(curso);
        cursoDocente.setDocente(docente);
        cursoDocente.setEstado(EstadoSimple.ACTIVO);
        cursoDocente = cursoDocenteRepository.saveAndFlush(cursoDocente);

        Resena first = new Resena();
        first.setEstudiante(estudiante);
        first.setCursoDocente(cursoDocente);
        first.setComentario("Comentario valido numero uno");
        first.setEsAnonimo(false);
        first.setEstado(EstadoResena.PENDIENTE);
        first.setVersion(1);
        resenaRepository.saveAndFlush(first);

        Resena duplicate = new Resena();
        duplicate.setEstudiante(estudiante);
        duplicate.setCursoDocente(cursoDocente);
        duplicate.setComentario("Comentario valido numero dos");
        duplicate.setEsAnonimo(true);
        duplicate.setEstado(EstadoResena.APROBADA);
        duplicate.setVersion(1);

        assertThatThrownBy(() -> resenaRepository.saveAndFlush(duplicate))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void enforcesPuntajeRange() {
        Carrera carrera = carreraRepository.saveAndFlush(baseCarrera("Administracion Hotelera"));
        Usuario usuario = usuarioRepository.saveAndFlush(baseUsuario("U11223344@utp.edu.pe"));
        Estudiante estudiante = new Estudiante();
        estudiante.setUsuario(usuario);
        estudiante.setCodigoEstudiante("U11223344");
        estudiante.setCarrera(carrera);
        estudiante = estudianteRepository.saveAndFlush(estudiante);

        Curso curso = cursoRepository.saveAndFlush(baseCurso("Estadistica", null));
        Docente docente = docenteRepository.saveAndFlush(baseDocente("Marta", "Lopez"));
        CursoDocente cursoDocente = new CursoDocente();
        cursoDocente.setCurso(curso);
        cursoDocente.setDocente(docente);
        cursoDocente.setEstado(EstadoSimple.ACTIVO);
        cursoDocente = cursoDocenteRepository.saveAndFlush(cursoDocente);

        Resena resena = new Resena();
        resena.setEstudiante(estudiante);
        resena.setCursoDocente(cursoDocente);
        resena.setComentario("Comentario valido para puntaje");
        resena.setEsAnonimo(false);
        resena.setEstado(EstadoResena.PENDIENTE);
        resena.setVersion(1);
        resena = resenaRepository.saveAndFlush(resena);

        ResenaCalificacion calificacion = new ResenaCalificacion();
        calificacion.setResena(resena);
        calificacion.setCriterio(criterioRepository.findAll().getFirst());
        calificacion.setPuntaje(6);

        assertThatThrownBy(() -> resenaCalificacionRepository.saveAndFlush(calificacion))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Usuario baseUsuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPasswordHash("$2a$10$abcdefghijklmnopqrstuv1234567890123456789012345678901234");
        usuario.setNombres("Juan");
        usuario.setApellidos("Perez");
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuario;
    }

    private Carrera baseCarrera(String nombre) {
        Carrera carrera = new Carrera();
        carrera.setNombre(nombre);
        carrera.setEstado(EstadoCarrera.ACTIVA);
        return carrera;
    }

    private Curso baseCurso(String nombre, Carrera carrera) {
        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setTipo(carrera == null ? TipoCurso.GENERAL : TipoCurso.CARRERA);
        curso.setCarrera(carrera);
        curso.setEstado(EstadoSimple.ACTIVO);
        return curso;
    }

    private Docente baseDocente(String nombres, String apellidos) {
        Docente docente = new Docente();
        docente.setNombres(nombres);
        docente.setApellidos(apellidos);
        docente.setEmail(nombres.toLowerCase() + "." + apellidos.toLowerCase() + "@utp.edu.pe");
        docente.setEstado(EstadoSimple.ACTIVO);
        return docente;
    }
}

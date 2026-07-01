package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.EstadoResena;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resena")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_docente_id", nullable = false)
    private CursoDocente cursoDocente;
    @Column(name = "comentario", nullable = false, columnDefinition = "TEXT")
    private String comentario;
    @Column(name = "es_anonimo", nullable = false)
    private boolean esAnonimo;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoResena estado;
    @Column(name = "motivo_rechazo", length = 255)
    private String motivoRechazo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_moderador_id")
    private Usuario adminModerador;
    @Column(name = "version", nullable = false)
    private Integer version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resena_anterior_id")
    private Resena resenaAnterior;
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
    @Column(name = "fecha_moderacion")
    private OffsetDateTime fechaModeracion;
    @Column(name = "clave_activa", insertable = false, updatable = false)
    private String claveActiva;
    @OneToMany(mappedBy = "resena", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResenaCalificacion> calificaciones = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public CursoDocente getCursoDocente() { return cursoDocente; }
    public void setCursoDocente(CursoDocente cursoDocente) { this.cursoDocente = cursoDocente; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public boolean isEsAnonimo() { return esAnonimo; }
    public void setEsAnonimo(boolean esAnonimo) { this.esAnonimo = esAnonimo; }
    public EstadoResena getEstado() { return estado; }
    public void setEstado(EstadoResena estado) { this.estado = estado; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public Usuario getAdminModerador() { return adminModerador; }
    public void setAdminModerador(Usuario adminModerador) { this.adminModerador = adminModerador; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Resena getResenaAnterior() { return resenaAnterior; }
    public void setResenaAnterior(Resena resenaAnterior) { this.resenaAnterior = resenaAnterior; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public OffsetDateTime getFechaModeracion() { return fechaModeracion; }
    public void setFechaModeracion(OffsetDateTime fechaModeracion) { this.fechaModeracion = fechaModeracion; }
    public String getClaveActiva() { return claveActiva; }
    public List<ResenaCalificacion> getCalificaciones() { return calificaciones; }
    public void setCalificaciones(List<ResenaCalificacion> calificaciones) { this.calificaciones = calificaciones; }
}

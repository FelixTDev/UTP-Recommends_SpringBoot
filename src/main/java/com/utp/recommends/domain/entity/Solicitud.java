package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.EstadoSolicitud;
import com.utp.recommends.domain.enums.TipoSolicitud;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoSolicitud tipo;
    @Column(name = "nombre_curso_sugerido", length = 150)
    private String nombreCursoSugerido;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_sugerida_id")
    private Carrera carreraSugerida;
    @Column(name = "nombre_docente_sugerido", length = 150)
    private String nombreDocenteSugerido;
    @Column(name = "comentario", nullable = false, columnDefinition = "TEXT")
    private String comentario;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSolicitud estado;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Usuario admin;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resena_generada_id")
    private Resena resenaGenerada;
    @Column(name = "motivo_rechazo", length = 255)
    private String motivoRechazo;
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
    @Column(name = "fecha_resolucion")
    private OffsetDateTime fechaResolucion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public TipoSolicitud getTipo() { return tipo; }
    public void setTipo(TipoSolicitud tipo) { this.tipo = tipo; }
    public String getNombreCursoSugerido() { return nombreCursoSugerido; }
    public void setNombreCursoSugerido(String nombreCursoSugerido) { this.nombreCursoSugerido = nombreCursoSugerido; }
    public Carrera getCarreraSugerida() { return carreraSugerida; }
    public void setCarreraSugerida(Carrera carreraSugerida) { this.carreraSugerida = carreraSugerida; }
    public String getNombreDocenteSugerido() { return nombreDocenteSugerido; }
    public void setNombreDocenteSugerido(String nombreDocenteSugerido) { this.nombreDocenteSugerido = nombreDocenteSugerido; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public Usuario getAdmin() { return admin; }
    public void setAdmin(Usuario admin) { this.admin = admin; }
    public Resena getResenaGenerada() { return resenaGenerada; }
    public void setResenaGenerada(Resena resenaGenerada) { this.resenaGenerada = resenaGenerada; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public OffsetDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(OffsetDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}

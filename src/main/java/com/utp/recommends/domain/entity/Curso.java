package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.EstadoSimple;
import com.utp.recommends.domain.enums.TipoCurso;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCurso tipo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSimple estado;
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoCurso getTipo() { return tipo; }
    public void setTipo(TipoCurso tipo) { this.tipo = tipo; }
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public EstadoSimple getEstado() { return estado; }
    public void setEstado(EstadoSimple estado) { this.estado = estado; }
}

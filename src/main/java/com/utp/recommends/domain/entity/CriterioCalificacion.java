package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.EstadoSimple;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "criterio_calificacion")
public class CriterioCalificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name = "descripcion", length = 255)
    private String descripcion;
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
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstadoSimple getEstado() { return estado; }
    public void setEstado(EstadoSimple estado) { this.estado = estado; }
}

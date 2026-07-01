package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.EstadoSimple;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "docente")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;
    @Column(name = "email", length = 150)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSimple estado;
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public EstadoSimple getEstado() { return estado; }
    public void setEstado(EstadoSimple estado) { this.estado = estado; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}

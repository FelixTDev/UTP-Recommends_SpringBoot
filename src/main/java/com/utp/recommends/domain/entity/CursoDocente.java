package com.utp.recommends.domain.entity;

import com.utp.recommends.domain.enums.EstadoSimple;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "curso_docente")
public class CursoDocente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSimple estado;
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public Docente getDocente() { return docente; }
    public void setDocente(Docente docente) { this.docente = docente; }
    public EstadoSimple getEstado() { return estado; }
    public void setEstado(EstadoSimple estado) { this.estado = estado; }
}

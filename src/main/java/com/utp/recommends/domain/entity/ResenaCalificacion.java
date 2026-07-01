package com.utp.recommends.domain.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "resena_calificacion")
public class ResenaCalificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resena_id", nullable = false)
    private Resena resena;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterio_id", nullable = false)
    private CriterioCalificacion criterio;
    @Column(name = "puntaje", nullable = false)
    private Byte puntaje;
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Resena getResena() { return resena; }
    public void setResena(Resena resena) { this.resena = resena; }
    public CriterioCalificacion getCriterio() { return criterio; }
    public void setCriterio(CriterioCalificacion criterio) { this.criterio = criterio; }
    public Byte getPuntaje() { return puntaje; }
    public void setPuntaje(Byte puntaje) { this.puntaje = puntaje; }
}

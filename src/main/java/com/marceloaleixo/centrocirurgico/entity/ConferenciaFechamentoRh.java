package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "conferencia_fechamento_rh", uniqueConstraints = @UniqueConstraint(
        name = "uk_conferencia_rh_periodo_colaborador",
        columnNames = {"periodo_inicio", "periodo_fim", "colaborador_id"}))
public class ConferenciaFechamentoRh {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fim", nullable = false)
    private LocalDate periodoFim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @Column(length = 1000)
    private String observacao;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    @PrePersist @PreUpdate
    void atualizarData() { atualizadoEm = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDate v) { periodoInicio = v; }
    public LocalDate getPeriodoFim() { return periodoFim; }
    public void setPeriodoFim(LocalDate v) { periodoFim = v; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador v) { colaborador = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String v) { observacao = v; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}

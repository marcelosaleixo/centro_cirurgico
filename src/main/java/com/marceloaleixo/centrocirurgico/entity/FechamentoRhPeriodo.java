package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fechamento_rh_periodo", uniqueConstraints = @UniqueConstraint(
        name = "uk_fechamento_rh_periodo", columnNames = {"periodo_inicio", "periodo_fim"}))
public class FechamentoRhPeriodo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fim", nullable = false)
    private LocalDate periodoFim;

    @Column(nullable = false, length = 20)
    private String status = "ABERTO";

    @Column(name = "fechado_em")
    private OffsetDateTime fechadoEm;

    @Column(name = "observacao_fechamento", length = 1000)
    private String observacaoFechamento;

    public Long getId() { return id; }
    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDate v) { periodoInicio = v; }
    public LocalDate getPeriodoFim() { return periodoFim; }
    public void setPeriodoFim(LocalDate v) { periodoFim = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public OffsetDateTime getFechadoEm() { return fechadoEm; }
    public void setFechadoEm(OffsetDateTime v) { fechadoEm = v; }
    public String getObservacaoFechamento() { return observacaoFechamento; }
    public void setObservacaoFechamento(String v) { observacaoFechamento = v; }
    public boolean isFechado() { return "FECHADO".equals(status); }
}

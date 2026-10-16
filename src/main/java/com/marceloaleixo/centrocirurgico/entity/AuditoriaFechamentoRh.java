package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auditoria_fechamento_rh")
public class AuditoriaFechamentoRh {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "periodo_inicio", nullable = false) private LocalDate periodoInicio;
    @Column(name = "periodo_fim", nullable = false) private LocalDate periodoFim;
    @Column(name = "colaborador_nome", length = 180) private String colaboradorNome;
    @Column(name = "acao", nullable = false, length = 40) private String acao;
    @Column(name = "status_anterior", length = 30) private String statusAnterior;
    @Column(name = "status_novo", length = 30) private String statusNovo;
    @Column(name = "detalhe", length = 1000) private String detalhe;
    @Column(name = "responsavel", nullable = false, length = 120) private String responsavel = "sistema";
    @Column(name = "criado_em", nullable = false) private OffsetDateTime criadoEm;

    @PrePersist void antesDeSalvar() { if (criadoEm == null) criadoEm = OffsetDateTime.now(); }
    public Long getId() { return id; }
    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDate v) { periodoInicio = v; }
    public LocalDate getPeriodoFim() { return periodoFim; }
    public void setPeriodoFim(LocalDate v) { periodoFim = v; }
    public String getColaboradorNome() { return colaboradorNome; }
    public void setColaboradorNome(String v) { colaboradorNome = v; }
    public String getAcao() { return acao; }
    public void setAcao(String v) { acao = v; }
    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String v) { statusAnterior = v; }
    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String v) { statusNovo = v; }
    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String v) { detalhe = v; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String v) { responsavel = v; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime v) { criadoEm = v; }
}

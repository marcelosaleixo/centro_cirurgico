package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import com.marceloaleixo.centrocirurgico.enums.StatusFerias;

@Entity
@Table(name = "ferias")
public class Ferias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusFerias status = StatusFerias.SOLICITADA;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovado_por_id")
    private Colaborador aprovadoPor;
    @Column(length = 500)
    private String observacao;
    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

    public Ferias() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public Colaborador getColaborador() { return this.colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public LocalDate getDataInicio() { return this.dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return this.dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public StatusFerias getStatus() { return this.status; }
    public void setStatus(StatusFerias status) { this.status = status; }
    public Colaborador getAprovadoPor() { return this.aprovadoPor; }
    public void setAprovadoPor(Colaborador aprovadoPor) { this.aprovadoPor = aprovadoPor; }
    public String getObservacao() { return this.observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public OffsetDateTime getCriadoEm() { return this.criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}

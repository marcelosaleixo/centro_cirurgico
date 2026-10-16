package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import com.marceloaleixo.centrocirurgico.enums.StatusTrocaPlantao;

@Entity
@Table(name = "troca_plantao")
public class TrocaPlantao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Colaborador solicitante;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aceitante_id", nullable = false)
    private Colaborador aceitante;
    @Column(name = "data_plantao_original", nullable = false)
    private LocalDate dataPlantaoOriginal;
    @Column(name = "data_plantao_destino")
    private LocalDate dataPlantaoDestino;
    @Column(nullable = false, length = 1000)
    private String motivo;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_aprovacao", nullable = false, length = 20)
    private StatusTrocaPlantao statusAprovacao = StatusTrocaPlantao.PENDENTE;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovado_por_id")
    private Colaborador aprovadoPor;
    @Column(name = "data_solicitacao", insertable = false, updatable = false)
    private OffsetDateTime dataSolicitacao;
    @Column(name = "data_decisao")
    private OffsetDateTime dataDecisao;

    public TrocaPlantao() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public Colaborador getSolicitante() { return this.solicitante; }
    public void setSolicitante(Colaborador solicitante) { this.solicitante = solicitante; }
    public Colaborador getAceitante() { return this.aceitante; }
    public void setAceitante(Colaborador aceitante) { this.aceitante = aceitante; }
    public LocalDate getDataPlantaoOriginal() { return this.dataPlantaoOriginal; }
    public void setDataPlantaoOriginal(LocalDate dataPlantaoOriginal) { this.dataPlantaoOriginal = dataPlantaoOriginal; }
    public LocalDate getDataPlantaoDestino() { return this.dataPlantaoDestino; }
    public void setDataPlantaoDestino(LocalDate dataPlantaoDestino) { this.dataPlantaoDestino = dataPlantaoDestino; }
    public String getMotivo() { return this.motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public StatusTrocaPlantao getStatusAprovacao() { return this.statusAprovacao; }
    public void setStatusAprovacao(StatusTrocaPlantao statusAprovacao) { this.statusAprovacao = statusAprovacao; }
    public Colaborador getAprovadoPor() { return this.aprovadoPor; }
    public void setAprovadoPor(Colaborador aprovadoPor) { this.aprovadoPor = aprovadoPor; }
    public OffsetDateTime getDataSolicitacao() { return this.dataSolicitacao; }
    public void setDataSolicitacao(OffsetDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
    public OffsetDateTime getDataDecisao() { return this.dataDecisao; }
    public void setDataDecisao(OffsetDateTime dataDecisao) { this.dataDecisao = dataDecisao; }
}

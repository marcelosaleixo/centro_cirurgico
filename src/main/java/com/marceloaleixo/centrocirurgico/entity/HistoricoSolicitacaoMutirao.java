package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_solicitacao_mutirao", indexes = {
    @Index(name = "idx_hist_solicitacao_data", columnList = "solicitacao_id, ocorrido_em")
})
public class HistoricoSolicitacaoMutirao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitacao_id", nullable = false)
    private Long solicitacaoId;

    @Column(name = "acao", nullable = false, length = 60)
    private String acao;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "responsavel", length = 160)
    private String responsavel;

    @Column(name = "status_anterior", length = 24)
    private String statusAnterior;

    @Column(name = "status_novo", length = 24)
    private String statusNovo;

    @Column(name = "ocorrido_em", nullable = false)
    private LocalDateTime ocorridoEm = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getSolicitacaoId() { return solicitacaoId; }
    public void setSolicitacaoId(Long solicitacaoId) { this.solicitacaoId = solicitacaoId; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }
    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }
    public LocalDateTime getOcorridoEm() { return ocorridoEm; }
    public void setOcorridoEm(LocalDateTime ocorridoEm) { this.ocorridoEm = ocorridoEm; }
}

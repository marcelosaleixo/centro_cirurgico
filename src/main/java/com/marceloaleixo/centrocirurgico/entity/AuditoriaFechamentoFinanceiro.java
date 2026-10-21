package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auditoria_fechamento_financeiro", indexes = {
    @Index(name = "idx_auditoria_fin_cirurgia_data", columnList = "cirurgia_id,registrado_em")
})
public class AuditoriaFechamentoFinanceiro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cirurgia_id", nullable = false) private Cirurgia cirurgia;
    @Column(nullable = false, length = 30) private String acao;
    @Column(nullable = false, length = 120) private String responsavel;
    @Column(nullable = false, columnDefinition = "text") private String detalhes;
    @Column(name = "registrado_em", nullable = false, insertable = false, updatable = false) private OffsetDateTime registradoEm;
    public Long getId(){return id;} public Cirurgia getCirurgia(){return cirurgia;} public void setCirurgia(Cirurgia v){cirurgia=v;}
    public String getAcao(){return acao;} public void setAcao(String v){acao=v;}
    public String getResponsavel(){return responsavel;} public void setResponsavel(String v){responsavel=v;}
    public String getDetalhes(){return detalhes;} public void setDetalhes(String v){detalhes=v;}
    public OffsetDateTime getRegistradoEm(){return registradoEm;}
}

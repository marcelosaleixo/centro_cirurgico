package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_item_solicitacao_mutirao", indexes = {
    @Index(name = "idx_aud_item_solicitacao_data", columnList = "solicitacao_id, ocorrido_em")
})
public class AuditoriaItemSolicitacaoMutirao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "solicitacao_id", nullable = false) private Long solicitacaoId;
    @Column(name = "item_solicitacao_id", nullable = false) private Long itemSolicitacaoId;
    @Column(name = "item_descricao", nullable = false, length = 160) private String itemDescricao;
    @Column(name = "campo_alterado", nullable = false, length = 40) private String campoAlterado;
    @Column(name = "valor_anterior", precision = 12, scale = 3) private BigDecimal valorAnterior;
    @Column(name = "valor_novo", precision = 12, scale = 3) private BigDecimal valorNovo;
    @Column(name = "responsavel", length = 160) private String responsavel;
    @Column(name = "ocorrido_em", nullable = false) private LocalDateTime ocorridoEm = LocalDateTime.now();

    public Long getId(){return id;}
    public Long getSolicitacaoId(){return solicitacaoId;} public void setSolicitacaoId(Long v){solicitacaoId=v;}
    public Long getItemSolicitacaoId(){return itemSolicitacaoId;} public void setItemSolicitacaoId(Long v){itemSolicitacaoId=v;}
    public String getItemDescricao(){return itemDescricao;} public void setItemDescricao(String v){itemDescricao=v;}
    public String getCampoAlterado(){return campoAlterado;} public void setCampoAlterado(String v){campoAlterado=v;}
    public BigDecimal getValorAnterior(){return valorAnterior;} public void setValorAnterior(BigDecimal v){valorAnterior=v;}
    public BigDecimal getValorNovo(){return valorNovo;} public void setValorNovo(BigDecimal v){valorNovo=v;}
    public String getResponsavel(){return responsavel;} public void setResponsavel(String v){responsavel=v;}
    public LocalDateTime getOcorridoEm(){return ocorridoEm;} public void setOcorridoEm(LocalDateTime v){ocorridoEm=v;}
}
